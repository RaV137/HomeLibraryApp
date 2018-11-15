package pl.danowski.rafal.homelibrary;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import pl.danowski.rafal.homelibrary.dialogs.ConfirmLogOutDialog;

public class MainMenuActivity extends AppCompatActivity {

    private Button mMyBooksButton;
    private Button mMyRoomsButton;
    private Button mProfileButton;
    private Button mPremiumButton;
    private Button mSettingsButton;
    private Button mLogOutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null : "ActionBar is null!";
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("Home library");

        mMyBooksButton = findViewById(R.id.myBooks);
        mMyBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myBooks();
            }
        });
        mMyRoomsButton = findViewById(R.id.myRooms);
        mMyRoomsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRooms();
            }
        });
        mProfileButton = findViewById(R.id.profile);
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile();
            }
        });
        mPremiumButton = findViewById(R.id.premium);
        mPremiumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                premium();
            }
        });
        mSettingsButton = findViewById(R.id.settings);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings();
            }
        });
        mLogOutButton = findViewById(R.id.logOut);
        mLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

    }

    private void myBooks() {
        // TODO
    }

    private void myRooms() {
        // TODO
    }

    private void profile() {
        // TODO
    }

    private void premium() {
        // TODO
    }

    private void settings() {
        // TODO
    }

    private void logOut() {
        ConfirmLogOutDialog dialog = new ConfirmLogOutDialog();
        dialog.setContext(MainMenuActivity.this);
        dialog.show(getFragmentManager(), "ConfirmLogOutDialog");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
