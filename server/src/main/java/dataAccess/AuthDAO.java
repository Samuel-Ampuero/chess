package dataAccess;

import model.AuthData;

import java.util.Collection;
public interface AuthDAO {
    AuthData createAuth(String username) throws DataAccessException;

    Collection<AuthData> listAuths() throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    void deleteAllAuths() throws DataAccessException;
}
