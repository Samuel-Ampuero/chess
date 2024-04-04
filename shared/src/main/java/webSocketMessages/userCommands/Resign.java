package webSocketMessages.userCommands;

import com.google.gson.Gson;

public class Resign extends UserGameCommand{
    int gameID;
    public Resign(int gameID, String authToken){
        super(authToken, CommandType.JOIN_PLAYER);
        this.gameID = gameID;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
