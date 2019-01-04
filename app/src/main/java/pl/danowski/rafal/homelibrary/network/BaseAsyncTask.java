package pl.danowski.rafal.homelibrary.network;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;

public class BaseAsyncTask<T, K, L> extends AsyncTask<T, K, L> {

    protected Context mContext;

    public BaseAsyncTask(Context context) {
        mContext = context;
    }

    @Override
    protected L doInBackground(T... ts) {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        return null;
    }

}
