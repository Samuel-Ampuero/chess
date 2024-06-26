package dataAccess;

import exception.DataAccessException;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    public void createUser(String username, String password, String email) throws DataAccessException {
        var statement = "INSERT INTO userDatabase (username, password, email) VALUES (?, ?, ?)";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);

        executeUpdate(statement, username, hashedPassword, email);
    }

    public Collection<UserData> listUsers() throws DataAccessException{
        var result = new ArrayList<UserData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM userDatabase";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String name = rs.getString("username");
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        result.add(new UserData(name, password, email));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("500: Error");
        }
        return result;

    }

    public UserData getUser(String username) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM userDatabase WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String name = rs.getString("username");
                        String password = rs.getString("password");
                        String email = rs.getString("email");

                        return new UserData(name, password, email);
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public void deleteAllUsers() throws DataAccessException{
        var statement = "TRUNCATE userDatabase";
        executeUpdate(statement);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try {
            var conn = DatabaseManager.getConnection();
            try {
                var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS);
                try {
                    var i = 0;
                    while (!(params.length <= i)) {
                        var param = params[i];
                        if (param instanceof String p) ps.setString(i + 1, p);
                        else if (param == null) ps.setNull(i + 1, NULL);
                        i++;
                    }
                    ps.executeUpdate();

                    var rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        return rs.getInt(1);
                    }

                    return 0;
                } finally {
                    ps.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            throw new DataAccessException("500: Error");
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS userDatabase (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        configureHelper(createStatements);
    }

    static void configureHelper(String[] createStatements) throws DataAccessException {
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
