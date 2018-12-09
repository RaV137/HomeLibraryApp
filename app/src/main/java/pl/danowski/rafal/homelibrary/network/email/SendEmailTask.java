package pl.danowski.rafal.homelibrary.network.email;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public final class SendEmailTask extends AsyncTask<Void, Void, Void> {

    private String body;
    private String subject;
    private String email;
    private Context context;

    public SendEmailTask(String body, String subject, String email, Context context) {
        this.body = body;
        this.subject = subject;
        this.email = email;
        this.context = context;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            GMailSender sender = new GMailSender();
            sender.sendMail(subject, body, "home.library.dev@gmail.com", email);
        } catch (Exception e) {
            Toast.makeText(context, "Something went wrong, contact me with email to home.library.dev@gmail.com", Toast.LENGTH_LONG).show();
            Log.e("SendMail", e.getMessage(), e);
            throw new RuntimeException("sendAnEmailWithLoginCredentials: " + e.getMessage());
        }
        return null;
    }
}
