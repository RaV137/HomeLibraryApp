package pl.danowski.rafal.homelibrary.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.adapters.GbaBookListAdapter;
import pl.danowski.rafal.homelibrary.exceptions.NoNetworkConnectionException;
import pl.danowski.rafal.homelibrary.model.gba.GBABook;
import pl.danowski.rafal.homelibrary.network.BaseAsyncTask;
import pl.danowski.rafal.homelibrary.services.BookService;
import pl.danowski.rafal.homelibrary.utiities.sharedPreferences.SharedPreferencesUtilities;
import pl.danowski.rafal.homelibrary.utiities.toast.NoNetworkConnectionToast;

public class AddGBABookActivity extends AppCompatActivity {

    @Getter
    private static List<AsyncTask> tasks = new ArrayList<>();

    private EditText mQuery;
    private ListView mList;
    private BookService service = BookService.getInstance();
    private ArrayList<GBABook> books = new ArrayList<>();
    private Context mContext;
    private GbaBookListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gbabook);

        mContext = this;
        mQuery = findViewById(R.id.query);
//        mQuery.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.toString().trim().length() >= 3) {
//                    fillList();
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//            }
//        });

        mList = findViewById(R.id.gbaBooksList);

        ImageView mSearchIcon = findViewById(R.id.searchIcon);
        mSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = mQuery.getText().toString().trim();
                if (query.length() >= 3) {
                    fillList();
                }
            }
        });

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GBABook book = (GBABook) adapterView.getItemAtPosition(i);
                String url = book.getPreviewLinkURL();
                if (TextUtils.isEmpty(url)) {
                    Toast.makeText(mContext, "Nie można otworzyć w przeglądarce.", Toast.LENGTH_SHORT).show();
                } else {
                    openInBrowser(url);
                }
            }
        });
    }


    private void fillList() {
        SearchGbaBooksTask task = new SearchGbaBooksTask(mContext);
        task.execute();
        tasks.add(task);
    }

    private void openInBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private class SearchGbaBooksTask extends BaseAsyncTask<Void, Void, Void> {

        private String query;

        public SearchGbaBooksTask(Context context) {
            super(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            query = mQuery.getText().toString().trim();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            super.doInBackground(voids);
            if (!TextUtils.isEmpty(query)) {
                try {
                    books = (ArrayList<GBABook>) service.findGBABooksByQuery(mContext, query);
                } catch (NoNetworkConnectionException e) {
                    NoNetworkConnectionToast.show(mContext);
                }
            } else {
                books = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            TextView noBooksInfo = findViewById(R.id.noGbaBooksInfo);
            if (books == null) {
                noBooksInfo.setVisibility(View.VISIBLE);
                mList.setVisibility(View.GONE);
            } else {
                if (adapter == null) {
                    adapter = new GbaBookListAdapter(mContext, books);
                    mList.setAdapter(adapter);
                }
                noBooksInfo.setVisibility(View.GONE);
                mList.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        }
    }

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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
