package webSocketMessages.serverMessages;

import chess.ChessGame;
import com.google.gson.Gson;

public class LoadGame extends ServerMessage{
    ChessGame game;
    ServerMessage serverMessage;

    public LoadGame(ChessGame game){
        this.game = game;
        serverMessage = new ServerMessage(ServerMessageType.LOAD_GAME);
    }

    public String toString() {
        return new Gson().toJson(this);
    }
    public ChessGame getGame(){return game;}
}
