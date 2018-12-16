package pl.danowski.rafal.homelibrary.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import pl.danowski.rafal.homelibrary.exceptions.NoNetworkConnectionException;

public class OnlineCheck {

    public static void isOnline(Context context) throws NoNetworkConnectionException {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr != null ? connMgr.getActiveNetworkInfo() : null;
        if (networkInfo == null || !networkInfo.isConnected()) {
            throw new NoNetworkConnectionException();
        }
    }

}
