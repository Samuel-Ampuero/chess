package dataAccessTests;

import dataAccess.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameDAOTests {
    private GameDAO sql;
    @BeforeEach
    public void createDatabases(){
        try {
            sql = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createPositiveTest(){
        try {
            sql.createGame("game");
            Assertions.assertNotNull(sql.listGames());

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createNegativeTest(){
        Assertions.assertThrows(DataAccessException.class, () -> sql.createGame(null));

    }

    @Test
    public void listPositiveTest(){
        try {
            sql.createGame("game");
            sql.createGame("game1");
            sql.createGame("game2");
            sql.createGame("game3");
            sql.createGame("game4");
            sql.createGame("game5");

            Assertions.assertEquals(sql.listGames().size(), 6);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void listNegativeTest(){
        try {
            sql.createGame("game");
            sql.createGame("game1");
            sql.createGame("game2");
            sql.createGame("game3");
            sql.createGame("game4");
            sql.createGame("game");
        } catch (DataAccessException e) {
            try {
                Assertions.assertEquals(sql.listGames().size(), 5);
            } catch (DataAccessException err) {
                throw new RuntimeException(err);
            }
        }
    }

    @Test
    public void getPositiveTest(){
        try {
            var gameID = sql.createGame("game");
            var test = sql.getGame(gameID);
            Assertions.assertNotNull(test);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void getNegativeTest(){
        try {
            sql.createGame("game");
            var test = sql.getGame(404);
            Assertions.assertNull(test);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @AfterEach
    @Test
    public void clearDatabase(){
        try {
            sql.createGame("test1");
            sql.createGame("test2");
            sql.deleteAllGames();
            Assertions.assertEquals(sql.listGames().size(), 0);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
