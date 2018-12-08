package pl.danowski.rafal.homelibrary.controllers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pl.danowski.rafal.homelibrary.model.User;
import pl.danowski.rafal.homelibrary.network.NetworkSingleton;
import pl.danowski.rafal.homelibrary.network.OnlineCheck;
import pl.danowski.rafal.homelibrary.utiities.enums.LoginResult;
import pl.danowski.rafal.homelibrary.utiities.enums.RegistrationResult;

public class UserController {

    private static List<User> mockUsers;

    public UserController() {
        if (mockUsers == null) {
            mockUsers = new ArrayList<>();
            mockUsers.add(new User("admin", "e00cf25ad42683b3df678c61f42c6bda",
                    "Admin", "Adminowski", false, "adminadminowski@yopmail.com"));
        }
    }

    public boolean checkPasswordForLogin(Context context, final String login, final String password) {
        return confirmCredentials(context, login, password);
    }

    private boolean confirmCredentials(Context context, final String login, final String password) {
        for (User u : mockUsers) {
            if (u.getLogin().equals(login)) {
                if (u.getPassword().equals(password)) {
                    return true;
                }
            }
        }
        return false;
    }

    public LoginResult attemptLogin(Context context, final String login, final String password) {
        // TODO - replace with shot to API

        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return LoginResult.CONNECTION_ERROR;
        }

        return confirmCredentials(context, login, password) ? LoginResult.SUCCESS : LoginResult.FAILURE;
    }

    public RegistrationResult attemptRegistration(Context context, final String login, final String email, final String encryptedPassword) {
        // TODO - replace with shot to API

        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return RegistrationResult.CONNECTION_ERROR;
        }

        for (User u : mockUsers) {
            if (u.getLogin().equals(login)) {
                return RegistrationResult.LOGIN_ALREADY_EXISTS;
            } else if (u.getEmail().equals(email)) {
                return RegistrationResult.EMAIL_ALREADY_EXISTS;
            }
        }

        mockUsers.add(new User(login, encryptedPassword, "", "", false, email));
        return RegistrationResult.SUCCESS;
    }

    public boolean isUserRegistered(Context context, final String login, final String email) {
        // TODO - replace with shot to API

        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return false;
        }

        for (User u : mockUsers) {
            if (u.getLogin().equals(login) && u.getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    public void updateUserPassword(Context context, String login, String encryptedPassword) {
        // TODO - replace with shot to API

        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            // TODO
        }

        for (User u : mockUsers) {
            if (u.getLogin().equals(login)) {
                u.setPassword(encryptedPassword);
            }
        }
    }

    public void updateUserEmail(Context context, String login, String email) {
        // TODO - replace with shot to API

        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            // TODO
        }

        for (User u : mockUsers) {
            if (u.getLogin().equals(login)) {
                u.setEmail(email);
            }
        }
    }

    public User findUserByLogin(Context context, String login) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return null;
        }

        for (User u : mockUsers) {
            if (u.getLogin().equals(login)) {
                return u;
            }
        }

        return null;
    }

    public void deleteUser(Context context, String login) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            // TODO
        }

        for (User u : mockUsers) {
            if (u.getLogin().equals(login)) {
                mockUsers.remove(u);
            }
        }

        throw new RuntimeException();
    }

    private void addNewRequest(Context context, String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject r) {
                        // TODO
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        NetworkSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
