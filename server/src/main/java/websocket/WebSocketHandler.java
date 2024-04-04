package websocket;

import com.google.gson.Gson;
import dataAccess.*;
import exception.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.*;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    private final AuthDAO authMemory;
    private final UserDAO userMemory;
    private final GameDAO gameMemory;

    public WebSocketHandler(AuthDAO authMemory, UserDAO userMemory, GameDAO gameMemory){
        this.authMemory = authMemory;
        this.userMemory = userMemory;
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
            //FIXME:: IMPLEMENT
        } else if (action.getCommandType() == UserGameCommand.CommandType.LEAVE){
            action = new Gson().fromJson(message, Leave.class);
            //FIXME:: IMPLEMENT
        } else if (action.getCommandType() == UserGameCommand.CommandType.RESIGN){
            action = new Gson().fromJson(message, Resign.class);
            //FIXME:: IMPLEMENT
        }
    }

    private void joinPlayer(JoinPlayer player, Session session) throws IOException {
        connections.add(player.getAuthString(), session);
        String message;
        try {
            message = String.format("%s has joined the game", authMemory.getAuth(player.getAuthString()).username());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        var notification = new Notification(message);
        connections.broadcast(player.getAuthString(), notification);
        try {
            session.getRemote().sendString(new LoadGame(gameMemory.getGame(player.getGameID()).game()).toString());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void joinObserver(JoinObserver player, Session session) throws IOException {
        connections.add(player.getAuthString(), session);
        String message;
        try {
            message = String.format("%s is observing the game", authMemory.getAuth(player.getAuthString()).username());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        var notification = new Notification(message);
        connections.broadcast(player.getAuthString(), notification);
        try {
            session.getRemote().sendString(new LoadGame(gameMemory.getGame(player.getGameID()).game()).toString());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

//    private void exit(String authToken) throws IOException {
//        connections.remove(authToken);
//        var message = String.format("%s left the shop", authToken);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(authToken, notification);
//    }

}