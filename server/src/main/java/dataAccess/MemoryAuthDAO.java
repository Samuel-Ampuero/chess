package dataAccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
public class MemoryAuthDAO implements AuthDAO{
    final private Collection<AuthData> authDatas = new ArrayList<>();
    public AuthData createAuth(String username) throws DataAccessException {
        UUID uuid = UUID.randomUUID();
        AuthData authData = new AuthData(uuid.toString().replaceAll("-", ""), username);
        authDatas.add(authData);
        return authData;
    }

    public Collection<AuthData> listAuths() throws DataAccessException{
        return authDatas;
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        for (AuthData elem : authDatas){
            if(elem.authToken() == authToken){
                return elem;
            }
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        for (AuthData elem : authDatas){
            if(elem.authToken() == authToken){
                authDatas.remove(elem);
                return;
            }
        }
    }

    public void deleteAllAuths() throws DataAccessException{
        authDatas.clear();
    }
}
