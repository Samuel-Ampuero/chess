package dataAccess;

import exception.DataAccessException;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO{
    private Collection<UserData> userDatas = new ArrayList<>();
    public void createUser(String username, String password, String email) throws DataAccessException {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);
        UserData userData = new UserData(username, hashedPassword, email);
        userDatas.add(userData);
    }

    public Collection<UserData> listUsers() throws DataAccessException{
        return userDatas;
    }

    public UserData getUser(String username) throws DataAccessException{
        for (UserData elem : userDatas){
            if(Objects.equals(elem.username(), username)){
                return elem;
            }
        }
        return null;
    }

    public void deleteAllUsers() throws DataAccessException{
        userDatas.clear();
    }
}
