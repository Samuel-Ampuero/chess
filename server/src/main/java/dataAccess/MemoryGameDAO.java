package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
public class MemoryGameDAO implements GameDAO{
    private Collection<GameData> gameDatas = new ArrayList<>();
    private int gameID = 1;
    public int createGame(String gameName) throws DataAccessException {
        GameData gameData = new GameData(gameID,null,null,gameName,new ChessGame());
        gameDatas.add(gameData);
        return gameID++;
    }

    public Collection<GameData> listGames() throws DataAccessException{
        return gameDatas;
    }

    public GameData getGame(String gameName) throws DataAccessException{
        for (GameData elem : gameDatas){
            if(elem.gameName() == gameName){
                return elem;
            }
        }
        return null;
    }

    public void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame chessGame){
        for (GameData elem : gameDatas){
            if(elem.gameID() == gameID){
                elem.updateGameData(whiteUsername, blackUsername, gameName, chessGame);
                return;
            }
        }
    }

    public void deleteGame(String gameName) throws DataAccessException{
        for (GameData elem : gameDatas){
            if(elem.gameName() == gameName){
                gameDatas.remove(elem);
                return;
            }
        }
    }

    public void deleteAllGames() throws DataAccessException{
        gameDatas.clear();
    }
}
