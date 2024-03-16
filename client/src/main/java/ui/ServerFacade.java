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

//    public void deletePet(int id) throws ResponseException {
//        var path = String.format("/pet/%s", id);
//        this.makeRequest("DELETE", path, null, null);
//    }
//
//    public void deleteAllPets() throws ResponseException {
//        var path = "/pet";
//        this.makeRequest("DELETE", path, null, null);
//    }
//
//    public Pet[] listPets() throws ResponseException {
//        var path = "/pet";
//        record listPetResponse(Pet[] pet) {
//        }
//        var response = this.makeRequest("GET", path, null, listPetResponse.class);
//        return response.pet();
//    }


}
