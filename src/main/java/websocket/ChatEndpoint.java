package websocket;

import java.util.Map;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import model.PlayerAction;

import static model.PlayerAction.PlayerUpdate.*;

@ServerEndpoint(value = "/room/{roomID}")
public class ChatEndpoint {
    private Logger log = Logger.getLogger(ChatEndpoint.class.getSimpleName());

    private RoomList roomList = RoomList.getRoomList();
    private Room room;

    private volatile String roomID;

    @OnOpen
    public void init(@PathParam("roomID") String roomID, Session session) {
        this.roomID = roomID;
        room = roomList.getRoom(roomID);

        if (room == null) {
            log.info("Error: room doesn't exist.");
            this.onClose(session, new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Room Does Not Exist."));
        }
    }

    @OnMessage
    public void onMessage(final Session session, final String messageJson) {
        Gson gson = new Gson();
        PlayerAction action = gson.fromJson(messageJson, PlayerAction.class);
        Map<String, Object> properties = session.getUserProperties();

        switch(action.getAction()) {
            case LOGIN:
                properties.put("name", action.getUsername());

                Room.ConnectionStatus status = room.join(session,  action.getDetails(), action.getUsername());
                if (status == Room.ConnectionStatus.SUCCESS) {

                    String json = gson.toJson(room.getRoomState());
                    PlayerAction response = new PlayerAction(action.getUsername(), JOINED, json);

                    room.sendMessage(gson.toJson(response));
                }
                break;
            case UPDATE_LIFETOTAL: {
                room.updateLife(action.getUsername(), action.getDetails());
                String json = gson.toJson(room.getRoomState());
                PlayerAction response = new PlayerAction(action.getUsername(), UPDATE_LIFETOTAL, json);
                room.sendMessage(gson.toJson(response));
                break;
            }
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        Gson gson = new Gson();
        PlayerAction response = new PlayerAction(session.getUserProperties().get("name").toString(), LEAVE, "");
        room.sendMessage(gson.toJson(response));

        if (room.leave(session)) {
            roomList.removeRoom(roomID);
            room = null;
        }
    }

    @OnError
    public void onError(Session session, Throwable ex) {
        log.info("Error: " + ex.getMessage());
    }
}