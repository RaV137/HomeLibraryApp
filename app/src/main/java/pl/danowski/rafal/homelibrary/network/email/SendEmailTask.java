package pl.danowski.rafal.homelibrary.network.email;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import pl.danowski.rafal.homelibrary.network.BaseAsyncTask;

public final class SendEmailTask extends BaseAsyncTask<Void, Void, Void> {

    private String body;
    private String subject;
    private String email;

    public SendEmailTask(String body, String subject, String email, Context context) {
        super(context);
        String disclaimer = "\n\nWiadomość email generowana automatycznie. " +
                "Proszę, nie odpowiadaj na tego maila.\n" +
                "Właścicielem Twoich danych jest właściciel maila home.library.dev@gmail.com." +
                "Jeżeli chcesz usunąć swoje dane, usuń konto w aplikacji.";
        this.body = body + disclaimer;
        this.subject = subject;
        this.email = email;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            GMailSender sender = new GMailSender();
            sender.sendMail(subject, body, "home.library.dev@gmail.com", email);
        } catch (Exception e) {
            Toast.makeText(mContext, "Something went wrong, contact me with email to home.library.dev@gmail.com", Toast.LENGTH_LONG).show();
            Log.e("SendMail", e.getMessage(), e);
            throw new RuntimeException("sendAnEmailWithLoginCredentials: " + e.getMessage());
        }
        return null;
    }
}
