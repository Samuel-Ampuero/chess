package clientTests;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import request_result.CreateGameRequest;
import request_result.GameResult;
import request_result.JoinGameRequest;
import request_result.LoginRequest;
import server.Server;
import ui.ServerFacade;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:8080");
    }
    @BeforeEach
    @Test
    public void clear(){
        try {
            Assertions.assertEquals("", facade.clear());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void registerPositive() throws Exception {
        var authData = facade.register(new UserData("player1", "password", "p1@email.com"));
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerNegative() throws Exception {
        facade.register(new UserData("player1", "password", "p1@email.com"));
        Assertions.assertThrows(ResponseException.class, () ->
                facade.register(new UserData("player1", "password", "p1@email.com")));
    }

    @Test
    void loginPositive() {
        try {
            var test = facade.register(new UserData("player1", "password", "p1@email.com"));
            facade.logout(test.authToken());
            var result = facade.login(new LoginRequest("player1", "password"));
            Assertions.assertTrue(result.authToken().length() > 10);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void loginNegative() {
        Assertions.assertThrows(ResponseException.class, () ->
                facade.login(new LoginRequest("player1", "password")));
    }

    @Test
    void logoutPositive() {
        try {
            var test = facade.register(new UserData("player1", "password", "p1@email.com"));
            facade.logout(test.authToken());
            Assertions.assertThrows(ResponseException.class, () ->
                    facade.logout(test.authToken()));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void logoutNegative() {
        try {
            facade.register(new UserData("player1", "password", "p1@email.com"));
            Assertions.assertThrows(ResponseException.class, () ->
                    facade.logout("not authorized"));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createPositive() {
        try {
            var test = facade.register(new UserData("player1", "password", "p1@email.com"));
            var game = facade.create(new CreateGameRequest("game"), test.authToken());
            var result = facade.list(test.authToken());
            Assertions.assertTrue(game.gameID() > 0);
            Assertions.assertEquals(1, result.games().size());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createNegative() {
        Assertions.assertThrows(ResponseException.class, () ->
                facade.create(new CreateGameRequest("test"), "no authToken"));
    }

    @Test
    void listPositive() {
        try {
            var test = facade.register(new UserData("player1", "password", "p1@email.com"));
            facade.create(new CreateGameRequest("game1"), test.authToken());
            facade.create(new CreateGameRequest("game2"), test.authToken());
            facade.create(new CreateGameRequest("game3"), test.authToken());
            var result = facade.list(test.authToken());
            Assertions.assertEquals(3, result.games().size());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void listNegative() {
        try {
            var test = facade.register(new UserData("player1", "password", "p1@email.com"));
            facade.create(new CreateGameRequest("game1"), test.authToken());
            facade.create(new CreateGameRequest("game2"), test.authToken());
            facade.create(new CreateGameRequest("game3"), test.authToken());
            Assertions.assertThrows(ResponseException.class, () ->
                    facade.list("not authorized"));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void joinPositive() {
        try {
            var test = facade.register(new UserData("player1", "password", "p1@email.com"));
            var game = facade.create(new CreateGameRequest("game1"), test.authToken());
            facade.join(new JoinGameRequest("WHITE", game.gameID()), test.authToken());
            var result = facade.list(test.authToken());
            for (GameResult gameResult : result.games()){
                Assertions.assertEquals(test.username(), gameResult.whiteUsername());
            }
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void joinNegeative() {
        try {
            var user1 = facade.register(new UserData("player1", "password", "p1@email.com"));
            var user2 = facade.register(new UserData("player2", "password", "p2@email.com"));
            var game = facade.create(new CreateGameRequest("game1"), user1.authToken());
            facade.join(new JoinGameRequest("WHITE", game.gameID()), user1.authToken());
            Assertions.assertThrows(ResponseException.class, () ->
                    facade.join(new JoinGameRequest("WHITE", game.gameID()), user2.authToken()));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }
}
