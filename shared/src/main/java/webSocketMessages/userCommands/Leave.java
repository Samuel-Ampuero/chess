package webSocketMessages.userCommands;

import com.google.gson.Gson;

public class Leave {
    int gameID;
    UserGameCommand gameCommand;
    public Leave(int gameID, String authToken){
        this.gameID = gameID;
        gameCommand = new UserGameCommand(authToken, UserGameCommand.CommandType.LEAVE);
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
