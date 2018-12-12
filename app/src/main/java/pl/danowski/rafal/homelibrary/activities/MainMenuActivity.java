package pl.danowski.rafal.homelibrary.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;

public class MainMenuActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null : "ActionBar is null!";
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("Home library");

        boolean userLoggedIn = SharedPreferencesUtilities.isUserLoggedIn(getApplicationContext());

        Button myBooksButton = findViewById(R.id.myBooks);
        myBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myBooks();
            }
        });
        Button myRoomsButton = findViewById(R.id.myRooms);
        myRoomsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRooms();
            }
        });
        Button profileButton = findViewById(R.id.profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile();
            }
        });
        Button premiumButton = findViewById(R.id.premium);
        premiumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                premium();
            }
        });
        Button settingsButton = findViewById(R.id.search);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings();
            }
        });
        Button logOutButton = findViewById(R.id.logOut);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

    }

    private void myBooks() {
        startActivity(new Intent(this, BooksActivity.class));
    }

    private void myRooms() {
        startActivity(new Intent(this, RoomsActivity.class));
    }

    private void profile() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    private void premium() {
        Toast.makeText(this, "Zawartość czasowo niedostępna", Toast.LENGTH_SHORT).show();
    }

    private void settings() {
        // TODO
    }

    private void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wylogowywanie")
                .setMessage("Czy na pewno chcesz się wylogować?")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferencesUtilities.deleteLogin(getApplicationContext());
                        SharedPreferencesUtilities.setAutologin(getApplicationContext(), false);
                        finish();
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
        // TODO - nic nie rób = wyświetlanie tylko w pozycji portretowej
    }

}
