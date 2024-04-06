package webSocketMessages.serverMessages;

import chess.ChessGame;
import com.google.gson.Gson;

public class LoadGame extends ServerMessage{
    ChessGame game;

    public LoadGame(ChessGame game){
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
    public ChessGame getGame(){return game;}
}
