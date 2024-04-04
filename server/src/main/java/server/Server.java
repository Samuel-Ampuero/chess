package server;

import com.google.gson.Gson;
import exception.DataAccessException;
import model.UserData;
import request_result.*;
import spark.*;
import dataAccess.*;
import service.*;
import websocket.WebSocketHandler;

import java.util.Objects;

public class Server {
    private AuthDAO authMemory;
    private UserDAO userMemory;
    private GameDAO gameMemory;
    private WebSocketHandler webSocketHandler = null;

    private void createDAOs() {
        try {
            userMemory = new SQLUserDAO();
            authMemory = new SQLAuthDAO();
            gameMemory = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public int run(int desiredPort) {
        createDAOs();
        Spark.port(desiredPort);

        webSocketHandler = new WebSocketHandler(authMemory,userMemory,gameMemory);

        Spark.staticFiles.location("web");

        Spark.webSocket("/connect", webSocketHandler);

        Spark.post("/user", this::registerHandler);
        Spark.post("/session", this::loginHandler);
        Spark.delete("/session", this::logoutHandler);
        Spark.get("/game", this::listGamesHandler);
        Spark.post("/game", this::createGameHandler);
        Spark.put("/game", this::joinGameHandler);
        Spark.delete("/db", this::clearHandler);
        // Register your endpoints and handle exceptions here.

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object registerHandler(Request request, Response response) throws DataAccessException {
        UserData user = new Gson().fromJson(request.body(), UserData.class);
        RegistrationService service = new RegistrationService();
        var result = service.register(userMemory, user, authMemory);

        if (result instanceof FailureRepsonse){
            if (Objects.equals(((FailureRepsonse) result).message(), "Error: bad request")){
                response.status(400);
            } else {
                response.status(403);
            }
        } else {
            response.status(200);
        }

        return new Gson().toJson(result);
    }
    public Object loginHandler(Request request, Response response) throws DataAccessException {
        LoginRequest user = new Gson().fromJson(request.body(), LoginRequest.class);
        LoginService service = new LoginService();
        var result = service.login(userMemory, user, authMemory);

        if (result instanceof FailureRepsonse){
            if (Objects.equals(((FailureRepsonse) result).message(), "Error: unauthorized")){
                response.status(401);
            }
        } else {
            response.status(200);
        }

        return new Gson().toJson(result);
    }
    public Object logoutHandler(Request request, Response response) throws DataAccessException {
        AuthTokenRequest authToken = new AuthTokenRequest(request.headers("Authorization"));
        LogoutService service = new LogoutService();
        var result = service.logout(authToken, authMemory);

        if (result instanceof FailureRepsonse){
            if (Objects.equals(((FailureRepsonse) result).message(), "Error: unauthorized")){
                response.status(401);
            }
        } else {
            response.status(200);
        }

        return new Gson().toJson(result);
    }
    public Object listGamesHandler(Request request, Response response) throws DataAccessException {
        AuthTokenRequest authToken = new AuthTokenRequest(request.headers("Authorization"));
        ListGamesService service = new ListGamesService();
        var result = service.listGames(authToken, authMemory, gameMemory);

        if (result instanceof FailureRepsonse){
            if (Objects.equals(((FailureRepsonse) result).message(), "Error: unauthorized")){
                response.status(401);
            }
        } else {
            response.status(200);
        }

        return new Gson().toJson(result);
    }
    public Object createGameHandler(Request request, Response response) throws DataAccessException {
        AuthTokenRequest authToken = new AuthTokenRequest(request.headers("Authorization"));
        CreateGameRequest game = new Gson().fromJson(request.body(), CreateGameRequest.class);
        CreateGameService service = new CreateGameService();
        var result = service.createGame(game, authToken, authMemory, gameMemory);

        if (result instanceof FailureRepsonse){
            if (Objects.equals(((FailureRepsonse) result).message(), "Error: bad request")){
                response.status(400);
            } else {
                response.status(401);
            }
        } else {
            response.status(200);
        }

        return new Gson().toJson(result);
    }
    public Object joinGameHandler(Request request, Response response) throws DataAccessException {
        AuthTokenRequest authToken = new AuthTokenRequest(request.headers("Authorization"));
        JoinGameRequest jgame = new Gson().fromJson(request.body(), JoinGameRequest.class);
        JoinGameService service = new JoinGameService();
        var result = service.joinGame(jgame, authToken, authMemory, gameMemory);

        if (result instanceof FailureRepsonse){
            if (Objects.equals(((FailureRepsonse) result).message(), "Error: bad request")){
                response.status(400);
            } else if (Objects.equals(((FailureRepsonse) result).message(), "Error: unauthorized")){
                response.status(401);
            } else {
                response.status(403);
            }
        } else {
            response.status(200);
        }

        return new Gson().toJson(result);
    }
    public Object clearHandler(Request request, Response response) throws DataAccessException {
        ClearService service = new ClearService();
        var result = service.clear(userMemory, authMemory, gameMemory);
        response.status(200);
        return new Gson().toJson(result);
    }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
