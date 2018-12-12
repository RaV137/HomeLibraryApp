package pl.danowski.rafal.homelibrary.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.model.book.Book;
import pl.danowski.rafal.homelibrary.model.room.Room;
import pl.danowski.rafal.homelibrary.services.RoomService;

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
        ImageView mCover = convertView.findViewById(R.id.bookCover);

        assert book != null : "Book is null!";
        mTitle.setText(book.getTitle());
        mAuthor.setText(book.getAuthor());

        Integer rating = book.getRating();
        if(rating == null) {
            mRating.setText("N/A");
        } else {
            mRating.setText(String.valueOf(rating));
        }

        Boolean favourite = book.getFavourite();
        if(favourite) {
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

        // TODO obrazek z GPA wrzucić do bazy i pobierać z niej przy wyświetlaniu listy książek
//        String imageUrl = book.getSmallImageUrl();

        Integer roomId = book.getRoomId();
        FindRoomByIdAndFindImageFromUrlTask task = new FindRoomByIdAndFindImageFromUrlTask(roomId, null);
        task.execute((Void) null);

        return convertView;
    }

    // TODO: dla każdej książki osobny wątek, co może pójść nie tak...
    private class FindRoomByIdAndFindImageFromUrlTask extends AsyncTask<Void, Void, Void> {

        private int roomId;
        private String url;
        private Room room;

        FindRoomByIdAndFindImageFromUrlTask(int roomId, String url) {
            this.roomId = roomId;
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RoomService service = new RoomService();
            room = service.findRoomById(mContext, roomId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Integer shelf = book.getShelfNumber();
            String roomAndShelf = room.getShortName() + "/" + shelf;
            mRoomAndShelf.setText(roomAndShelf);
        }
    }
}