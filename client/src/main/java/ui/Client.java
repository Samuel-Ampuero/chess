package ui;

import exception.ResponseException;
import model.UserData;
import request_result.UserResult;

import java.util.Arrays;
import java.util.Scanner;
public class Client {
    private String visitorName = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;

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

    private String eval(String input){
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
//                case "rescue" -> rescuePet(params);
                case "logout" -> logout(params);
//                case "signout" -> signOut();
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
        if (params.length >= 1) {
            state = State.SIGNEDIN;
            visitorName = params[0];
            UserResult result = server.register(new UserData(params[0],params[1], params[2]));
            return String.format("You signed in as %s. Here is your authToken: %s\n", result.username(), result.authToken());
        }
        throw new ResponseException(400, "Expected: <yourname>");
    }
    public String logout(String... params) throws ResponseException {
        if (params.length == 1) {
            state = State.SIGNEDOUT;
            String authToken = params[0];
            visitorName = "";
            server.logout(authToken);
            return "Successfully logged out\n";
        }
        throw new ResponseException(400, "Expected: <yourname>");
    }
}
