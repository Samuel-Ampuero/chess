package dataAccess;

import chess.ChessGame;
import exception.DataAccessException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame chessGame) throws DataAccessException;

    void deleteAllGames() throws DataAccessException;
}
