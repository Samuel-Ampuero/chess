package dataAccess;

import model.UserData;

import java.util.Collection;

public interface UserDAO {
    void createUser(String username, String password, String email) throws DataAccessException;

    Collection<UserData> listUsers() throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void deleteAllUsers() throws DataAccessException;
}
