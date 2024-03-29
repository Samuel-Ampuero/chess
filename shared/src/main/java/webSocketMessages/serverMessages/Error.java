package webSocketMessages.serverMessages;

import com.google.gson.Gson;

public class Error extends ServerMessage{
    String errorMessage;
    ServerMessage serverMessage;

    public Error(String errorMessage){
        this.errorMessage = errorMessage;
        serverMessage = new ServerMessage(ServerMessageType.ERROR);
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
