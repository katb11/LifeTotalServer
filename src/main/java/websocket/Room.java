package websocket;

import model.PlayerState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.websocket.Session;

public class Room {
    public enum ConnectionStatus {
        SUCCESS,
        INVALID_ROOM,
        INVALID_PASSWORD
    }

    private HashMap<String, PlayerState> playerStates = new HashMap<>();
    private List<Session> sessions = new ArrayList<>();
    private String password;

    Room(String password) {
        this.password = password;
    }

    synchronized ConnectionStatus join(Session session, String password, String name) {
        if (this.password.equals(password)) {
            sessions.add(session);

            if (playerStates.get(name) == null) {
                PlayerState ps = new PlayerState();
                playerStates.put(name, ps);
            }
            return ConnectionStatus.SUCCESS;
        } else {
            return ConnectionStatus.INVALID_PASSWORD;
        }
    }

    synchronized boolean leave(Session session) {
        sessions.remove(session);
        return sessions.isEmpty();
    }

    synchronized void sendMessage(String message) {
        for (Session session: sessions) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized boolean validConnection(String password) {
        return this.password.equals(password);
    }

    HashMap<String, PlayerState> getRoomState() {
        return playerStates;
    }

    int updateLife(String player, String type) {
        PlayerState ps = playerStates.get(player);
        int lifeTotal = ps.getLifeTotal();

        switch(type) {
            case("INC1"):
                lifeTotal++;
                break;
            case("DEC1"):
                lifeTotal--;
                break;
        }

        ps.setLifeTotal(lifeTotal);
        playerStates.put(player, ps);
        return ps.getLifeTotal();
    }

    void removePlayer(String username) {
        playerStates.remove(username);
    }
}