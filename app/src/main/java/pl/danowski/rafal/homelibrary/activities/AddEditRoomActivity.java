package pl.danowski.rafal.homelibrary.activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.model.room.CreateRoom;
import pl.danowski.rafal.homelibrary.model.room.Room;
import pl.danowski.rafal.homelibrary.model.user.User;
import pl.danowski.rafal.homelibrary.services.RoomService;
import pl.danowski.rafal.homelibrary.services.UserService;
import pl.danowski.rafal.homelibrary.utiities.PasswordEncrypter;
import pl.danowski.rafal.homelibrary.utiities.enums.IntentExtras;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;
import pl.danowski.rafal.homelibrary.utiities.validators.Validator;
import top.defaults.colorpicker.ColorPickerPopup;

public class AddEditRoomActivity extends AppCompatActivity {

    private boolean edit;
    private Room currRoom;
    private RoomService mService = new RoomService();
    private UserService mUserService = new UserService();
    private int userId;
    private EditText mRoomName;
    private EditText mShortRoomName;
    private Button colourPicker;

    @Setter(AccessLevel.PRIVATE)
    private int selectedColour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_room);

        colourPicker = findViewById(R.id.colorPicker);
        mRoomName = findViewById(R.id.roomName);
        mShortRoomName = findViewById(R.id.shortName);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null : "ActionBar is null!";
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        Boolean editRoom = false;
        if (extras != null) {
            editRoom = extras.getBoolean(IntentExtras.EDIT_ROOM.getName());
        }

        this.edit = editRoom;

        if (edit) {
            Integer roomId = extras.getInt(IntentExtras.ROOM_ID.getName());
            FindRoomByIdTask task = new FindRoomByIdTask(roomId, actionBar);
            task.execute((Void) null);
        } else {
            actionBar.setTitle("Stwórz pokój");
            String login = SharedPreferencesUtilities.getLogin(this);
            FindUserByLoginTask task = new FindUserByLoginTask(login);
            task.execute((Void) null);
        }

        Button okButton = findViewById(R.id.accept);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accept();
            }
        });

        colourPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colourPickerOnClickListener();
            }
        });
    }

    private void colourPickerOnClickListener() {
        int colour;
        if (edit) {
            colour = Color.parseColor(currRoom.getColour());
        } else {
            colour = Color.WHITE;
        }
        AlertDialog dialog = ColorPickerDialogBuilder
                .with(this)
                .setTitle("Wybierz kolor")
                .initialColor(colour)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(8)
                .setPositiveButton("Ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        setSelectedColour(selectedColor);
                        colourPicker.setBackgroundColor(selectedColor);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .build();
        dialog.show();
    }

    private void accept() {
        // TODO: walidacja pól
        View focusView = null;
        boolean correct = true;

        String roomName = mRoomName.getText().toString();
        String shortName = mShortRoomName.getText().toString();

        if (TextUtils.isEmpty(roomName)) {
            focusView = mRoomName;
            mRoomName.setError("Pole wymagane");
            correct = false;
        } else if (TextUtils.isEmpty(shortName)) {
            focusView = mShortRoomName;
            mShortRoomName.setError("Pole wymagane");
            correct = false;
        }


        if (correct) {
            if(edit) {
                currRoom.setName(roomName);
                currRoom.setShortName(shortName);
                currRoom.setColour(getStringFromColorId(selectedColour));
                updateRoom();
            } else {
                CreateRoom room = new CreateRoom();
                room.setUserId(userId);
                room.setName(roomName);
                room.setShortName(shortName);
                room.setColour(getStringFromColorId(selectedColour));
                createRoom(room);
            }
        } else {
            focusView.requestFocus();
        }
    }

    private void createRoom(CreateRoom room) {
        CreateRoomTask task = new CreateRoomTask(room);
        task.execute((Void) null);
    }

    private void updateRoom() {
        UpdateRoomTask task = new UpdateRoomTask();
        task.execute((Void) null);
    }

    private class FindRoomByIdTask extends AsyncTask<Void, Void, Void> {

        private int id;
        private android.support.v7.app.ActionBar actionBar;

        public FindRoomByIdTask(int id, android.support.v7.app.ActionBar actionBar) {
            this.id = id;
            this.actionBar = actionBar;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            currRoom = mService.findRoomById(getBaseContext(), id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            actionBar.setTitle(currRoom.getName());
            mRoomName.setText(currRoom.getName());
            mShortRoomName.setText(currRoom.getShortName());
            setSelectedColour(Color.parseColor(currRoom.getColour()));
            colourPicker.setBackgroundColor(selectedColour);
            userId = currRoom.getUserId();
        }
    }

    private String getStringFromColorId(int id) {
        return String.format("#%06X", (0xFFFFFF & id));
    }

    private class CreateRoomTask extends AsyncTask<Void, Void, Void> {

        private CreateRoom room;

        public CreateRoomTask(CreateRoom room) {
            this.room = room;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mService.createRoom(getBaseContext(), this.room); // TODO obsługa błędów
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            finish();
        }
    }

    private class UpdateRoomTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mService.updateRoom(getBaseContext(), currRoom);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            finish();
        }
    }

    private class FindUserByLoginTask extends AsyncTask<Void, Void, Void> {

        private String login;
        private int id;

        public FindUserByLoginTask(String login) {
            this.login = login;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            User user = mUserService.findUserByLogin(getBaseContext(), login);
            this.id = user.getId();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            userId = id;
        }
    }

}
