package pl.danowski.rafal.homelibrary.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.adapters.RoomGridAdapter;
import pl.danowski.rafal.homelibrary.model.room.Room;
import pl.danowski.rafal.homelibrary.services.RoomService;
import pl.danowski.rafal.homelibrary.utiities.enums.IntentExtras;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;

public class RoomsActivity extends AppCompatActivity {

    private List<Room> rooms;
    private ArrayAdapter<Room> adapter;
    private GridView gridViewRooms;
    private final RoomService mService = new RoomService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_rooms);

        rooms = new ArrayList<>();
        adapter = new RoomGridAdapter(getBaseContext(), (ArrayList<Room>) rooms);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null : "ActionBar is null!";
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Twoje pokoje");

        String login = SharedPreferencesUtilities.getLogin(this);
        GetUserRoomsTask task = new GetUserRoomsTask(login);
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

    private final class GetUserRoomsTask extends AsyncTask<Void, Void, Void> {

        private String login;

        GetUserRoomsTask(String login) {
            this.login = login;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            rooms = mService.findRoomsByUserLogin(getBaseContext(), login);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(rooms == null || rooms.size() == 0) {
                TextView noRoomsInfo = findViewById(R.id.noRoomsInfo);
                noRoomsInfo.setVisibility(View.VISIBLE);
                gridViewRooms.setVisibility(View.GONE);
                return;
            }
            adapter = new RoomGridAdapter(getBaseContext(), (ArrayList<Room>) rooms);
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

    private final class DeleteRoomTask extends AsyncTask<Void, Void, Void> {

        private Integer id;

        DeleteRoomTask(Integer id) {
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Room room = rooms.get(id);
            boolean success = mService.deleteRoom(getBaseContext(), room.getId());
            if(success) {
                rooms.remove(room);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(rooms == null || rooms.size() == 0) {
                TextView noRoomsInfo = findViewById(R.id.noRoomsInfo);
                noRoomsInfo.setVisibility(View.VISIBLE);
                gridViewRooms.setVisibility(View.GONE);
                return;
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void displayBooksFromRoom(Room room) {
        int id = room.getId();
        Intent intent = new Intent(this, BooksActivity.class);
        intent.putExtra(IntentExtras.USER_ID.getName(), room.getUserId());
        intent.putExtra(IntentExtras.ROOM_ID.getName(), id);
        startActivity(intent);
    }

    private void addRoom() {
        Intent intent = new Intent(this, AddEditRoomActivity.class);
        intent.putExtra(IntentExtras.EDIT_ROOM.getName(), false);
        startActivity(intent);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    private void sort() {
        rooms.sort(new Comparator<Room>() {
            @Override
            public int compare(Room r1, Room r2) {
                return r1.getName().toLowerCase().compareTo(r2.getName().toLowerCase());
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.sort:
                sort();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        DeleteRoomTask task = new DeleteRoomTask((int) id);
        task.execute((Void) null);
    }

    private void editRoom(long id) {
        Room room = rooms.get((int) id);
        int roomId = room.getId();
        Intent intent = new Intent(this, AddEditRoomActivity.class);
        intent.putExtra(IntentExtras.ROOM_ID.getName(), roomId);
        intent.putExtra(IntentExtras.EDIT_ROOM.getName(), true);
        startActivity(intent);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
