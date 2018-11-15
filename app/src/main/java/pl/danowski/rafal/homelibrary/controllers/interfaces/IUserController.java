package pl.danowski.rafal.homelibrary.controllers.interfaces;

import android.content.Context;

import pl.danowski.rafal.homelibrary.utiities.enums.LoginResult;
import pl.danowski.rafal.homelibrary.utiities.enums.RegistrationResult;

public interface IUserController {

    LoginResult attemptLogin(Context context, String login, String password, boolean isOnline);

    RegistrationResult attemptRegistration(Context context, String login, String email, String encryptedPassword, boolean isOnline);

    boolean findUser(Context context, String login, String email, boolean isOnline);

    void updateUserPassword(Context context, String login, String encryptedPassword, boolean isOnline);
}
