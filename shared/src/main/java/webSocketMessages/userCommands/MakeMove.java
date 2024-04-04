package webSocketMessages.userCommands;

import chess.ChessMove;
import com.google.gson.Gson;

public class MakeMove extends UserGameCommand{
    int gameID;
    ChessMove move;
    public MakeMove(int gameID, ChessMove move, String authToken){
        super(authToken, CommandType.MAKE_MOVE);
        this.gameID = gameID;
        this.move = move;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
