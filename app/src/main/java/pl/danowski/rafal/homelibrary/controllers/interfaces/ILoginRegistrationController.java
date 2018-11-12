package pl.danowski.rafal.homelibrary.controllers.interfaces;

import pl.danowski.rafal.homelibrary.utiities.enums.LoginResult;
import pl.danowski.rafal.homelibrary.utiities.enums.RegistrationResult;

public interface ILoginRegistrationController {

    LoginResult attemptLogin(String login, String password);

    RegistrationResult attemptRegistration(String login, String email, String password, String name, String surname);

}
