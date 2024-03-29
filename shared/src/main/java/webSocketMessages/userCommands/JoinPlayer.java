package webSocketMessages.userCommands;

import chess.ChessGame;
import com.google.gson.Gson;

public class JoinPlayer extends UserGameCommand{
    int gameID;
    ChessGame.TeamColor playerColor;
    UserGameCommand gameCommand;
    public JoinPlayer(int gameID, ChessGame.TeamColor playerColor, String authToken){
        this.gameID = gameID;
        this.playerColor = playerColor;
        gameCommand = new UserGameCommand(authToken, CommandType.JOIN_PLAYER);
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
