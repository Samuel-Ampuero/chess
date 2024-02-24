package dataAccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserDAO implements UserDAO{
    private Collection<UserData> userDatas = new ArrayList<>();
    public void createUser(String username, String password, String email) throws DataAccessException {
        UserData userData = new UserData(username, password, email);
        userDatas.add(userData);
    }

    public Collection<UserData> listUsers() throws DataAccessException{
        return userDatas;
    }

    public String getUser(String username) throws DataAccessException{
        for (UserData elem : userDatas){
            if(elem.username() == username){
                return elem.username();
            }
        }
        return null;
    }

    public void deleteAllUsers() throws DataAccessException{
        userDatas.clear();
    }
}
