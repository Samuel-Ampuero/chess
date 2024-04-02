package webSocketMessages.serverMessages;

import com.google.gson.Gson;

public class Notification extends ServerMessage{
    String message;
    ServerMessage serverMessage;

    public Notification(String message){
        this.message = message;
        serverMessage = new ServerMessage(ServerMessageType.NOTIFICATION);
    }

    public String toString() {
        return new Gson().toJson(this);
    }
    public String getMessage() {return message;}
}
