package webSocketMessages.userCommands;

import com.google.gson.Gson;

public class Leave extends UserGameCommand{
    int gameID;
    public Leave(int gameID, String authToken){
        super(authToken, CommandType.LEAVE);
        this.gameID = gameID;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
