package pl.danowski.rafal.homelibrary.services;

import android.content.Context;

import pl.danowski.rafal.homelibrary.controllers.UserController;
import pl.danowski.rafal.homelibrary.model.user.CreateUser;
import pl.danowski.rafal.homelibrary.model.user.User;
import pl.danowski.rafal.homelibrary.network.OnlineCheck;
import pl.danowski.rafal.homelibrary.utiities.enums.LoginResult;
import pl.danowski.rafal.homelibrary.utiities.enums.RegistrationResult;

public class UserService {

    private final UserController controller = new UserController();

    public boolean checkPasswordForLogin(Context context, final String login, final String password) {
        return confirmCredentials(context, login, password);
    }

    private boolean confirmCredentials(Context context, final String login, final String password) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return false; // TODO
        }

        User user = findUserByLogin(context, login);
        if (user == null) {
            return false;
        }

        return password.equals(user.getPassword());
    }

    public LoginResult attemptLogin(Context context, final String login, final String password) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return LoginResult.CONNECTION_ERROR;
        }

        return confirmCredentials(context, login, password) ? LoginResult.SUCCESS : LoginResult.FAILURE;
    }

    public RegistrationResult attemptRegistration(Context context, final String login, final String email, final String encryptedPassword) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return RegistrationResult.CONNECTION_ERROR;
        }

        if (isUserRegistered(context, login, email)) {
            return RegistrationResult.USER_ALREADY_EXISTS;
        }

        CreateUser user = new CreateUser(login, email, encryptedPassword, false);
        return controller.createUser(user) ? RegistrationResult.SUCCESS : RegistrationResult.CONNECTION_ERROR; // TODO: co w przypadku 'false'?
    }

    public boolean isUserRegistered(Context context, final String login, final String email) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return false;
        }

        User user;
        user = findUserByLogin(context, login);
        if (user != null) {
            return true;
        }

        user = findUserByEmail(context, email);
        return user != null;
    }

    public void updateUserPassword(Context context, String login, String encryptedPassword) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            // TODO
        }

        User user = controller.findUserByLogin(login);
        user.setPassword(encryptedPassword);
        controller.updateUser(user);
    }

    public void updateUserEmail(Context context, String login, String email) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            // TODO
        }

        User user = controller.findUserByLogin(login);
        user.setEmail(email);
        controller.updateUser(user);
    }

    public User findUserByLogin(Context context, String login) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return null;
        }

        return controller.findUserByLogin(login);
    }

    public User findUserByEmail(Context context, String email) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return null;
        }

        return controller.findUserByEmail(email);
    }

    public void deleteUser(Context context, String login) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            // TODO
        }

        controller.deleteUser(login);
    }

}
