package server;

import com.google.gson.Gson;
import model.UserData;
import request_result.*;
import spark.*;
import dataAccess.*;
import service.*;

public class Server {

    private AuthDAO authMemory = new MemoryAuthDAO();
    private UserDAO userMemory = new MemoryUserDAO();
    private GameDAO gameMemory = new MemoryGameDAO();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.post("/user", this::registerHandler);
        Spark.post("/session", this::loginHandler);
        Spark.delete("/session", this::logoutHandler);
        Spark.get("/game", this::listGamesHandler);
        // Register your endpoints and handle exceptions here.

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object registerHandler(Request request, Response response) throws DataAccessException {
        UserData user = new Gson().fromJson(request.body(), UserData.class);
        RegistrationService service = new RegistrationService();
        var result = service.register(userMemory, user, authMemory, response);
        return new Gson().toJson(result);
    }
    public Object loginHandler(Request request, Response response) throws DataAccessException {
        LoginRequest user = new Gson().fromJson(request.body(), LoginRequest.class);
        LoginService service = new LoginService();
        var result = service.login(userMemory, user, authMemory, response);
        return new Gson().toJson(result);
    }
    public Object logoutHandler(Request request, Response response) throws DataAccessException {
        AuthTokenRequest authToken = new AuthTokenRequest(request.headers("Authorization"));
        LogoutService service = new LogoutService();
        var result = service.logout(authToken, authMemory, response);
        return new Gson().toJson(result);
    }

    public Object listGamesHandler(Request request, Response response) throws DataAccessException {
        AuthTokenRequest authToken = new AuthTokenRequest(request.headers("Authorization"));
        ListGamesService service = new ListGamesService();
        var result = service.listGames(authToken, authMemory, gameMemory, response);
        return new Gson().toJson(result);
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
