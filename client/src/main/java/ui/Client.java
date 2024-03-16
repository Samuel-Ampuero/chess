package ui;

import exception.ResponseException;
import model.GameData;
import model.UserData;
import request_result.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
public class Client {
    private String visitorName = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private String authToken;
    private HashMap<Integer, GameResult> gameList = new HashMap<>();

    public Client(String url) {
        server = new ServerFacade(url);
    }
    public void run() {
        System.out.println("♕ Welcome to 240 chess. ♕");

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!result.equals("quit")) {

            String line = scanner.nextLine();

            try {
                System.out.print(eval(line));
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
            throw new ResponseException(400, "You must sign in");
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
//                case "quit" -> "quit";
                default -> "need to fix";//help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            state = State.SIGNEDIN;
            visitorName = params[0];
            UserResult result = server.register(new UserData(params[0],params[1], params[2]));
            authToken = result.authToken();
            return String.format("You signed in as %s.\n", result.username());
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>\n");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            state = State.SIGNEDIN;
            visitorName = params[0];
            UserResult result = server.login(new LoginRequest(params[0], params[1]));
            authToken = result.authToken();
            return String.format("Successfully logged in. Welcome %s.\n", result.username());
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
            CreateGameResult result = server.create(new CreateGameRequest(params[0]), authToken);
            return String.format("Successfully create a ChessGame. Here is the gameID: %s\n", result.gameID());
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
            System.out.printf("Successfully joined game #%s\n", params[0]);
            new ChessBoardUI(game.game()).printBoards();
            return "";
        }
        throw new ResponseException(400, "Expected: <gameID> [WHITE|BLACK|<empty>]\n");
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
            return "";
        }
        throw new ResponseException(400, "Expected: <gameID>\n");
    }
}
