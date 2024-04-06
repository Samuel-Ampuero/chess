package dataAccess;

import exception.DataAccessException;
import model.AuthData;

import java.sql.*;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO{
    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }
    public String createAuth(String username) throws DataAccessException {
        UUID uuid = UUID.randomUUID();
        String authToken = uuid.toString().replaceAll("-", "");

        var statement = "INSERT INTO authDatabase (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, authToken, username);
        return authToken;
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM authDatabase WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String auth = rs.getString("authToken");
                        String username = rs.getString("username");

                        return new AuthData(auth, username);
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        if (authToken == null){
            throw new DataAccessException("500: ERROR");
        }

        var statement = "DELETE FROM authDatabase WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    public void deleteAllAuths() throws DataAccessException{
        var statement = "TRUNCATE authDatabase";
        executeUpdate(statement);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var j = 0; j < params.length; j++) {
                    var param = params[j];
                    if (param instanceof String p) ps.setString(j + 1, p);
                    else if (param == null) ps.setNull(j + 1, NULL);
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
            CREATE TABLE IF NOT EXISTS authDatabase (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        SQLUserDAO.configureHelper(createStatements);
    }
}
