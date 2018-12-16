package pl.danowski.rafal.homelibrary.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;

public class ImageDownloader {

    public static Bitmap bitmapFromUrl(String url) {
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

}
