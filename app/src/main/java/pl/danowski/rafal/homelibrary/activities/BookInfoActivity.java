package pl.danowski.rafal.homelibrary.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import pl.danowski.rafal.homelibrary.utiities.toast.NoNetworkConnectionToast;

public class BookInfoActivity extends AppCompatActivity {

    private Book mBook;
    private GBABook mGBABook;

    private ImageView mCover;
    private TextView mTitle;
    private TextView mAuthor;
    private TextView mPublisher;
    private ImageView mIsRead;
    private ImageView mIsFavourite;
    private TextView mRating;
    private TextView mRoom;
    private TextView mShelf;
    private TextView mPageCount;
    private TextView mYearPublished;
    private TextView mIsbn;

    private String webUrl;

    private android.support.v7.app.ActionBar mActionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_book_info);

        mActionBar = getSupportActionBar();
        assert mActionBar != null : "ActionBar is null!";
        mActionBar.setDisplayHomeAsUpEnabled(true);

        mCover = findViewById(R.id.bookCover);
        mTitle = findViewById(R.id.title);
        mAuthor = findViewById(R.id.author);
        mPublisher = findViewById(R.id.publisher);
        mIsRead = findViewById(R.id.isRead);
        mIsFavourite = findViewById(R.id.isFavourite);
        mRating = findViewById(R.id.rating);
        mRoom = findViewById(R.id.room);
        mShelf = findViewById(R.id.shelf);
        mPageCount = findViewById(R.id.pageCount);
        mYearPublished = findViewById(R.id.yearPublished);
        mIsbn = findViewById(R.id.isbn);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Integer bookId = extras.getInt(IntentExtras.BOOK_ID.getName());
            String gbaID = extras.getString(IntentExtras.GBA_ID.getName());
            FindBookAndGBAByIdsTask task = new FindBookAndGBAByIdsTask(gbaID, bookId, this);
            task.execute((Void) null);
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

    }

    private void favouriteClicked() {
        boolean fav = mBook.getFavourite();
        mBook.setFavourite(!fav);

        Drawable isFavouriteDrawable;
        if (!fav) {
            isFavouriteDrawable = getResources().getDrawable(R.drawable.ic_star_black_24dp);
        } else {
            isFavouriteDrawable = getResources().getDrawable(R.drawable.ic_star_border_black_24dp);
        }
        mIsFavourite.setImageDrawable(isFavouriteDrawable);
        UpdateBookTask task = new UpdateBookTask(this);
        task.execute();
    }

    private void readClicked() {
        boolean read = mBook.getRead();
        mBook.setRead(!read);

        Drawable isReadDrawable;
        if (!read) {
            isReadDrawable = getResources().getDrawable(R.drawable.ic_check_circle_green_24dp);
        } else {
            isReadDrawable = getResources().getDrawable(R.drawable.ic_check_circle_red_24dp);
        }
        mIsRead.setImageDrawable(isReadDrawable);
        UpdateBookTask task = new UpdateBookTask(this);
        task.execute();
    }

    private class UpdateBookTask extends BaseAsyncTask<Void, Void , Void> {

        public UpdateBookTask(Context context) {
            super(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            super.doInBackground(voids);

            BookService service = BookService.getInstance();
            try {
                service.updateBook(mContext, mBook);
            } catch (NoNetworkConnectionException e) {
                NoNetworkConnectionToast.show(mContext);
            }

            return null;
        }
    }

    private void openInBrowser() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
        startActivity(browserIntent);
    }


    private class FindBookAndGBAByIdsTask extends BaseAsyncTask<Void, Void, Void> {

        private String mGbaId;
        private Integer mBookId;
        private Room room;
        private Bitmap bmp;

        FindBookAndGBAByIdsTask(String gbaId, Integer bookId, Context context) {
            super(context);
            mGbaId = gbaId;
            mBookId = bookId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            super.doInBackground(voids);

            BookService service = BookService.getInstance();
            try {
                mGBABook = service.findGBABookById(mContext, mGbaId);
                mBook = service.findBookById(mContext, mBookId);

                RoomService roomService = RoomService.getInstance();
                if (mBook != null) {
                    room = roomService.findRoomById(mContext, mBook.getRoomId());
                }

                String url = mGBABook.getThumbnailURL();
                if(url != null) {
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

                mTitle.setText(mBook.getTitle());
                mAuthor.setText(mBook.getAuthor());
                mPublisher.setText(mGBABook.getPublisher());

                String roomText = "Pokój: " + room.getName();
                mRoom.setText(roomText);

                String shelfText = "Nr półki: " + mBook.getShelfNumber();
                mShelf.setText(shelfText);

                String pageCountText = "L. stron: " + mGBABook.getPageCount();
                mPageCount.setText(pageCountText);

                String yearPublishedText = "Wydano: " + mGBABook.getPublishedYear();
                mYearPublished.setText(yearPublishedText);

                String isbnText = "ISBN: " + mGBABook.getIsbn13();
                mIsbn.setText(isbnText);

                Integer rating = mBook.getRating();
                if (rating == null) {
                    mRating.setText("N/A");
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

                if(bmp != null) {
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
