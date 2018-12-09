package pl.danowski.rafal.homelibrary.activities;

import android.app.DialogFragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import pl.danowski.rafal.homelibrary.R;
//import pl.danowski.rafal.homelibrary.dialogs.SortBooksDialog;
//import pl.danowski.rafal.homelibrary.utiities.BookUtility;

public class AllBooksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_books);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null : "ActionBar is null!";
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.all_books_activity_title);

//        BookUtility.initialize(findViewById(R.id.allBooksLayout));
//        BookUtility.printBooks();

//        FloatingActionButton fab = findViewById(R.id.addBook);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addBook();
//            }
//        });
    }

//    private void addBook() {
//        BookUtility.addBind();
//    }

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

    private void sort() {
//        DialogFragment dialog = new SortBooksDialog();
//        dialog.show(getFragmentManager(), "SortBooksDialog");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
