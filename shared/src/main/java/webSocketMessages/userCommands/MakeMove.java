package webSocketMessages.userCommands;

import chess.ChessMove;
import com.google.gson.Gson;

public class MakeMove extends UserGameCommand{
    int gameID;
    ChessMove move;
    UserGameCommand gameCommand;
    public MakeMove(int gameID, ChessMove move, String authToken){
        this.gameID = gameID;
        this.move = move;
        gameCommand = new UserGameCommand(authToken, UserGameCommand.CommandType.MAKE_MOVE);
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
