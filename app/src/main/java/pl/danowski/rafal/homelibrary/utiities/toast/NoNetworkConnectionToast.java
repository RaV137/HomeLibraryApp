package pl.danowski.rafal.homelibrary.utiities.toast;

import android.content.Context;
import android.widget.Toast;

public class NoNetworkConnectionToast {

    public static void show(Context context) {
        Toast.makeText(context, "Brak połączenia z internetem.", Toast.LENGTH_LONG).show();
    }

}
