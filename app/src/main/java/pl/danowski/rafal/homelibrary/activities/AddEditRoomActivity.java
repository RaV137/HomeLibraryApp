package pl.danowski.rafal.homelibrary.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import lombok.AccessLevel;
import lombok.Setter;
import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.exceptions.NoNetworkConnectionException;
import pl.danowski.rafal.homelibrary.model.room.CreateRoom;
import pl.danowski.rafal.homelibrary.model.room.Room;
import pl.danowski.rafal.homelibrary.model.user.User;
import pl.danowski.rafal.homelibrary.network.BaseAsyncTask;
import pl.danowski.rafal.homelibrary.services.RoomService;
import pl.danowski.rafal.homelibrary.services.UserService;
import pl.danowski.rafal.homelibrary.utiities.enums.IntentExtras;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;
import pl.danowski.rafal.homelibrary.utiities.toast.NoNetworkConnectionToast;

public class AddEditRoomActivity extends AppCompatActivity {

    private boolean edit;
    private Room currRoom;
    private RoomService mService = RoomService.getInstance();
    private UserService mUserService = UserService.getInstance();
    private int userId;
    private EditText mRoomName;
    private EditText mShortRoomName;
    private Button colourPicker;

    @Setter(AccessLevel.PRIVATE)
    private int selectedColour;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    }

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
            FindRoomByIdTask task = new FindRoomByIdTask(roomId, actionBar, this);
            task.execute((Void) null);
        } else {
            actionBar.setTitle("Stwórz pokój");
            String login = SharedPreferencesUtilities.getLogin(this);
            FindUserByLoginTask task = new FindUserByLoginTask(login, this);
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
            if (edit) {
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
        CreateRoomTask task = new CreateRoomTask(room, this);
        task.execute((Void) null);
    }

    private void updateRoom() {
        UpdateRoomTask task = new UpdateRoomTask(this);
        task.execute((Void) null);
    }

    private class FindRoomByIdTask extends BaseAsyncTask<Void, Void, Void> {

        private int id;
        private android.support.v7.app.ActionBar actionBar;

        public FindRoomByIdTask(int id, ActionBar actionBar, Context context) {
            super(context);
            this.id = id;
            this.actionBar = actionBar;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            super.doInBackground(voids);
            try {
                currRoom = mService.findRoomById(mContext, id);
            } catch (NoNetworkConnectionException e) {
                NoNetworkConnectionToast.show(mContext);
            }
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

    private class CreateRoomTask extends BaseAsyncTask<Void, Void, Void> {

        private CreateRoom room;

        public CreateRoomTask(CreateRoom room, Context context) {
            super(context);
            this.room = room;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            super.doInBackground(voids);
            try {
                mService.createRoom(mContext, this.room); // TODO obsługa błędów
            } catch (NoNetworkConnectionException e) {
                NoNetworkConnectionToast.show(mContext);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            finish();
        }
    }

    private class UpdateRoomTask extends BaseAsyncTask<Void, Void, Void> {

        private UpdateRoomTask(Context context) {
           super(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            super.doInBackground(voids);
            try {
                mService.updateRoom(mContext, currRoom);
            } catch (NoNetworkConnectionException e) {
                NoNetworkConnectionToast.show(mContext);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            finish();
        }
    }

    private class FindUserByLoginTask extends BaseAsyncTask<Void, Void, Void> {

        private String login;
        private int id;

        FindUserByLoginTask(String login, Context context) {
            super(context);
            this.login = login;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            super.doInBackground(voids);
            User user;
            try {
                user = mUserService.findUserByLogin(mContext, login);
            } catch (NoNetworkConnectionException e) {
                NoNetworkConnectionToast.show(mContext);
                return null;
            }
            this.id = user.getId();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            userId = id;
        }
    }

}
