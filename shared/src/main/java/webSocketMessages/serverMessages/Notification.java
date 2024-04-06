package webSocketMessages.serverMessages;

import com.google.gson.Gson;

public class Notification extends ServerMessage{
    String message;

    public Notification(String message){
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
    public String getMessage() {return message;}
}
