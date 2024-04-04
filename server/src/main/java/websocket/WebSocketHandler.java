package websocket;

import com.google.gson.Gson;
import dataAccess.*;
import exception.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.UserGameCommand;

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
        UserGameCommand action = new UserGameCommand();
        if (message.startsWith("{\"gameID")){
            action = new Gson().fromJson(message, JoinPlayer.class);
            joinPlayer((JoinPlayer) action, session);
        }
//        switch (action.getCommandType()) {
//            case JOIN_PLAYER -> joinPlayer(action.getAuthString(), session);
////            case JOIN_OBSERVER -> exit(action.getAuthString());
////            case MAKE_MOVE -> enter(action.getAuthString(), session);
////            case LEAVE -> exit(action.getAuthString());
////            case RESIGN -> exit(action.getAuthString());
//        }
    }

    private void joinPlayer(JoinPlayer player, Session session) throws IOException {
        connections.add(player.getAuthString(), session);
        System.out.println("GOT HERE");
        String message;
        try {
            message = String.format("%s is in the shop", authMemory.getAuth(player.getAuthString()).username());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        var notification = new Notification(message);
        connections.broadcast(player.getAuthString(), notification);
        //FIXME:: NEED TO FIX "GAME"
        session.getRemote().sendString(new LoadGame("test").toString());
    }

//    private void exit(String authToken) throws IOException {
//        connections.remove(authToken);
//        var message = String.format("%s left the shop", authToken);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(authToken, notification);
//    }

}