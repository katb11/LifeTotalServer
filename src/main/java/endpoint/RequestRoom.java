package endpoint;

import model.User;
import org.json.JSONObject;
import service.JWT;
import org.apache.commons.io.IOUtils;
import websocket.Room;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static websocket.RoomList.getRoomList;

/* This class sets up a private room for a user */
@WebServlet("/requestRoom")
public class RequestRoom extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String body = IOUtils.toString(request.getReader());
        JSONObject obj = new JSONObject(body);

        String token = obj.getString("token");
        String room = null;
        String pass = null;

        if (obj.has("room") && obj.has("password")) {
            room = obj.getString("room");
            pass = obj.getString("password");
        }

        User u = JWT.decodeJWT(token);

        if (u != null) {
            if (room == null || pass == null) {
                String roomID = "test";
                String password = Integer.toString(u.getId());
                getRoomList().createRoom("test", password);

                JSONObject resp = new JSONObject();
                resp.put("room", roomID);
                resp.put("password", password);

                response.getWriter().write(resp.toString());
            } else {
                Room r = getRoomList().getRoom(room);
                if (r != null && r.validConnection(pass)) {
                    JSONObject resp = new JSONObject();
                    resp.put("room", room);
                    resp.put("password", pass);

                    response.getWriter().write(resp.toString());
                } else {
                    response.getWriter().write("failure");
                }
            }
        } else {
            response.getWriter().write("failure");
        }
    }
}