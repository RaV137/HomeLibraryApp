package pl.danowski.rafal.homelibrary.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.controllers.LoginRegistrationController;
import pl.danowski.rafal.homelibrary.controllers.interfaces.ILoginRegistrationController;

public class RegisterActivity extends AppCompatActivity {

    private ILoginRegistrationController loginRegistrationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        this.loginRegistrationController = new LoginRegistrationController();
    }
}
