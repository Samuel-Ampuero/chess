package websocket;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    //public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public final HashMap<Integer,ConcurrentHashMap<String, Connection>> connections = new HashMap<>(new ConcurrentHashMap<>());

//    public void add(String authToken, Session session) {
//        var connection = new Connection(authToken, session);
//        connections.put(authToken, connection);
//    }

    public void add(int gameID, String authToken, Session session) {
        var connection = new Connection(authToken, session);
        if (connections.containsKey(gameID)){
            connections.get(gameID).put(authToken, connection);
            return;
        }
        connections.put(gameID,new ConcurrentHashMap<>());
        connections.get(gameID).put(authToken,connection);
    }

    public void remove(int gameID, String authToken) {
        connections.get(gameID).remove(authToken);
    }

    public void broadcast(int gameID, String excludeAuthToken, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.get(gameID).values()) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludeAuthToken)) {
                    c.send(serverMessage.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.get(gameID).remove(c.authToken);
        }
    }

    public void broadcastClient(int gameID, String authToken, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.get(gameID).values()) {
            if (c.session.isOpen()) {
                if (c.authToken.equals(authToken)) {
                    c.send(serverMessage.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.get(gameID).remove(c.authToken);
        }
    }
}
