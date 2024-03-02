package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO{

    public SQLGameDAO() throws DataAccessException{
        configureDatabase();
    }
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

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("500: Error");
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS gameDatabase (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) NOT NULL,
              `blackUsername` varchar(256) NOT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`)
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("500: Error");
        }
    }
}
