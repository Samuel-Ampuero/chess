package dataAccess;

import chess.ChessGame;
import model.GameData;

import com.google.gson.Gson;
import java.sql.*;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO{

    public SQLGameDAO() throws DataAccessException{
        configureDatabase();
    }
    public int createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO gameDatabase (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        var game = new Gson().toJson(new ChessGame());
        return executeUpdate(statement, null, null, gameName, game);
    }

    public ArrayList<GameData> listGames() throws DataAccessException{
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameDatabase";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        while (rs.next()) {
                            result.add(readGameData(rs));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("500: Error");
        }
        return result;
    }
    private GameData readGameData(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var game = rs.getString("game");

        var chessGame = new Gson().fromJson(game, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
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
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
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
