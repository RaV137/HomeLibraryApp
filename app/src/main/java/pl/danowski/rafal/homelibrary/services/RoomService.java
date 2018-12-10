package pl.danowski.rafal.homelibrary.services;

import android.content.Context;

import java.util.List;

import pl.danowski.rafal.homelibrary.controllers.RoomController;
import pl.danowski.rafal.homelibrary.model.room.CreateRoom;
import pl.danowski.rafal.homelibrary.model.room.Room;
import pl.danowski.rafal.homelibrary.network.OnlineCheck;

public class RoomService {

    private final RoomController controller = new RoomController();

    public Room createRoom(Context context, CreateRoom room) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return null;
        }

        return controller.createRoom(room);
    }

    public boolean updateRoom(Context context, Room room) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return false;
        }

        return controller.updateRoom(room);
    }

    public Room findRoomById(Context context, int id) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return null;
        }

        return controller.findRoomById(id);
    }

    public List<Room> findRoomsByUserLogin(Context context, String login) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return null;
        }

        return controller.findRoomsByUserLogin(login);
    }

    public boolean deleteRoom(Context context, int id) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return false;
        }

        return controller.deleteRoom(id);
    }
}
