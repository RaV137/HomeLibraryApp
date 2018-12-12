package pl.danowski.rafal.homelibrary.activities;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
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
import pl.danowski.rafal.homelibrary.adapters.BookGridAdapter;
import pl.danowski.rafal.homelibrary.adapters.RoomGridAdapter;
import pl.danowski.rafal.homelibrary.dialogs.ChangeEmailDialog;
import pl.danowski.rafal.homelibrary.dialogs.SortBooksDialog;
import pl.danowski.rafal.homelibrary.model.book.Book;
import pl.danowski.rafal.homelibrary.model.room.Room;
import pl.danowski.rafal.homelibrary.services.BookService;
import pl.danowski.rafal.homelibrary.utiities.enums.SortOptionEnum;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;

public class BooksActivity extends AppCompatActivity {

    private List<Book> books;
    private ArrayAdapter<Book> adapter;
    private GridView gridViewBooks;
    private final BookService mService= new BookService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_books);

        books = new ArrayList<>();
        gridViewBooks = findViewById(R.id.booksGrid);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null : "ActionBar is null!";
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.all_books_activity_title);

        String login = SharedPreferencesUtilities.getLogin(this);
        GetUserBooksTask task = new GetUserBooksTask(login);
        task.execute((Void) null);

        FloatingActionButton fab = findViewById(R.id.addBook);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBook();
            }
        });
    }

    private final class GetUserBooksTask extends AsyncTask<Void, Void, Void> {

        private String login;

        GetUserBooksTask(String login) {
            this.login = login;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            books = mService.findBooksByUserLogin(getBaseContext(), login);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(books == null || books.size() == 0) {
                TextView noRoomsInfo = findViewById(R.id.noBooksInfo);
                noRoomsInfo.setVisibility(View.VISIBLE);
                gridViewBooks.setVisibility(View.GONE);
                return;
            }
            adapter = new BookGridAdapter(getBaseContext(), (ArrayList<Book>) books);
            adapter.notifyDataSetChanged();
            gridViewBooks.invalidateViews();
            gridViewBooks.setAdapter(adapter);
            gridViewBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        final int position, long id) {
                    showBookInfo(books.get(position));
                }
            });
        }
    }

    private void showBookInfo(Book book) {

    }

    private void addBook() {

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

    private void sort() {
        SortBooksDialog dialog = new SortBooksDialog();
        dialog.setDialogResult(new SortBooksDialog.OnMyDialogResult(){
            @Override
            public void finish(SortOptionEnum sortOptionEnum) {
                switch (sortOptionEnum) {
                    case SORT_BY_SCORE_ASC:
                        sortByScoreAsc();
                        break;
                    case SORT_BY_SCORE_DESC:
                        sortByScoreDesc();
                        break;
                    case SORT_BY_AUTHOR_ASC:
                        sortByAuthorAsc();
                        break;
                    case SORT_BY_AUTHOR_DESC:
                        sortByAuthorDesc();
                        break;
                    case SORT_BY_TITLE_ASC:
                        sortByTitleAsc();
                        break;
                    case SORT_BY_TITLE_DESC:
                        sortByTitleDesc();
                        break;
                }
            }
        });
        dialog.show(getFragmentManager(), "SortBooksDialog");
    }

    @SuppressLint("NewApi")
    public void sortByScoreAsc() {
        books.sort(new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                // TODO rating == null
                return b1.getRating() - b2.getRating();
            }
        });
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    public void sortByScoreDesc() {
        books.sort(new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                // TODO rating == null
                return b2.getRating() - b1.getRating();
            }
        });
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    public void sortByTitleAsc() {
        books.sort(new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                return b1.getTitle().compareTo(b2.getTitle());
            }
        });
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    public void sortByTitleDesc() {
        books.sort(new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                return b2.getTitle().compareTo(b1.getTitle());
            }
        });
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    public void sortByAuthorAsc() {
        books.sort(new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                return b1.getAuthor().compareTo(b2.getAuthor());
            }
        });
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    public void sortByAuthorDesc() {
        books.sort(new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                return b2.getAuthor().compareTo(b1.getAuthor());
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
