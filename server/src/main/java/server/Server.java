package server;

import com.google.gson.Gson;
import model.UserData;
import request_result.*;
import spark.*;
import dataAccess.*;
import service.*;

import java.io.Reader;

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
        // Register your endpoints and handle exceptions here.

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object registerHandler(Request request, Response response) throws DataAccessException {
        UserData user = new Gson().fromJson(request.body(), UserData.class);
        RegistrationService service = new RegistrationService();
        var result = service.register(userMemory, user, authMemory);
        return new Gson().toJson(result);
    }
    public Object loginHandler(Request request, Response response) throws DataAccessException {
        LoginRequest user = new Gson().fromJson(request.body(), LoginRequest.class);
        LoginService service = new LoginService();
        var result = service.login(userMemory, user, authMemory);
        return new Gson().toJson(result);
    }

    //FIXME:: HOW TO GRAB THE HEADER???????
    public Object logoutHandler(Request request, Response response) throws DataAccessException {
        LogoutRequest authToken = new Gson().fromJson(request.headers(), LogoutRequest.class);
        LogoutService service = new LogoutService();
        var result = service.logout(authToken, authMemory);
        return new Gson().toJson(result);
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
