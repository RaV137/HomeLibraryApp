package pl.danowski.rafal.homelibrary.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.model.room.Room;

public class RoomGridAdapter extends ArrayAdapter<Room> {

    public RoomGridAdapter(Context context, ArrayList<Room> users) {
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        Room room = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.room_item, parent, false);
        }

        TextView mName = convertView.findViewById(R.id.roomName);
        TextView mShortName = convertView.findViewById(R.id.roomShortName);

        assert room != null;
        convertView.setBackgroundColor(parseColourFromString(room.getColour()));

        mName.setText(room.getName());
        mShortName.setText(String.format("(%s)", room.getShortName()));

        return convertView;
    }

    private int parseColourFromString(String colour) {
        return 0;
    }

}
