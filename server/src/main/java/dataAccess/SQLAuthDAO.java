package dataAccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import java.util.Objects;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO{
    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }
    private Collection<AuthData> authDatas = new ArrayList<>();
    public String createAuth(String username) throws DataAccessException {
        UUID uuid = UUID.randomUUID();
        String authToken = uuid.toString().replaceAll("-", "");

        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, authToken, username);
        return authToken;
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        for (AuthData elem : authDatas){
            if(Objects.equals(elem.authToken(), authToken)){
                return elem;
            }
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        for (AuthData elem : authDatas){
            if(Objects.equals(elem.authToken(), authToken)){
                authDatas.remove(elem);
                return;
            }
        }
    }

    public void deleteAllAuths() throws DataAccessException{
        authDatas.clear();
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
            CREATE TABLE IF NOT EXISTS auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
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