package webSocketMessages.userCommands;

import com.google.gson.Gson;

public class JoinObserver extends UserGameCommand{
    int gameID;
    UserGameCommand gameCommand;
    public JoinObserver(int gameID, String authToken){
        this.gameID = gameID;
        gameCommand = new UserGameCommand(authToken, CommandType.JOIN_OBSERVER);
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
