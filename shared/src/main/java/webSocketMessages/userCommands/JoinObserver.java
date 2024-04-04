package webSocketMessages.userCommands;

import com.google.gson.Gson;

public class JoinObserver extends UserGameCommand{
    int gameID;
    public JoinObserver(int gameID, String authToken){
        super(authToken, CommandType.JOIN_OBSERVER);
        this.gameID = gameID;
    }

    public String toString() {
        return new Gson().toJson(this);
    }

    public int getGameID(){return gameID;}
}
