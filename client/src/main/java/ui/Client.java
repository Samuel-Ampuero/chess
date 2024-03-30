package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import request_result.*;
import websocket.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
public class Client extends EscapeSequences{
    private String visitorName = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private boolean connected = false;
    private String authToken;
    private HashMap<Integer, GameResult> gameList = new HashMap<>();
    private NotificationHandler notificationHandler;
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
            if (state == State.SIGNEDOUT) {
                System.out.print("\n[LOGGED_OUT] >>> ");
            } else {
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
                //case "join_player" -> joinPlayer(params);
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
        state = State.SIGNEDOUT;
        visitorName = "";
        server.logout(authToken);
        authToken = "";
        return "Successfully logged out\n";
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            server.create(new CreateGameRequest(params[0]), authToken);
            return "Successfully create a ChessGame.\n";
        }
        throw new ResponseException(400, "Expected: <gameName>\n");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
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
        if (params.length == 2) {
            if (!gameList.containsKey(Integer.parseInt(params[0]))){
                throw new ResponseException(400, "Game does not exist\n");
            }
            GameData game = server.join(new JoinGameRequest(params[1].toUpperCase(), gameList.get(Integer.parseInt(params[0])).gameID()), authToken);
            ws = new WebSocketFacade(url, notificationHandler);

            if (params[1].equalsIgnoreCase("WHITE")) {
                ws.joinPlayer(authToken, Integer.parseInt(params[0]), ChessGame.TeamColor.WHITE);
            } else {
                ws.joinPlayer(authToken, Integer.parseInt(params[0]), ChessGame.TeamColor.BLACK);
            }

            new ChessBoardUI(game.game()).printBoards();
            System.out.printf("Successfully joined game #%s\n", params[0]);
            connected = true;

            return "";
        }
        throw new ResponseException(400, "Expected: <gameID> [WHITE|BLACK]\n");
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            if (!gameList.containsKey(Integer.parseInt(params[0]))){
                throw new ResponseException(400, "Game does not exist\n");
            }
            GameData game = server.join(new JoinGameRequest(null, gameList.get(Integer.parseInt(params[0])).gameID()), authToken);
            System.out.printf("Observing game #%s\n", params[0]);
            new ChessBoardUI(game.game()).printBoards();
            connected = true;
            return "";
        }
        throw new ResponseException(400, "Expected: <gameID>\n");
    }

//    public String joinPlayer(String... params) throws ResponseException {
//        assertSignedIn();
//        if (params.length == 2) {
//            if (!gameList.containsKey(Integer.parseInt(params[0]))){
//                throw new ResponseException(400, "Game does not exist\n");
//            }
//            if ((Objects.equals(params[1], "white") && !Objects.equals(gameList.get(Integer.parseInt(params[0])).whiteUsername(), visitorName)) ||
//                    (Objects.equals(params[1], "black") && !Objects.equals(gameList.get(Integer.parseInt(params[0])).blackUsername(), visitorName))) {
//                throw new ResponseException(400, String.format("You are not that color player in Game #%s\n", params[0]));
//            }
//            connected = true;
//
////            GameData game = gameList.get(Integer.parseInt(params[0]));
////            System.out.printf("Successfully joined game #%s\n", params[0]);
////            new ChessBoardUI(game.game()).printBoards();
//            return "";
//        }
//        //FIXME
//        throw new ResponseException(400, "Expected: <gameID> [WHITE|BLACK|<empty>]\n");
//    }

    public String clear() throws ResponseException {
        server.clear();
        visitorName = null;
        state = State.SIGNEDOUT;
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
        } else if (state == State.SIGNEDIN && connected) {
            out.println(SET_TEXT_COLOR_BLUE + "  redraw" + "\u001b[0m" + " - chess board");
            out.println(SET_TEXT_COLOR_BLUE + "  highlight <POSITION>" + "\u001b[0m" + " - legal moves of specified piece");
            out.println(SET_TEXT_COLOR_BLUE + "  make <MOVE>" + "\u001b[0m" + " - on board e.g. b2b3");
            out.println(SET_TEXT_COLOR_BLUE + "  resign" + "\u001b[0m" + " - the game");
            out.println(SET_TEXT_COLOR_BLUE + "  leave" + "\u001b[0m" + " - the game");
            out.println(SET_TEXT_COLOR_BLUE + "  help" + "\u001b[0m" + " - with possible commands");
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
}
