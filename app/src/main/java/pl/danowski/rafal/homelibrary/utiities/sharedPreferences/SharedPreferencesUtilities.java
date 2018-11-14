package pl.danowski.rafal.homelibrary.utiities.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesUtilities {

    public static final String PREF_USER_NAME= "login";

    private static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setPreference(Context ctx, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getUserName(Context ctx, String key) {
        return getSharedPreferences(ctx).getString(key, "");
    }

}
