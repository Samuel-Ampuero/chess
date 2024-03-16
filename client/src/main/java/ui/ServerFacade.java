package ui;

import exception.ResponseException;
import model.GameData;
import model.UserData;
import request_result.*;

public class ServerFacade extends ClientCommunicator{
    private final String serverUrl;
    public ServerFacade(String url) {
        serverUrl = url;
    }

    public UserResult register(UserData user) throws ResponseException {
        var path = "/user";
        return new ClientCommunicator(serverUrl).makeRequest("POST", path, null, user, UserResult.class);
    }
    public UserResult login(LoginRequest user) throws ResponseException {
        var path = "/session";
        return new ClientCommunicator(serverUrl).makeRequest("POST", path, null, user, UserResult.class);
    }
    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        new ClientCommunicator(serverUrl).makeRequest("DELETE", path, authToken, new AuthTokenRequest(authToken), null);
    }

    public CreateGameResult create(CreateGameRequest gameRequest, String authToken) throws ResponseException {
        var path = "/game";
        return new ClientCommunicator(serverUrl).makeRequest("POST", path, authToken, gameRequest, CreateGameResult.class);
    }

    public GameListResult list(String authToken) throws ResponseException {
        var path = "/game";
        return new ClientCommunicator(serverUrl).makeRequest("GET", path, authToken, null, GameListResult.class);
    }

    public GameData join(JoinGameRequest request, String authToken) throws ResponseException {
        var path = "/game";
        return new ClientCommunicator(serverUrl).makeRequest("PUT", path, authToken, request, GameData.class);
    }

    public String clear() throws ResponseException {
        var path = "/db";
        new ClientCommunicator(serverUrl).makeRequest("DELETE", path, null, null, null);
        return "";
    }
}
