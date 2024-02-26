package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import request_result.FailureRepsonse;
import request_result.AuthTokenRequest;
import request_result.UserResult;
import spark.Response;

public class LogoutService {
    public Object logout(AuthTokenRequest logoutData, AuthDAO authDAO, Response res) throws DataAccessException {
        try{
            if(authDAO.getAuth(logoutData.authToken()) == null){
                res.status(401);
                return new FailureRepsonse("Error: unauthorized");
            }
            authDAO.deleteAuth(logoutData.authToken());
            res.status(200);
            return "{}";
        } catch (DataAccessException err) {
            return new UserResult("Error", "error");
        }
    }
}
