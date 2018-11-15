package pl.danowski.rafal.homelibrary.utiities.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class SharedPreferencesUtilities {

    private static final String LOGIN = "login";
    private static final String AUTOLOGIN = "autologin";

    private static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setLogin(Context ctx, String login) {
        setPreference(ctx, LOGIN, login);
    }

    public static void deleteLogin(Context context) {
        setLogin(context, "");
    }

    public static String getLogin(Context context) {
        return getPreference(context, LOGIN);
    }

    public static void setAutologin(Context context, boolean autologin) {
        setPreference(context, AUTOLOGIN, String.valueOf(autologin));
    }

    public static boolean isUserLoggedIn(Context context) {
        String autologin = getPreference(context, AUTOLOGIN);
        if (isEmpty(autologin)) {
            return false;
        } else {
            return Boolean.getBoolean(autologin) && isSavedLogin(context);
        }
    }

    private static boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }

    private static boolean isSavedLogin(Context context) {
        return !isEmpty(getLogin(context));
    }

    private static String getPreference(Context ctx, String key) {
        return getSharedPreferences(ctx).getString(key, "");
    }

    private static void setPreference(Context ctx, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(key, value);
        editor.apply();
    }
}
