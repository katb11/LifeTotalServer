package websocket;

import java.util.HashMap;


public class RoomList {

    private static RoomList instance = null;
    private static HashMap<String, Room> rooms = new HashMap<>();

    public synchronized Room getRoom(String roomID) {
        return rooms.getOrDefault(roomID, null);
    }

    public synchronized void createRoom(String roomID, String password) {
        Room room = new Room(password);
        rooms.put(roomID, room);
    }

    public synchronized void removeRoom(String roomID) {
        rooms.remove(roomID);
    }

    public synchronized static RoomList getRoomList() {
        if (instance == null) {
            instance = new RoomList();
        }
        return instance;
    }
}
