package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import request_result.*;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import websocket.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Client extends EscapeSequences implements NotificationHandler{
    private String visitorName = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private boolean connected = false;
    private boolean observing = false;
    private boolean checkMate = false;
    boolean resigned;
    private String authToken;
    private ChessGame currentGameState;
    private ChessGame.TeamColor playerColor;
    private int joinedGameID;

    private HashMap<String, Integer> chessLetters = new HashMap<>() {{
        put("a", 1);
        put("b", 2);
        put("c", 3);
        put("d", 4);
        put("e", 5);
        put("f", 6);
        put("g", 7);
        put("h", 8);
    }};
    private HashMap<String, ChessPiece.PieceType> promotions = new HashMap<>(){{
        put("queen", ChessPiece.PieceType.QUEEN);
        put("bishop", ChessPiece.PieceType.BISHOP);
        put("knight", ChessPiece.PieceType.KNIGHT);
        put("rook", ChessPiece.PieceType.ROOK);
    }};
    private HashMap<Integer, GameResult> gameList = new HashMap<>();
    private WebSocketFacade ws;
    private String url;

    public Client(String url) {
        this.url = url;
        server = new ServerFacade(url);
    }
    public void run() {
        System.out.println(SET_TEXT_BOLD + "♕ Welcome to 240 chess. Type Help to get started. ♕" + "\u001b[0m");

        Scanner scanner = new Scanner(System.in);
        var input = "";

        while (!input.equals("quit")) {
            if (state == State.SIGNEDOUT && !connected) {
                System.out.print("\n[LOGGED_OUT] >>> ");
            } else if (state == State.SIGNEDIN && !connected){
                System.out.print("\n[LOGGED_IN] >>> ");
            }


            String line = scanner.nextLine();

            try {
                String result = eval(line);
                if (Objects.equals(result, "quit")) {
                    System.out.println("Thanks for playing. Goodbye.\n");
                    return;
                }
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public enum State {
        SIGNEDOUT,
        SIGNEDIN
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must login\n");
        }
    }

    private void assertNotConnected() throws ResponseException {
        if (connected) {
            throw new ResponseException(400, "You must leave the current game first\n");
        }
    }

    private void assertConnected() throws ResponseException {
        if (!connected) {
            throw new ResponseException(400, "You must join a game first\n");
        }
    }

    private void assertNotObserving() throws ResponseException {
        if (observing) {
            throw new ResponseException(400, "You cannot do that as an observer\n");
        }
    }

    private void assertNotResigned() throws ResponseException {
        if (resigned) {
            throw new ResponseException(400, "Game was resigned. Command not allowed.\n");
        }
    }

    private void assertNotCheckMate() throws ResponseException {
        if (checkMate) {
            throw new ResponseException(400, "You are in CheckMate, the only option left is to resign. Better luck next time!\n");
        }
    }

    private String eval(String input){
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "redraw" -> redrawBoard();
                case "highlight" -> highlightMoves(params);
                case "move" -> makeMove(params);
                case "resign" -> resign();
                case "leave" -> leave();
                case "delete_all_data" -> clear();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
    public String register(String... params) throws ResponseException {
        if (state == State.SIGNEDIN) {
            return "If you would like to register a new user, please logout first.\n";
        }
        assertNotConnected();
        if (params.length == 3) {
            state = State.SIGNEDIN;
            visitorName = params[0];
            UserResult result = server.register(new UserData(params[0],params[1], params[2]));
            authToken = result.authToken();
            return String.format("You are logged in as %s. Please type Help for commands.\n", visitorName);
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>\n");
    }

    public String login(String... params) throws ResponseException {
        if (state == State.SIGNEDIN) {
            return "Already signed in. If you wish to login as a different user, please logout first.\n";
        }
        assertNotConnected();
        if (params.length == 2) {
            try {
                UserResult result = server.login(new LoginRequest(params[0], params[1]));
                authToken = result.authToken();
                state = State.SIGNEDIN;
                visitorName = params[0];
                return String.format("Successfully logged in. Welcome %s. Please type Help for commands. \n", visitorName);
            } catch (ResponseException e) {
                return e.getMessage();
            }

        }
        throw new ResponseException(400, "Expected: <username> <password>\n");
    }

    public String logout() throws ResponseException {
        assertNotConnected();
        state = State.SIGNEDOUT;
        visitorName = "";
        server.logout(authToken);
        authToken = "";
        return "Successfully logged out\n";
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        assertNotConnected();
        if (params.length == 1) {
            server.create(new CreateGameRequest(params[0]), authToken);
            return "Successfully create a ChessGame.\n";
        }
        throw new ResponseException(400, "Expected: <gameName>\n");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        assertNotConnected();
        GameListResult result = server.list(authToken);

        String out = "Here is the list of games:\n";
        int i = 1;
        for (GameResult game : result.games()){
            out += i + ": ";// + game.toString() + "\n";
            out += String.format("gameName = %s, whiteUsername = %s, blackUsername = %s\n", game.gameName(),game.whiteUsername(),game.blackUsername());
            gameList.put(i , game);
            i++;
        }
        return out;
    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        assertNotConnected();
        if (params.length == 2) {
            if (!gameList.containsKey(Integer.parseInt(params[0]))){
                throw new ResponseException(400, "Game does not exist\n");
            }

            if (params[1].equalsIgnoreCase("WHITE")) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else {
                playerColor = ChessGame.TeamColor.BLACK;
            }

            if ((playerColor == ChessGame.TeamColor.WHITE && !Objects.equals(gameList.get(Integer.parseInt(params[0])).whiteUsername(), visitorName))
                    || (playerColor == ChessGame.TeamColor.BLACK &&!Objects.equals(gameList.get(Integer.parseInt(params[0])).blackUsername(), visitorName))){
                server.join(new JoinGameRequest(params[1].toUpperCase(), gameList.get(Integer.parseInt(params[0])).gameID()), authToken);
            }

            ws = new WebSocketFacade(url, this);
            ws.joinPlayer(authToken, gameList.get(Integer.parseInt(params[0])).gameID(), playerColor);

            System.out.printf("Successfully joined game #%s. Please type help for commands\n", params[0]);
            resigned = false;
            connected = true;
            joinedGameID = gameList.get(Integer.parseInt(params[0])).gameID();
            return "";
        }
        throw new ResponseException(400, "Expected: <gameID> [WHITE|BLACK]\n");
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        assertNotConnected();
        if (params.length == 1) {
            if (!gameList.containsKey(Integer.parseInt(params[0]))){
                throw new ResponseException(400, "Game does not exist\n");
            }

            GameData game = server.join(new JoinGameRequest(null, gameList.get(Integer.parseInt(params[0])).gameID()), authToken);
            playerColor = ChessGame.TeamColor.WHITE;
            ws = new WebSocketFacade(url, this);
            ws.joinObserver(authToken, gameList.get(Integer.parseInt(params[0])).gameID());

            System.out.printf("Observing game #%s\n", params[0]);
            connected = true;
            observing = true;
            joinedGameID = gameList.get(Integer.parseInt(params[0])).gameID();
            currentGameState = game.game();

            return "";
        }
        throw new ResponseException(400, "Expected: <gameID>\n");
    }

    public String redrawBoard() throws ResponseException {
        assertSignedIn();
        assertConnected();

        if (playerColor.equals(ChessGame.TeamColor.WHITE)){
            new ChessBoardUI(currentGameState).createWhiteChessBoard();
        } else if (playerColor.equals(ChessGame.TeamColor.BLACK)){
            new ChessBoardUI(currentGameState).createBlackChessBoard();
        }
        return "";
    }

    public String highlightMoves(String... params) throws ResponseException {
        assertSignedIn();
        assertConnected();
        assertNotObserving();
        if (params.length == 1 && params[0].length() == 2 && Character.isLetter(params[0].charAt(0)) && Character.isDigit(params[0].charAt(1))) {

            if (currentGameState.getBoard().getPiece(new ChessPosition(Integer.parseInt(params[0].substring(1,2)), chessLetters.get(params[0].substring(0,1)))) == null){
                throw new ResponseException(400, "Invalid Position\n");
            }

            if (playerColor.equals(ChessGame.TeamColor.WHITE)){
                new ChessBoardUI(currentGameState).whiteChessBoardHighlight(new ChessPosition(Integer.parseInt(params[0].substring(1,2)), chessLetters.get(params[0].substring(0,1))));
            } else if (playerColor.equals(ChessGame.TeamColor.BLACK)){
                new ChessBoardUI(currentGameState).blackChessBoardHighlight(new ChessPosition(Integer.parseInt(params[0].substring(1,2)), chessLetters.get(params[0].substring(0,1))));
            }

            return "";
        }
        throw new ResponseException(400, "Expected: <POSITION>\n");
    }

    public String makeMove(String... params) throws ResponseException {
        assertSignedIn();
        assertConnected();
        assertNotObserving();
        assertNotResigned();
        assertNotCheckMate();
        if ((params.length == 1 || params.length == 2) && params[0].length() == 4
                && Character.isLetter(params[0].charAt(0)) && Character.isLetter(params[0].charAt(2))
                && Character.isDigit(params[0].charAt(1)) && Character.isDigit(params[0].charAt(3))) {
            if (params.length == 2 && promotions.containsKey(params[1])) {
                throw new ResponseException(400, "Promotion invalid");
            }

            if (currentGameState.getBoard().getPiece(new ChessPosition(Integer.parseInt(params[0].substring(1,2)), chessLetters.get(params[0].substring(0,1)))) == null) {
                throw new ResponseException(400, "Invalid Move\n");
            }

            if (params.length == 1) {
                ws.makeMove(authToken, joinedGameID, new ChessMove(new ChessPosition(Integer.parseInt(params[0].substring(1,2)), chessLetters.get(params[0].substring(0,1))),
                        new ChessPosition(Integer.parseInt(params[0].substring(3)), chessLetters.get(params[0].substring(2,3))),null));
            } else {
                ws.makeMove(authToken, joinedGameID, new ChessMove(new ChessPosition(Integer.parseInt(params[0].substring(1,2)), chessLetters.get(params[0].substring(0,1))),
                        new ChessPosition(Integer.parseInt(params[0].substring(3)), chessLetters.get(params[0].substring(2,3))),promotions.get(params[1])));
            }

            System.out.println("Successful Move!");

            return "";
        }
        throw new ResponseException(400, "Expected: <MOVE> [PROMOTION | <empty>] e.g. b2b1 or b2b1 \"queen\"\n");
    }

    public String resign() throws ResponseException {
        assertSignedIn();
        assertConnected();
        assertNotObserving();

        ws.resign(authToken,joinedGameID);
        resigned = true;

        return "";
    }

    public String leave() throws ResponseException {
        assertSignedIn();
        assertConnected();
        ws.leave(authToken,joinedGameID);
        connected = false;
        observing = false;
        checkMate = false;
        resigned = false;
        currentGameState = null;
        playerColor = null;
        joinedGameID = 0;

        System.out.println("Successfully left the game. Please type help for commands");
        return "";
    }

    public String clear() throws ResponseException {
        server.clear();
        visitorName = null;
        state = State.SIGNEDOUT;
        connected = false;
        authToken = null;
        gameList.clear();
        return "Cleared.\n";
    }

    public String help() {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        if (state == State.SIGNEDOUT) {
            out.println(SET_TEXT_COLOR_BLUE + "  register <USERNAME> <PASSWORD> <EMAIL>" + "\u001b[0m" + " - to create an account");
            out.println(SET_TEXT_COLOR_BLUE + "  login <USERNAME> <PASSWORD>" + "\u001b[0m" + " - to play chess");
            out.println(SET_TEXT_COLOR_BLUE + "  quit" + "\u001b[0m" + " - playing chess");
            out.println(SET_TEXT_COLOR_BLUE + "  help" + "\u001b[0m" + " - with possible commands");
            return "";
        } else if (state == State.SIGNEDIN && connected && observing) {
            out.println(SET_TEXT_COLOR_BLUE + "  redraw" + "\u001b[0m" + " - chess board");
            out.println(SET_TEXT_COLOR_BLUE + "  leave" + "\u001b[0m" + " - the game");
            out.println(SET_TEXT_COLOR_BLUE + "  help" + "\u001b[0m" + " - with possible commands");
            return "";
        } else if (state == State.SIGNEDIN && connected) {
            out.println(SET_TEXT_COLOR_BLUE + "  redraw" + "\u001b[0m" + " - chess board");
            out.println(SET_TEXT_COLOR_BLUE + "  highlight <POSITION>" + "\u001b[0m" + " - legal moves of specified piece e.g. b2");
            out.println(SET_TEXT_COLOR_BLUE + "  move <POSITIONS> [PROMOTION | <empty>]" + "\u001b[0m" + " - on board e.g. b2b1 or b2b1 \"queen\".\n\t\t\tAccepted promotions: \"queen\", \"bishop\", \"knight\", and \"rook\" without quotes");
            out.println(SET_TEXT_COLOR_BLUE + "  resign" + "\u001b[0m" + " - the game");
            out.println(SET_TEXT_COLOR_BLUE + "  leave" + "\u001b[0m" + " - the game");
            out.println(SET_TEXT_COLOR_BLUE + "  help" + "\u001b[0m" + " - with possible commands");
            return "";
        }
        out.println(SET_TEXT_COLOR_BLUE + "  create <NAME>" + "\u001b[0m" + " - a game");
        out.println(SET_TEXT_COLOR_BLUE + "  list" + "\u001b[0m" + " - games");
        out.println(SET_TEXT_COLOR_BLUE + "  join <ID> [WHITE|BLACK]" + "\u001b[0m" + " - a game");
        out.println(SET_TEXT_COLOR_BLUE + "  observe <ID>" + "\u001b[0m" + " - a game");
        out.println(SET_TEXT_COLOR_BLUE + "  logout" + "\u001b[0m" + " - when you are done");
        out.println(SET_TEXT_COLOR_BLUE + "  quit" + "\u001b[0m" + " - playing chess");
        out.println(SET_TEXT_COLOR_BLUE + "  help" + "\u001b[0m" + " - with possible commands");
        return "";
    }

    public void notify(ServerMessage notification) {
        if (notification instanceof Notification) {
            System.out.println(((Notification) notification).getMessage());
            if (((Notification) notification).getMessage().endsWith("Game Over.\n")) {
                resigned = true;
            }
        } else if (notification instanceof Error) {
            System.out.println(SET_TEXT_COLOR_RED + ((Error) notification).getErrorMessage() + "\u001b[0m");
        } else if (notification instanceof LoadGame) {
            currentGameState = ((LoadGame) notification).getGame();
            if (playerColor.equals(ChessGame.TeamColor.WHITE)){
                new ChessBoardUI(((LoadGame) notification).getGame()).createWhiteChessBoard();
            } else if (playerColor.equals(ChessGame.TeamColor.BLACK)){
                new ChessBoardUI(((LoadGame) notification).getGame()).createBlackChessBoard();
            }

            if(currentGameState.isInCheckmate(ChessGame.TeamColor.WHITE) && currentGameState.isInStalemate(ChessGame.TeamColor.WHITE)){
                System.out.println("WHITE is in CheckMate\n");
                if (playerColor == ChessGame.TeamColor.WHITE) {
                    checkMate = true;
                }
            } else if(currentGameState.isInCheckmate(ChessGame.TeamColor.BLACK) && currentGameState.isInStalemate(ChessGame.TeamColor.BLACK)){
                System.out.println("BLACK is in CheckMate\n");
                if (playerColor == ChessGame.TeamColor.BLACK) {
                    checkMate = true;
                }
            } else{
                if (currentGameState.isInCheck(ChessGame.TeamColor.WHITE)){
                    System.out.println("WHITE is in Check");
                } else if (currentGameState.isInCheck(ChessGame.TeamColor.BLACK)){
                    System.out.println("BLACK is in Check");
                }
            }

            System.out.printf("It is %s's turn\n", currentGameState.getTeamTurn().toString());
        }
    }
}
