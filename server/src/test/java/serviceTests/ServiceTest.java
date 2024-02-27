package serviceTests;

import dataAccess.*;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import service.*;
import request_result.*;
import model.UserData;
import org.junit.jupiter.api.Test;

public class ServiceTest {

    @Test
    public void registerPositiveTest(){
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();

        RegistrationService service = new RegistrationService();
        try {
            var test = service.register(userDAO, new UserData("username", "password", "email"), authDAO);

            Assertions.assertInstanceOf(UserResult.class, test);
        } catch (DataAccessException err){
            return;
        }
    }

    @Test
    public void registerNegativeTest(){
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();

        RegistrationService service = new RegistrationService();
        try {
            userDAO.createUser("username", "password", "email");
            var test = service.register(userDAO, new UserData("username", "password", "email"), authDAO);

            Assertions.assertInstanceOf(FailureRepsonse.class, test);
        } catch (DataAccessException err){
            return;
        }
    }

    @Test
    public void loginPositiveTest(){
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();

        LoginService service = new LoginService();
        try{
            userDAO.createUser("username", "password", "email");

            var test = service.login(userDAO, new LoginRequest("username","password"), authDAO);

            Assertions.assertInstanceOf(UserResult.class,test);
        } catch (DataAccessException err){
            throw new AssertionFailedError("Failed");
        }
    }

    @Test
    public void loginNegativeTest(){
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();

        LoginService service = new LoginService();
        try{
            userDAO.createUser("username", "password", "email");

            var test = service.login(userDAO, new LoginRequest("username","not the password"), authDAO);

            Assertions.assertInstanceOf(FailureRepsonse.class,test);
        } catch (DataAccessException err){
            throw new AssertionFailedError("Failed");
        }
    }

    @Test
    public void logoutPositiveTest(){
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();

        LogoutService service = new LogoutService();
        try{
            userDAO.createUser("username", "password", "email");
            String authToken = authDAO.createAuth("username");

            var test = service.logout(new AuthTokenRequest(authToken),authDAO);

            Assertions.assertInstanceOf(SucessResult.class,test);
        } catch (DataAccessException err){
            throw new AssertionFailedError("Failed");
        }
    }

    @Test
    public void logoutNegativeTest(){
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();

        LogoutService service = new LogoutService();
        try{
            userDAO.createUser("username", "password", "email");
            authDAO.createAuth("username");

            var test = service.logout(new AuthTokenRequest("not a token"),authDAO);

            Assertions.assertInstanceOf(FailureRepsonse.class,test);
        } catch (DataAccessException err){
            throw new AssertionFailedError("Failed");
        }
    }

    @Test
    public void listPositiveTest(){
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameDAO emptyGameDAO = new MemoryGameDAO();

        ListGamesService service = new ListGamesService();
        try{
            userDAO.createUser("username", "password", "email");
            String authToken = authDAO.createAuth("username");

            gameDAO.createGame("game");
            gameDAO.createGame("game2");
            gameDAO.createGame("game3");
            gameDAO.createGame("game4");

            var test = service.listGames(new AuthTokenRequest(authToken), authDAO, gameDAO);
            Assertions.assertInstanceOf(GameListResult.class, test);
            Assertions.assertNotEquals(gameDAO, emptyGameDAO);
        } catch (DataAccessException err){
            throw new AssertionFailedError("Failed");
        }
    }

    @Test
    public void listNegativeTest(){
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameDAO emptyGameDAO = new MemoryGameDAO();

        ListGamesService service = new ListGamesService();
        try{
            userDAO.createUser("username", "password", "email");
            String authToken = authDAO.createAuth("username");

            gameDAO.createGame("game");
            gameDAO.createGame("game2");
            gameDAO.createGame("game3");
            gameDAO.createGame("game4");

            var test = service.listGames(new AuthTokenRequest("not a token"), authDAO, gameDAO);
            Assertions.assertInstanceOf(FailureRepsonse.class, test);
        } catch (DataAccessException err){
            throw new AssertionFailedError("Failed");
        }
    }

    @Test
    public void createPositiveTest(){
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        CreateGameService service = new CreateGameService();

        try{
            userDAO.createUser("username", "password", "email");
            String authToken = authDAO.createAuth("username");

            var test = service.createGame(new CreateGameRequest("game"),new AuthTokenRequest(authToken), authDAO, gameDAO);

            Assertions.assertInstanceOf(CreateGameResult.class, test);

        } catch (DataAccessException err){
            throw new AssertionFailedError("Failed");
        }
    }

    @Test
    public void createNegativeTest(){
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        CreateGameService service = new CreateGameService();

        try{
            var test = service.createGame(new CreateGameRequest("game"), null, authDAO, gameDAO);

            Assertions.assertInstanceOf(FailureRepsonse.class, test);

        } catch (DataAccessException err){
            throw new AssertionFailedError("Failed");
        }
    }

    @Test
    public void joinPositiveTest(){
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        JoinGameService service = new JoinGameService();
        try{
            userDAO.createUser("username1","password1", "email1");
            userDAO.createUser("username2","password2", "email2");
            String authToken1 = authDAO.createAuth("username1");
            String authToken2 = authDAO.createAuth("username2");
            int gameID = gameDAO.createGame("game");
            service.joinGame(new JoinGameRequest("WHITE", gameID), new AuthTokenRequest(authToken1), authDAO, gameDAO);
            service.joinGame(new JoinGameRequest("BLACK", gameID), new AuthTokenRequest(authToken2), authDAO, gameDAO);

            Assertions.assertEquals(gameDAO.getGame(gameID).gameName(), "game");
            Assertions.assertEquals(gameDAO.getGame(gameID).whiteUsername(), "username1");
            Assertions.assertEquals(gameDAO.getGame(gameID).blackUsername(), "username2");

        } catch (DataAccessException err){
            throw new AssertionFailedError("Failed");
        }
    }

    @Test
    public void joinNegativTest(){
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        JoinGameService service = new JoinGameService();
        try{
            userDAO.createUser("username1","password1", "email1");
            userDAO.createUser("username2","password2", "email2");
            String authToken1 = authDAO.createAuth("username1");
            String authToken2 = authDAO.createAuth("username2");
            int gameID = gameDAO.createGame("game");
            service.joinGame(new JoinGameRequest("WHITE", gameID), new AuthTokenRequest(authToken1), authDAO, gameDAO);
            var test = service.joinGame(new JoinGameRequest("WHITE", gameID), new AuthTokenRequest(authToken2), authDAO, gameDAO);

            Assertions.assertInstanceOf(FailureRepsonse.class, test);

        } catch (DataAccessException err){
            throw new AssertionFailedError("Failed");
        }
    }

    @Test
    public void clearTest(){
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        ClearService clearService = new ClearService();

        try{
            userDAO.createUser("username", "password", "email");
            userDAO.createUser("username1", "password1", "email1");
            userDAO.createUser("username2", "password2", "email2");
            userDAO.createUser("username3", "password3", "email3");
            authDAO.createAuth("username");
            authDAO.createAuth("username1");
            authDAO.createAuth("username2");
            authDAO.createAuth("username3");
            gameDAO.createGame("game");
            gameDAO.createGame("game1");
            gameDAO.createGame("game2");
            gameDAO.createGame("game3");

            var test = clearService.clear(userDAO, authDAO, gameDAO);

            Assertions.assertInstanceOf(SucessResult.class, test);

        } catch (DataAccessException err){
            throw new AssertionFailedError("Failed");
        }

    }
}
