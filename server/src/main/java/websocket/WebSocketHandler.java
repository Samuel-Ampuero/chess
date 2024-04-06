package websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import exception.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    private final AuthDAO authMemory;
    private final GameDAO gameMemory;

    public WebSocketHandler(AuthDAO authMemory, GameDAO gameMemory){
        this.authMemory = authMemory;
        this.gameMemory = gameMemory;
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        if (action.getCommandType() == UserGameCommand.CommandType.JOIN_PLAYER){
            action = new Gson().fromJson(message, JoinPlayer.class);
            joinPlayer((JoinPlayer) action, session);
        } else if (action.getCommandType() == UserGameCommand.CommandType.JOIN_OBSERVER){
            action = new Gson().fromJson(message, JoinObserver.class);
            joinObserver((JoinObserver) action, session);
        } else if (action.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE){
            action = new Gson().fromJson(message, MakeMove.class);
            makeMove((MakeMove) action, session);
        }  else if (action.getCommandType() == UserGameCommand.CommandType.RESIGN){
            action = new Gson().fromJson(message, Resign.class);
            resign((Resign) action, session);
        } else if (action.getCommandType() == UserGameCommand.CommandType.LEAVE){
            action = new Gson().fromJson(message, Leave.class);
            leave((Leave) action);
        }
    }

    private void joinPlayer(JoinPlayer player, Session session) throws IOException {
        try {
            ChessGame.TeamColor requestColor = player.getPlayerColor();
            if(gameMemory.getGame(player.getGameID()) == null){
                session.getRemote().sendString(new Gson().toJson(new Error("Error: Game does not exist\n")));
                return;
            }
            if(authMemory.getAuth(player.getAuthString()) == null){
                session.getRemote().sendString(new Gson().toJson(new Error("Error: Not Authorized\n")));
                return;
            }
            if((requestColor == ChessGame.TeamColor.WHITE && !Objects.equals(gameMemory.getGame(player.getGameID()).whiteUsername(), authMemory.getAuth(player.getAuthString()).username()))
                    ||(requestColor == ChessGame.TeamColor.BLACK && !Objects.equals(gameMemory.getGame(player.getGameID()).blackUsername(), authMemory.getAuth(player.getAuthString()).username()))){
                if((requestColor == ChessGame.TeamColor.WHITE && gameMemory.getGame(player.getGameID()).whiteUsername() != null)
                        ||(requestColor == ChessGame.TeamColor.BLACK && gameMemory.getGame(player.getGameID()).blackUsername() != null)){
                    session.getRemote().sendString(new Gson().toJson(new Error("Error: This team is already taken\n")));
                    return;
                }
                session.getRemote().sendString(new Gson().toJson(new Error("Error: You are not on this team\n")));
                return;
            }

            connections.add(player.getGameID(), player.getAuthString(), session);
            String message = String.format("%s has joined the game\n", authMemory.getAuth(player.getAuthString()).username());

            var notification = new Notification(message);
            connections.broadcast(player.getGameID(), player.getAuthString(), notification);
            connections.broadcastClient(player.getGameID(), player.getAuthString(), new LoadGame(gameMemory.getGame(player.getGameID()).game()));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void joinObserver(JoinObserver observer, Session session) throws IOException {
        try {
            if(gameMemory.getGame(observer.getGameID()) == null){
                session.getRemote().sendString(new Gson().toJson(new Error("Error: Game does not exist\n")));
                return;
            }
            if(authMemory.getAuth(observer.getAuthString()) == null){
                session.getRemote().sendString(new Gson().toJson(new Error("Error: Not Authorized\n")));
                return;
            }

            connections.add(observer.getGameID(), observer.getAuthString(), session);
            String message = String.format("%s is observing the game\n", authMemory.getAuth(observer.getAuthString()).username());

            var notification = new Notification(message);
            connections.broadcast(observer.getGameID(), observer.getAuthString(), notification);
            connections.broadcastClient(observer.getGameID(), observer.getAuthString(), new LoadGame(gameMemory.getGame(observer.getGameID()).game()));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void makeMove(MakeMove move, Session session) throws IOException {
        //Error cases
        try {
            if (!gameMemory.getGame(move.getGameID()).game().validMoves(move.getMove().getStartPosition()).contains(move.getMove())) {
                connections.broadcastClient(move.getGameID(), move.getAuthString(), new Error("Error: Invalid Move\n"));
                return;
            }

            ChessGame.TeamColor playerColor = null;
            if (Objects.equals(gameMemory.getGame(move.getGameID()).whiteUsername(), authMemory.getAuth(move.getAuthString()).username())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (Objects.equals(gameMemory.getGame(move.getGameID()).blackUsername(), authMemory.getAuth(move.getAuthString()).username())) {
                playerColor = ChessGame.TeamColor.BLACK;
            }
            if (gameMemory.getGame(move.getGameID()).game().getTeamTurn() != playerColor) {
                connections.broadcastClient(move.getGameID(), move.getAuthString(), new Error("Error: It is not your turn\n"));
                return;
            }
            if(gameMemory.getGame(move.getGameID()).game().isGameOver()) {
                connections.broadcastClient(move.getGameID(), move.getAuthString(), new Error("Game is already over.\n"));
                return;
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            String message;

            GameData game = gameMemory.getGame(move.getGameID());
            game.game().makeMove(move.getMove());
            gameMemory.updateGame(move.getGameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());

            connections.broadcast(move.getGameID(), move.getAuthString(), new LoadGame(gameMemory.getGame(move.getGameID()).game()));
            message = String.format("%s has made their move\n", authMemory.getAuth(move.getAuthString()).username());

            var notification = new Notification(message);
            connections.broadcast(move.getGameID(), move.getAuthString(), notification);
            session.getRemote().sendString(new Gson().toJson(new LoadGame(gameMemory.getGame(move.getGameID()).game())));
        } catch (DataAccessException | InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }
    private void resign(Resign resign, Session session) throws IOException {
        try {
            String user = authMemory.getAuth(resign.getAuthString()).username();
            if (!Objects.equals(user, gameMemory.getGame(resign.getGameID()).whiteUsername())
                    || !Objects.equals(user, gameMemory.getGame(resign.getGameID()).blackUsername())) {
                if (!Objects.equals(user, gameMemory.getGame(resign.getGameID()).whiteUsername())
                        && !Objects.equals(user, gameMemory.getGame(resign.getGameID()).blackUsername())) {
                    connections.broadcastClient(resign.getGameID(), resign.getAuthString(), new Error("You can't resign as an observer.\n"));
                    return;
                }
            }
            if(gameMemory.getGame(resign.getGameID()).game().isGameOver()) {
                connections.broadcastClient(resign.getGameID(), resign.getAuthString(), new Error("Game is already over.\n"));
                return;
            }

            String message = String.format("%s has resigned. Game Over.\n", authMemory.getAuth(resign.getAuthString()).username());
            var temp = gameMemory.getGame(resign.getGameID());
            temp.game().setGameOver();
            //gameMemory.getGame(resign.getGameID()).updateGameData(temp.whiteUsername(), temp.blackUsername(), temp.gameName(), temp.game());
            gameMemory.updateGame(temp.gameID(), temp.whiteUsername(), temp.blackUsername(), temp.gameName(), temp.game());
            var notification = new Notification(message);
            connections.broadcast(resign.getGameID(), resign.getAuthString(), notification);
            connections.broadcastClient(resign.getGameID(), resign.getAuthString(), new Notification("You resigned. Game Over.\n"));

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void leave(Leave leave) throws IOException {
        try {
            connections.remove(leave.getGameID(), leave.getAuthString());
            String message = String.format("%s has left the game", authMemory.getAuth(leave.getAuthString()).username());
            var notification = new Notification(message);
            connections.broadcast(leave.getGameID(), leave.getAuthString(), notification);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}