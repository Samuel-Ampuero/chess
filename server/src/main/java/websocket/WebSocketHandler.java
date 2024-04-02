package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(action.getAuthString(), session);
//            case JOIN_OBSERVER -> exit(action.getAuthString());
//            case MAKE_MOVE -> enter(action.getAuthString(), session);
//            case LEAVE -> exit(action.getAuthString());
//            case RESIGN -> exit(action.getAuthString());
        }
    }

    private void joinPlayer(String authToken, Session session) throws IOException {
        connections.add(authToken, session);
        //FIXME:: HOW TO GET NAME FROM DATABASE?
        var message = String.format("%s is in the shop", authToken);
        var notification = new Notification(message);
        connections.broadcast(authToken, notification);
    }

//    private void exit(String authToken) throws IOException {
//        connections.remove(authToken);
//        var message = String.format("%s left the shop", authToken);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(authToken, notification);
//    }

}