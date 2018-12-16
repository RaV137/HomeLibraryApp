package pl.danowski.rafal.homelibrary.activities;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pl.danowski.rafal.homelibrary.R;

public class BookInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    }
}
