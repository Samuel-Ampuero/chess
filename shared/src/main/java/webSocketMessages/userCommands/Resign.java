package webSocketMessages.userCommands;

import com.google.gson.Gson;

public class Resign {
    int gameID;
    UserGameCommand gameCommand;
    public Resign(int gameID, String authToken){
        this.gameID = gameID;
        gameCommand = new UserGameCommand(authToken, UserGameCommand.CommandType.RESIGN);
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
