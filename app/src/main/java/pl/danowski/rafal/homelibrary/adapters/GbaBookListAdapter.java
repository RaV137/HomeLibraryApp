package pl.danowski.rafal.homelibrary.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.activities.AddGBABookActivity;
import pl.danowski.rafal.homelibrary.activities.EditBookActivity;
import pl.danowski.rafal.homelibrary.model.gba.GBABook;
import pl.danowski.rafal.homelibrary.network.ImageDownloader;
import pl.danowski.rafal.homelibrary.utiities.enums.IntentExtras;

public class GbaBookListAdapter extends ArrayAdapter<GBABook> {

    private ImageView mCover;
    private Context mContext;

    public GbaBookListAdapter(Context context, ArrayList<GBABook> books) {
        super(context, 0, books);
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {

        final GBABook book = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_gba_book, parent, false);
        }

        mCover = view.findViewById(R.id.bookCover);
        TextView mTitle = view.findViewById(R.id.title);
        TextView mAuthor = view.findViewById(R.id.author);
        TextView mIsbn = view.findViewById(R.id.isbn);
        ImageView mAccept = view.findViewById(R.id.accept);

        assert book != null : "Passed book is null!";
        mTitle.setText(book.getTitle());
        mAuthor.setText(book.getAuthor());
        String isbn = "ISBN: " + book.getIsbn13();
        mIsbn.setText(isbn);

        FindImageFromUrlTask task = new FindImageFromUrlTask(book.getSmallThumbnailURL());
        task.execute((Void) null);
        AddGBABookActivity.getTasks().add(task);

        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditBookActivity.class);
                intent.putExtra(IntentExtras.GBA_ID.getName(), book.getId());
                mContext.startActivity(intent);
//                ((Activity) mContext).finish();
            }
        });

        return view;
    }

    private class FindImageFromUrlTask extends AsyncTask<Void, Void, Void> {

        private String url;
        private ImageView currCover;
        private Bitmap bmp;

        FindImageFromUrlTask(String url) {
            this.url = url;
            currCover = mCover;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (url != null) {
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
        }
    }

}
