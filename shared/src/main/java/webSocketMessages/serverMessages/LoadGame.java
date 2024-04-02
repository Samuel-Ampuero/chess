package webSocketMessages.serverMessages;

import com.google.gson.Gson;

public class LoadGame extends ServerMessage{
    String game;
    ServerMessage serverMessage;

    public LoadGame(String game){
        this.game = game;
        serverMessage = new ServerMessage(ServerMessageType.LOAD_GAME);
    }

    public String toString() {
        return new Gson().toJson(this);
    }
    public String getGame(){return game;}
}
