package pl.danowski.rafal.homelibrary.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.adapters.RoomGridAdapter;
import pl.danowski.rafal.homelibrary.model.room.Room;
import pl.danowski.rafal.homelibrary.services.RoomService;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;

public class RoomsActivity extends AppCompatActivity {

    @Getter
    private List<Room> rooms;

    private GridView gridViewRooms;
    private final RoomService mService = new RoomService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        rooms = new ArrayList<>();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null : "ActionBar is null!";
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Twoje pokoje");

        String login = SharedPreferencesUtilities.getLogin(this);
        GetUserRooms task = new GetUserRooms(login);
        task.execute((Void) null);

        FloatingActionButton fab = findViewById(R.id.addRoom);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRoom();
            }
        });

        gridViewRooms = findViewById(R.id.roomsGrid);
        registerForContextMenu(gridViewRooms);
    }

    private final class GetUserRooms extends AsyncTask<Void, Void, Void> {

        private String login;

        GetUserRooms(String login) {
            this.login = login;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            rooms = mService.findRoomsByUserLogin(getBaseContext(), login);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayAdapter<Room> adapter = new RoomGridAdapter(getBaseContext(), (ArrayList<Room>) rooms);
            adapter.notifyDataSetChanged();
            gridViewRooms.invalidateViews();
            gridViewRooms.setAdapter(adapter);
            gridViewRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        final int position, long id) {
                    displayBooksFromRoom(rooms.get(position));
                }
            });
        }
    }

    private void displayBooksFromRoom(Room room) {
        int id = room.getId();
        Intent intent = new Intent(this, BooksActivity.class);
        intent.putExtra(AcitivitiesConstats.USER_ID, room.getUserId());
        startActivity(intent);
    }

    private void addRoom() {
        Intent intent = new Intent(this, AddEditRoomActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.room_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit:
                editRoom(info.id);
                return true;
            case R.id.delete:
                deleteRoom(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteRoom(long id) {
        Room room = rooms.get((int) id);
        mService.deleteRoom(this, room.getId()); // TODO async task
    }

    private void editRoom(long id) {
        Room room = rooms.get((int) id);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
