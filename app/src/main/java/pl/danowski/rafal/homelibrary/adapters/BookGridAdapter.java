//package pl.danowski.rafal.homelibrary.adapters;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
//import pl.danowski.rafal.homelibrary.R;
//import pl.danowski.rafal.homelibrary.model.Bind;
//import pl.danowski.rafal.homelibrary.model.Position;
//import pl.danowski.rafal.homelibrary.utiities.BookUtility;
//
//public class BookGridAdapter extends ArrayAdapter<Bind> {
//
//    public BookGridAdapter(Context context, ArrayList<Bind> users) {
//        super(context, 0, users);
//    }
//
//    @NonNull
//    @Override
//    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
//
//        Bind bind = getItem(position);
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_grid_item, parent, false);
//        }
//
//        TextView mTitle = convertView.findViewById(R.id.title);
//        TextView mAuthor = convertView.findViewById(R.id.author);
//        TextView mRating = convertView.findViewById(R.id.rating);
//        TextView mRoomAndShelf = convertView.findViewById(R.id.roomAndShelf);
//        ImageView mFavourite = convertView.findViewById(R.id.favouriteStar);
//
//        assert bind != null : "Bind is null!";
//        assert bind.getBook() != null : "Book is null!";
//        Position book = bind.getBook();
//        mTitle.setText(book.getTitle());
//        mAuthor.setText(book.getAuthor());
//
//        Integer rating = bind.getRating();
//        if(rating == null) {
//            mRating.setText("N/A");
//        } else {
//            mRating.setText(String.valueOf(rating));
//        }
//
//        Boolean favourite = bind.getFavourite();
//        if(favourite) {
//            mFavourite.setImageResource(R.drawable.ic_star_black_24dp);
//        } else {
//            mFavourite.setImageResource(R.drawable.ic_star_border_black_24dp);
//        }
//        mFavourite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Bind currBind = BookUtility.getBindFromPosition(position);
//                currBind.setFavourite(!currBind.getFavourite());
//                BookUtility.printBooks();
//            }
//        });
//
//        String room = bind.getRoom().getName();
//        Integer shelf = bind.getShelf();
//        String roomAndShelf = room + "/" + shelf;
//        mRoomAndShelf.setText(roomAndShelf);
//
//        return convertView;
//    }
//}