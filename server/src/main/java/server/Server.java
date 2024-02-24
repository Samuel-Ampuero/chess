package server;

import com.google.gson.Gson;
import model.UserData;
import request_result.RegisterResult;
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
        Spark.post("/user", this::registerMethod);
        // Register your endpoints and handle exceptions here.

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object registerMethod(Request request, Response response) throws DataAccessException {
        UserData user = new Gson().fromJson(request.body(), UserData.class);
        RegistrationService service = new RegistrationService();
        var result = service.register(userMemory, user, authMemory);
        return new Gson().toJson(result);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
