package pl.danowski.rafal.homelibrary.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkSingleton {
    private static NetworkSingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private NetworkSingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

    }

    public static synchronized NetworkSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkSingleton(context);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    //    MySingleton.getInstance(this).addToRequestQueue(null);
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}