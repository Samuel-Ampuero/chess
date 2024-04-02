package webSocketMessages.userCommands;

import chess.ChessGame;
import com.google.gson.Gson;

public class JoinPlayer extends UserGameCommand{
    int gameID;
    ChessGame.TeamColor playerColor;

    public JoinPlayer(int gameID, ChessGame.TeamColor playerColor, String authToken){
        super(authToken, CommandType.JOIN_PLAYER);
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
