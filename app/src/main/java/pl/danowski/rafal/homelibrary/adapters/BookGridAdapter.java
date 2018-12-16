package pl.danowski.rafal.homelibrary.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.exceptions.NoNetworkConnectionException;
import pl.danowski.rafal.homelibrary.model.book.Book;
import pl.danowski.rafal.homelibrary.model.room.Room;
import pl.danowski.rafal.homelibrary.network.ImageDownloader;
import pl.danowski.rafal.homelibrary.services.RoomService;
import pl.danowski.rafal.homelibrary.utiities.toast.NoNetworkConnectionToast;

public class BookGridAdapter extends ArrayAdapter<Book> {

    public BookGridAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
        mContext = context;
        this.books = books;
    }

    private List<Book> books;
    private Book book;
    private Context mContext;
    private TextView mRoomAndShelf;
    private ImageView mCover;

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        book = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_grid_item, parent, false);
        }

        TextView mTitle = convertView.findViewById(R.id.title);
        TextView mAuthor = convertView.findViewById(R.id.author);
        TextView mRating = convertView.findViewById(R.id.rating);
        mRoomAndShelf = convertView.findViewById(R.id.roomAndShelf);
        ImageView mFavourite = convertView.findViewById(R.id.favouriteStar);
        mCover = convertView.findViewById(R.id.bookCover);

        assert book != null : "Book is null!";
        mTitle.setText(book.getTitle());
        mAuthor.setText(book.getAuthor());

        Integer rating = book.getRating();
        if (rating == null) {
            mRating.setText("N/A");
        } else {
            mRating.setText(String.valueOf(rating));
        }

        Boolean favourite = book.getFavourite();
        if (favourite) {
            mFavourite.setImageResource(R.drawable.ic_star_black_24dp);
        } else {
            mFavourite.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
        mFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Book currBook = books.get(position);
                currBook.setFavourite(!currBook.getFavourite());
                notifyDataSetChanged();
            }
        });

        String imageUrl = book.getSmallImageUrl();
        Integer roomId = book.getRoomId();
        FindRoomByIdAndFindImageFromUrlTask task = new FindRoomByIdAndFindImageFromUrlTask(roomId, imageUrl);
        task.execute((Void) null);

        return convertView;
    }

    private class FindRoomByIdAndFindImageFromUrlTask extends AsyncTask<Void, Void, Void> {

        private int roomId;
        private String url;
        private Room room;
        private ImageView currCover;
        private Bitmap bmp;

        FindRoomByIdAndFindImageFromUrlTask(int roomId, String url) {
            this.roomId = roomId;
            this.url = url;
            currCover = mCover;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RoomService service = new RoomService();
            try {
                room = service.findRoomById(mContext, roomId);
            } catch (NoNetworkConnectionException e) {
                NoNetworkConnectionToast.show(mContext);
                return null;
            }
            if(url != null) {
                bmp = ImageDownloader.bitmapFromUrl(url);
            } else {
                bmp = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (url != null) {
                currCover.setImageBitmap(bmp);
            } else {
                currCover.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_library_books_black_24dp));
            }
            Integer shelf = book.getShelfNumber();
            String roomAndShelf = room.getShortName() + "/" + shelf;
            mRoomAndShelf.setText(roomAndShelf);
        }
    }
}