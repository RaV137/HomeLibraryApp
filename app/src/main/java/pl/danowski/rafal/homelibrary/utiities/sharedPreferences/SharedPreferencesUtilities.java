package pl.danowski.rafal.homelibrary.utiities.sharedPreferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class SharedPreferencesUtilities {

    private static final String SHARED_PREFERENCES_FILE = " pl.danowski.rafal.homelibrary.PREFERENCES";

    private static final String LOGIN = "login";
    private static final String AUTOLOGIN = "autologin";

    public static void setLogin(Context applicationContext, String login) {
        setPreference(applicationContext, LOGIN, login);
    }

    public static void deleteLogin(Context applicationContext) {
        setLogin(applicationContext, "");
    }

    public static String getLogin(Context applicationContext) {
        return getPreference(applicationContext, LOGIN);
    }

    public static void setAutologin(Context applicationContext, boolean autologin) {
        setPreference(applicationContext, AUTOLOGIN, String.valueOf(autologin));
    }

    public static boolean isUserLoggedIn(Context applicationContext) {
        String autologin = getPreference(applicationContext, AUTOLOGIN);
        if (isEmpty(autologin)) {
            return false;
        } else {
            return Boolean.valueOf(autologin) && isSavedLogin(applicationContext);
        }
    }

    private static boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }

    private static boolean isSavedLogin(Context applicationContext) {
        return !isEmpty(getLogin(applicationContext));
    }

    private static String getPreference(Context context, String key) {
        String value = "";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getString(key, "");
        }
        return value;
    }

    private static void setPreference(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }


}
