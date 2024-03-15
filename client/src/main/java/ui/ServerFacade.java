package ui;

import exception.ResponseException;
import model.UserData;
import request_result.UserResult;
public class ServerFacade extends ClientCommunicator{
    private final String serverUrl;
    public ServerFacade(String url) {
        serverUrl = url;
    }

    public UserResult register(UserData user) throws ResponseException {
        var path = "/user";
        return new ClientCommunicator(serverUrl).makeRequest("POST", path, user, UserResult.class);
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
