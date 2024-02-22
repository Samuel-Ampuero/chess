package dataAccess;

import model.UserData;

import java.util.Collection;

public interface UserDAO {
    UserData createUser(String username, String password) throws DataAccessException;

    Collection<UserData> listUsers() throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void deleteUser(String username) throws DataAccessException;

    void deleteAllUsers() throws DataAccessException;
}
