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
            connections.add(player.getAuthString(), session);
            String message = String.format("%s has joined the game\n", authMemory.getAuth(player.getAuthString()).username());

            var notification = new Notification(message);
            connections.broadcast(player.getAuthString(), notification);
            session.getRemote().sendString(new LoadGame(gameMemory.getGame(player.getGameID()).game()).toString());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void joinObserver(JoinObserver observer, Session session) throws IOException {
        try {
            connections.add(observer.getAuthString(), session);
            String message = String.format("%s is observing the game\n", authMemory.getAuth(observer.getAuthString()).username());

            var notification = new Notification(message);
            connections.broadcast(observer.getAuthString(), notification);
            session.getRemote().sendString(new LoadGame(gameMemory.getGame(observer.getGameID()).game()).toString());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void makeMove(MakeMove move, Session session) throws IOException {
        //Error cases
        try {
            if (!gameMemory.getGame(move.getGameID()).game().validMoves(move.getMove().getStartPosition()).contains(move.getMove())) {
                session.getRemote().sendString(new Error("Error: Invalid Move\n").toString());
                return;
            }

            ChessGame.TeamColor playerColor = null;
            if (Objects.equals(gameMemory.getGame(move.getGameID()).whiteUsername(), authMemory.getAuth(move.getAuthString()).username())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (Objects.equals(gameMemory.getGame(move.getGameID()).blackUsername(), authMemory.getAuth(move.getAuthString()).username())) {
                playerColor = ChessGame.TeamColor.BLACK;
            }
            if (gameMemory.getGame(move.getGameID()).game().getTeamTurn() != playerColor) {
                session.getRemote().sendString(new Error("Error: It is not your turn\n").toString());
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

            connections.broadcast(move.getAuthString(), new LoadGame(gameMemory.getGame(move.getGameID()).game()));
            message = String.format("%s has made their move\n", authMemory.getAuth(move.getAuthString()).username());

            var notification = new Notification(message);
            connections.broadcast(move.getAuthString(), notification);

            session.getRemote().sendString(new LoadGame(gameMemory.getGame(move.getGameID()).game()).toString());
            session.getRemote().sendString(new Notification("Successful Move!\n").toString());
        } catch (DataAccessException | InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }
    private void resign(Resign resign, Session session) throws IOException {
        try {
            String message = String.format("%s has resigned. Game Over.\n", authMemory.getAuth(resign.getAuthString()).username());
            var notification = new Notification(message);
            connections.broadcast(resign.getAuthString(), notification);
            session.getRemote().sendString(new Notification("You resigned. Game Over.\n").toString());

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void leave(Leave leave) throws IOException {
        try {
            connections.remove(leave.getAuthString());
            String message = String.format("%s has left the game", authMemory.getAuth(leave.getAuthString()).username());
            var notification = new Notification(message);
            connections.broadcast(leave.getAuthString(), notification);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}