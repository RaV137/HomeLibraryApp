package pl.danowski.rafal.homelibrary.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.exceptions.NoNetworkConnectionException;
import pl.danowski.rafal.homelibrary.model.book.Book;
import pl.danowski.rafal.homelibrary.model.gba.GBABook;
import pl.danowski.rafal.homelibrary.model.room.Room;
import pl.danowski.rafal.homelibrary.network.BaseAsyncTask;
import pl.danowski.rafal.homelibrary.network.ImageDownloader;
import pl.danowski.rafal.homelibrary.services.BookService;
import pl.danowski.rafal.homelibrary.services.RoomService;
import pl.danowski.rafal.homelibrary.utiities.enums.IntentExtras;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;
import pl.danowski.rafal.homelibrary.utiities.toast.NoNetworkConnectionToast;
import pl.danowski.rafal.homelibrary.utiities.validators.Validator;

import static android.R.layout.simple_spinner_dropdown_item;
import static android.R.layout.simple_spinner_item;

public class EditBookActivity extends AppCompatActivity {

    private Book mBook;
    private GBABook mGBABook;
    private ArrayList<Room> rooms;
    private Room currRoom;

    private ImageView mCover;
    private TextView mTitle;
    private TextView mAuthor;
    private TextView mPublisher;
    private ImageView mIsRead;
    private ImageView mIsFavourite;
    private EditText mRating;
    private Spinner mRoom;
    private EditText mShelf;
    private TextView mPageCount;
    private TextView mYearPublished;
    private TextView mIsbn;

    private TextView mRoomTextView;

    private String webUrl;
    private BookService mService;
    private RoomService mRoomService;

    private android.support.v7.app.ActionBar mActionBar;

    private boolean edited = false;

    private List<AsyncTask> tasks = new ArrayList<>();

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
        setContentView(R.layout.activity_edit_book);

        mActionBar = getSupportActionBar();
        assert mActionBar != null : "ActionBar is null!";
        mActionBar.setDisplayHomeAsUpEnabled(true);

        mService = BookService.getInstance();
        mRoomService = RoomService.getInstance();

        mCover = findViewById(R.id.bookCover);
        mTitle = findViewById(R.id.title);
        mAuthor = findViewById(R.id.author);
        mPublisher = findViewById(R.id.publisher);
        mIsRead = findViewById(R.id.isRead);
        mIsFavourite = findViewById(R.id.isFavourite);
        mRating = findViewById(R.id.rating);
        mRoom = findViewById(R.id.room);
        mShelf = findViewById(R.id.shelfInput);
        mPageCount = findViewById(R.id.pageCount);
        mYearPublished = findViewById(R.id.yearPublished);
        mIsbn = findViewById(R.id.isbn);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Integer bookId = extras.getInt(IntentExtras.BOOK_ID.getName());
            String gbaID = extras.getString(IntentExtras.GBA_ID.getName());
            FindBookAndGBAByIdsTask task = new FindBookAndGBAByIdsTask(gbaID, bookId, this);
            task.execute((Void) null);
            tasks.add(task);
        }

        Button mWebButton = findViewById(R.id.webView);
        mWebButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInBrowser();
            }
        });

        mIsFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favouriteClicked();
            }
        });

        mIsRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readClicked();
            }
        });

        FloatingActionButton fab = findViewById(R.id.saveBook);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitWithSaving();
            }
        });

        mRating.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                notifyDataChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mShelf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                notifyDataChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mRoomTextView == null) {
                    mRoomTextView = (TextView) adapterView.getChildAt(0);
                }

                currRoom = (Room) adapterView.getSelectedItem();
                if (currRoom != null) {
                    updateSpinnerCurrValue(currRoom.getId());
                    notifyDataChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (mRoomTextView == null) {
                    mRoomTextView = (TextView) adapterView.getChildAt(0);
                }
                mRoomTextView.setError("Wybierz pokój.");
            }
        });

        FindRoomsTask task = new FindRoomsTask(this);
        task.execute();
        tasks.add(task);
    }

    private void updateSpinnerCurrValue(Integer id) {
        FindRoomByIdTask task = new FindRoomByIdTask(this);
        task.execute(id);
        tasks.add(task);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (edited) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Czy chcesz wyjść bez zapisywania zmian?")
                            .setTitle("Porzucić zmiany?")
                            .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    exitWithoutSaving();
                                }
                            })
                            .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // dismiss the dialog
                                }
                            })
                            .setNeutralButton("Zapisz", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    exitWithSaving();
                                }
                            })
                            .create().show();
                } else {
                    exitWithoutSaving();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void exitWithoutSaving() {
        finish();
    }

    private void exitWithSaving() {
        String ratingString = mRating.getText().toString();
        if (!TextUtils.isEmpty(ratingString)) {
            Integer rating = Integer.parseInt(ratingString);
            if (!Validator.isValidRating(rating)) {
                mRating.setText("");
                mRating.setError("Ocena musi się znajdować między 1 a 10.");
                mRating.requestFocus();
                return;
            } else {
                mBook.setRating(rating);
            }
        } else {
            mBook.setRating(null);
        }

        String shelfString = mShelf.getText().toString();
        if (!TextUtils.isEmpty(shelfString)) {
            Integer shelf = Integer.parseInt(shelfString);
            mBook.setShelfNumber(shelf);
        } else {
            mBook.setShelfNumber(null);
        }

        mBook.setRoomId(currRoom.getId());

        UpdateBookTask task = new UpdateBookTask(this);
        task.execute();
        tasks.add(task);
    }

    private void favouriteClicked() {
        notifyDataChanged();
        boolean fav = mBook.getFavourite();
        mBook.setFavourite(!fav);

        Drawable isFavouriteDrawable;
        if (!fav) {
            isFavouriteDrawable = getResources().getDrawable(R.drawable.ic_star_black_24dp);
        } else {
            isFavouriteDrawable = getResources().getDrawable(R.drawable.ic_star_border_black_24dp);
        }
        mIsFavourite.setImageDrawable(isFavouriteDrawable);
    }

    private void notifyDataChanged() {
        edited = true;
    }

    private void readClicked() {
        notifyDataChanged();
        boolean read = mBook.getRead();
        mBook.setRead(!read);

        Drawable isReadDrawable;
        if (!read) {
            isReadDrawable = getResources().getDrawable(R.drawable.ic_check_circle_green_24dp);
        } else {
            isReadDrawable = getResources().getDrawable(R.drawable.ic_check_circle_red_24dp);
        }
        mIsRead.setImageDrawable(isReadDrawable);
    }

    private void openInBrowser() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
        startActivity(browserIntent);
    }

    private class FindRoomByIdTask extends BaseAsyncTask<Integer, Void, Void> {

        public FindRoomByIdTask(Context context) {
            super(context);
        }

        @Override
        protected Void doInBackground(Integer... ints) {
            super.doInBackground(ints);
            try {
                currRoom = mRoomService.findRoomById(mContext, ints[0]);
            } catch (NoNetworkConnectionException e) {
                NoNetworkConnectionToast.show(mContext);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mRoomTextView != null) {
                if (currRoom != null) {
                    String roomText = currRoom.getName();
                    mRoomTextView.setText(roomText);
                }
            }
        }
    }

    private class FindRoomsTask extends BaseAsyncTask<Void, Void, Void> {

        public FindRoomsTask(Context context) {
            super(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            super.doInBackground(voids);
            String login = SharedPreferencesUtilities.getLogin(mContext);
            try {
                rooms = (ArrayList<Room>) mRoomService.findRoomsByUserLogin(mContext, login);
            } catch (NoNetworkConnectionException e) {
                NoNetworkConnectionToast.show(mContext);
                rooms = new ArrayList<>();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayAdapter<Room> adapter = new ArrayAdapter<>(mContext, R.layout.spinner_item_layout, rooms);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_layout);
            mRoom.setAdapter(adapter);
        }
    }

    private class UpdateBookTask extends BaseAsyncTask<Void, Void, Void> {

        UpdateBookTask(Context context) {
            super(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            super.doInBackground(voids);
            try {
                mService.updateBook(mContext, mBook);
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

    private class FindBookAndGBAByIdsTask extends BaseAsyncTask<Void, Void, Void> {

        private String mGbaId;
        private Integer mBookId;
        private Bitmap bmp;

        FindBookAndGBAByIdsTask(String gbaId, Integer bookId, Context context) {
            super(context);
            mGbaId = gbaId;
            mBookId = bookId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            super.doInBackground(voids);
            try {
                mGBABook = mService.findGBABookById(mContext, mGbaId);
                mBook = mService.findBookById(mContext, mBookId);

                String url = mGBABook.getThumbnailURL();
                if (url != null) {
                    bmp = ImageDownloader.bitmapFromUrl(url);
                } else {
                    bmp = null;
                }

            } catch (NoNetworkConnectionException e) {
                NoNetworkConnectionToast.show(mContext);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mBook != null && mGBABook != null) {
                mActionBar.setTitle(mBook.getTitle());

                updateSpinnerCurrValue(mBook.getRoomId());

                mTitle.setText(mBook.getTitle());
                mAuthor.setText(mBook.getAuthor());
                mPublisher.setText(mGBABook.getPublisher());
                mShelf.setText(String.valueOf(mBook.getShelfNumber()));

                String pageCountText = "L. stron: " + mGBABook.getPageCount();
                mPageCount.setText(pageCountText);

                String yearPublishedText = "Wydano: " + mGBABook.getPublishedYear();
                mYearPublished.setText(yearPublishedText);

                String isbnText = "ISBN: " + mGBABook.getIsbn13();
                mIsbn.setText(isbnText);

                Integer rating = mBook.getRating();
                if (rating == null) {
                    mRating.setText("");
                } else {
                    mRating.setText(String.valueOf(rating));
                }

                Drawable isReadDrawable;
                if (mBook.getRead()) {
                    isReadDrawable = getResources().getDrawable(R.drawable.ic_check_circle_green_24dp);
                } else {
                    isReadDrawable = getResources().getDrawable(R.drawable.ic_check_circle_red_24dp);
                }
                mIsRead.setImageDrawable(isReadDrawable);

                Drawable isFavouriteDrawable;
                if (mBook.getFavourite()) {
                    isFavouriteDrawable = getResources().getDrawable(R.drawable.ic_star_black_24dp);
                } else {
                    isFavouriteDrawable = getResources().getDrawable(R.drawable.ic_star_border_black_24dp);
                }
                mIsFavourite.setImageDrawable(isFavouriteDrawable);

                webUrl = mGBABook.getPreviewLinkURL();

                if (bmp != null) {
                    mCover.setImageBitmap(bmp);
                } else {
                    mCover.setImageDrawable(getResources().getDrawable(R.drawable.book_96));
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    }
}
