package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData createGame(String gameName) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    GameData getGame(String gameName) throws DataAccessException;

    void deleteAuth(String gameName) throws DataAccessException;

    void deleteAllGames() throws DataAccessException;
}
