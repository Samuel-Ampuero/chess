package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    var notification = new ServerMessage();
                    if(message.startsWith("{\"message")){
                        notification = new Gson().fromJson(message, Notification.class);
                    } else if (message.startsWith("{\"errorMessage")) {
                        notification = new Gson().fromJson(message, Error.class);
                    } else {
                        notification = new Gson().fromJson(message, LoadGame.class);
                    }
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinPlayer(String authToken, int gameID, ChessGame.TeamColor playerColor) throws ResponseException {
        try {
            var action = new JoinPlayer(gameID, playerColor, authToken);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void joinObserver(String authToken, int gameID) throws ResponseException {
        try {
            var action = new JoinObserver(gameID, authToken);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove chessMove) throws ResponseException {
        try {
            var action = new MakeMove(gameID, chessMove, authToken);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(String token, int gameID) throws ResponseException {
        try {
            var action = new Resign(gameID, token);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leave(String authToken, int game) throws ResponseException {
        try {
            var action = new Leave(game, authToken);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}
