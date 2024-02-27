package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
public class MemoryGameDAO implements GameDAO{
    private ArrayList<GameData> gameDatas = new ArrayList<>();
    private int gameID = 1;
    public int createGame(String gameName) throws DataAccessException {
        GameData gameData = new GameData(gameID,null,null,gameName,new ChessGame());
        gameDatas.add(gameData);
        return gameID++;
    }

    public ArrayList<GameData> listGames() throws DataAccessException{
        return gameDatas;
    }

    public GameData getGame(int gameID) throws DataAccessException{
        for (GameData elem : gameDatas){
            if(elem.gameID() == gameID){
                return elem;
            }
        }
        return null;
    }

    public void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame chessGame){
        for (int i = 0; i < gameDatas.size(); i++){
            if (gameDatas.get(i).gameID() == gameID){
                gameDatas.set(i, gameDatas.get(i).updateGameData(whiteUsername, blackUsername, gameName, chessGame));
            }
        }
    }

    public void deleteAllGames() throws DataAccessException{
        gameDatas.clear();
    }
}
