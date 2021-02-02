package websocket;

import java.util.HashMap;
import java.util.Random;


public class RoomList {

    private static RoomList instance = null;
    private static HashMap<String, Room> rooms = new HashMap<>();

    public synchronized Room getRoom(String roomID) {
        return rooms.getOrDefault(roomID.toUpperCase(), null);
    }

    public synchronized String createRoom(String password) {
        Room room = new Room(password);
        String roomID = createRoomCode();
        rooms.put(roomID, room);

        return roomID;
    }

    private synchronized  boolean roomExists(String roomID) {
        return rooms.containsKey(roomID);
    }

    synchronized void removeRoom(String roomID) {
        rooms.remove(roomID);
    }

    public synchronized static RoomList getRoomList() {
        if (instance == null) {
            instance = new RoomList();
        }
        return instance;
    }

    private synchronized String createRoomCode() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 6;
        Random random = new Random();
        String generatedString;

        do {
            generatedString = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        } while (RoomList.getRoomList().roomExists(generatedString));

        return generatedString.toUpperCase();
    }
}
