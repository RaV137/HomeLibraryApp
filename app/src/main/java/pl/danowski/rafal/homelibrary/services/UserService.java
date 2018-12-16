package pl.danowski.rafal.homelibrary.services;

import android.content.Context;

import pl.danowski.rafal.homelibrary.controllers.UserController;
import pl.danowski.rafal.homelibrary.exceptions.NoNetworkConnectionException;
import pl.danowski.rafal.homelibrary.model.user.CreateUser;
import pl.danowski.rafal.homelibrary.model.user.User;
import pl.danowski.rafal.homelibrary.network.OnlineCheck;

public class UserService {

    private final UserController controller = new UserController();

    public boolean checkPasswordForLogin(Context context, final String login, final String password) throws NoNetworkConnectionException {
        return confirmCredentials(context, login, password);
    }

    private boolean confirmCredentials(Context context, final String login, final String password) throws NoNetworkConnectionException {
        OnlineCheck.isOnline(context);

        User user = findUserByLogin(context, login);
        if (user == null) {
            return false;
        }

        return password.equals(user.getPassword());
    }

    public boolean attemptLogin(Context context, final String login, final String password) throws NoNetworkConnectionException {
        OnlineCheck.isOnline(context);
        return confirmCredentials(context, login, password);
    }

    public boolean attemptRegistration(Context context, final String login, final String email, final String encryptedPassword) throws NoNetworkConnectionException {
        OnlineCheck.isOnline(context);

        if (isUserRegistered(context, login, email)) {
            return false;
        }

        CreateUser user = new CreateUser(login, email, encryptedPassword, false);
        return controller.createUser(user);
    }

    public boolean isUserRegistered(Context context, final String login, final String email) throws NoNetworkConnectionException {
        OnlineCheck.isOnline(context);

        User user;
        user = findUserByLogin(context, login);
        if (user != null) {
            return true;
        }

        user = findUserByEmail(context, email);
        return user != null;
    }

    public boolean updateUserPassword(Context context, String login, String encryptedPassword) throws NoNetworkConnectionException {
        OnlineCheck.isOnline(context);
        User user = controller.findUserByLogin(login);
        user.setPassword(encryptedPassword);
        return controller.updateUser(user);
    }

    public boolean updateUserEmail(Context context, String login, String email) throws NoNetworkConnectionException {
        OnlineCheck.isOnline(context);
        User user = controller.findUserByLogin(login);
        user.setEmail(email);
        return controller.updateUser(user);
    }

    public User findUserByLogin(Context context, String login) throws NoNetworkConnectionException {
        OnlineCheck.isOnline(context);
        return controller.findUserByLogin(login);
    }

    public User findUserByEmail(Context context, String email) throws NoNetworkConnectionException {
        OnlineCheck.isOnline(context);
        return controller.findUserByEmail(email);
    }

    public boolean deleteUser(Context context, String login) throws NoNetworkConnectionException {
        OnlineCheck.isOnline(context);
        return controller.deleteUser(login);
    }

}
