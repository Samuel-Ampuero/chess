package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;

    GameData getGame(String gameName) throws DataAccessException;

    void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame chessGame);

    void deleteGame(String gameName) throws DataAccessException;

    void deleteAllGames() throws DataAccessException;
}
