package dataAccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
public class MemoryAuthDAO implements AuthDAO{
    private Collection<AuthData> authDatas = new ArrayList<>();
    public String createAuth(String username) throws DataAccessException {
        UUID uuid = UUID.randomUUID();
        String authToken = uuid.toString().replaceAll("-", "");
        AuthData authData = new AuthData(authToken, username);
        authDatas.add(authData);
        return authToken;
    }

    public Collection<AuthData> listAuths() throws DataAccessException{
        return authDatas;
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
}
