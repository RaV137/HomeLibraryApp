package pl.danowski.rafal.homelibrary.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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

import lombok.Getter;
import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.adapters.BookGridAdapter;
import pl.danowski.rafal.homelibrary.dialogs.SortBooksDialog;
import pl.danowski.rafal.homelibrary.exceptions.NoNetworkConnectionException;
import pl.danowski.rafal.homelibrary.model.book.Book;
import pl.danowski.rafal.homelibrary.network.BaseAsyncTask;
import pl.danowski.rafal.homelibrary.services.BookService;
import pl.danowski.rafal.homelibrary.utiities.enums.IntentExtras;
import pl.danowski.rafal.homelibrary.utiities.enums.SortOptionEnum;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;
import pl.danowski.rafal.homelibrary.utiities.toast.NoNetworkConnectionToast;

public class BooksActivity extends AppCompatActivity {

    private List<Book> allBooks;
    private List<Book> currBooks;
    private ArrayAdapter<Book> adapter;
    private GridView gridViewBooks;
    private final BookService mService = BookService.getInstance();

    private Integer roomId = null;

    @Getter
    private static List<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (AsyncTask task : tasks) {
            if (task == null)
                continue;
            AsyncTask.Status status = task.getStatus();
            if (status.equals(AsyncTask.Status.PENDING) || status.equals(AsyncTask.Status.RUNNING)) {
                task.cancel(true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_books);

        currBooks = new ArrayList<>();
        gridViewBooks = findViewById(R.id.booksGrid);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null : "ActionBar is null!";
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.all_books_activity_title);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            roomId = extras.getInt(IntentExtras.ROOM_ID.getName());
        }

        String login = SharedPreferencesUtilities.getLogin(this);
        GetUserBooksTask task = new GetUserBooksTask(login, this);
        task.execute((Void) null);
        tasks.add(task);

        FloatingActionButton fab = findViewById(R.id.addBook);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBook();
            }
        });
    }

    private void editBook(Book book) {
        Intent intent = new Intent(this, EditBookActivity.class);
        intent.putExtra(IntentExtras.BOOK_ID.getName(), book.getId());
        intent.putExtra(IntentExtras.GBA_ID.getName(), book.getGoogleBooksId());
        startActivity(intent);
    }

    private void addBook() {
        // TODO
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.room_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit:
                Book book = currBooks.get((int) info.id);
                editBook(book);
                return true;
            case R.id.delete:
                Integer bookId = currBooks.get((int) info.id).getId();
                deleteBook(bookId);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteBook(Integer bookId) {
        DeleteBookTask task = new DeleteBookTask(this);
        task.execute(bookId);
        tasks.add(task);
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
        dialog.setDialogResult(new SortBooksDialog.OnMyDialogResult() {
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
        currBooks.sort(new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                Integer r1 = b1.getRating();
                Integer r2 = b2.getRating();
                if (r1 == null) {
                    if (r2 == null) {
                        return 0;
                    }
                    return -1;
                } else if (r2 == null) {
                    return 1;
                }
                return r1 - r2;
            }
        });
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    public void sortByScoreDesc() {
        currBooks.sort(new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                Integer r1 = b1.getRating();
                Integer r2 = b2.getRating();
                if (r1 == null) {
                    if (r2 == null) {
                        return 0;
                    }
                    return 1;
                } else if (r2 == null) {
                    return -1;
                }
                return r2 - r1;
            }
        });
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    public void sortByTitleAsc() {
        currBooks.sort(new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                return b1.getTitle().compareTo(b2.getTitle());
            }
        });
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    public void sortByTitleDesc() {
        currBooks.sort(new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                return b2.getTitle().compareTo(b1.getTitle());
            }
        });
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    public void sortByAuthorAsc() {
        currBooks.sort(new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                return b1.getAuthor().compareTo(b2.getAuthor());
            }
        });
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    public void sortByAuthorDesc() {
        currBooks.sort(new Comparator<Book>() {
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

    private class DeleteBookTask extends BaseAsyncTask<Integer, Void, Void> {

        DeleteBookTask(Context context) {
            super(context);
        }

        @Override
        protected Void doInBackground(Integer... ints) {
            super.doInBackground(ints);
            try {
                mService.deleteBook(mContext, ints[0]);
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

    private final class GetUserBooksTask extends BaseAsyncTask<Void, Void, Void> {

        private String login;

        GetUserBooksTask(String login, Context context) {
            super(context);
            this.login = login;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                allBooks = mService.findBooksByUserLogin(mContext, login);
            } catch (NoNetworkConnectionException e) {
                NoNetworkConnectionToast.show(mContext);
            }
            return null;
        }

        @Override
        @SuppressLint("NewApi")
        protected void onPostExecute(Void aVoid) {
            if (roomId != null) {
//                currBooks = allBooks.stream().filter(book -> book.getRoomId().equals(roomId)).collect(Collectors.toList());
                if (currBooks == null) {
                    currBooks = new ArrayList<>();
                }

                for (Book book : allBooks) {
                    if (book.getRoomId().equals(roomId)) {
                        currBooks.add(book);
                    }
                }

            } else {
                currBooks = allBooks;
            }

            if (currBooks == null || currBooks.size() == 0) {
                TextView noRoomsInfo = findViewById(R.id.noBooksInfo);
                noRoomsInfo.setVisibility(View.VISIBLE);
                gridViewBooks.setVisibility(View.GONE);
                return;
            }
            adapter = new BookGridAdapter(mContext, (ArrayList<Book>) currBooks);
            adapter.notifyDataSetChanged();
            gridViewBooks.invalidateViews();
            gridViewBooks.setAdapter(adapter);
            gridViewBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        final int position, long id) {
                    editBook(currBooks.get(position));
                }
            });
        }
    }

}
