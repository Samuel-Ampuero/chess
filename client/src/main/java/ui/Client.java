package ui;

import exception.ResponseException;
import model.UserData;
import request_result.CreateGameRequest;
import request_result.CreateGameResult;
import request_result.LoginRequest;
import request_result.UserResult;

import java.util.Arrays;
import java.util.Scanner;
public class Client {
    private String visitorName = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private String authToken;

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
                case "logout" -> logout(params);
                case "create" -> createGame(params);
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
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

    public String logout(String... params) throws ResponseException {
        if (params.length == 0) {
            state = State.SIGNEDOUT;
            visitorName = "";
            server.logout(authToken);
            authToken = "";
            return "Successfully logged out\n";
        }
        throw new ResponseException(400, "Expected: <>\n");
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            CreateGameResult result = server.create(new CreateGameRequest(params[0]), authToken);
            return String.format("Successfully create a ChessGame. Here is the gameID: %s\n", result.gameID());
        }
        throw new ResponseException(400, "Expected: <gameName>\n");
    }

}
