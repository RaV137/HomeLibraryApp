package pl.danowski.rafal.homelibrary.controllers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pl.danowski.rafal.homelibrary.controllers.interfaces.IUserController;
import pl.danowski.rafal.homelibrary.model.User;
import pl.danowski.rafal.homelibrary.network.NetworkSingleton;
import pl.danowski.rafal.homelibrary.utiities.enums.LoginResult;
import pl.danowski.rafal.homelibrary.utiities.enums.RegistrationResult;

public class UserController implements IUserController {

    private static List<User> mockUsers;

    public UserController() {
        if (mockUsers == null) {
            mockUsers = new ArrayList<>();
            mockUsers.add(new User("admin", "e00cf25ad42683b3df678c61f42c6bda",
                    "Admin", "Adminowski", false, "adminadminowski@yopmail.com"));
        }
    }

    public LoginResult attemptLogin(Context context, final String loginOrEmail, final String password, final boolean isOnline) {
        // TODO - replace with shot to API

        if (!isOnline) {
            return LoginResult.CONNECTION_ERROR;
        }

        for (User u : mockUsers) {
            if (u.getLogin().equals(loginOrEmail) || u.getEmail().equals(loginOrEmail)) {
                if (u.getPassword().equals(password)) {
                    return LoginResult.SUCCESS;
                }
            }
        }

        return LoginResult.FAILURE;
    }

    @Override
    public RegistrationResult attemptRegistration(Context context, final String login, final String email, final String encryptedPassword, boolean isOnline) {
        // TODO - replace with shot to API

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

    @Override
    public boolean findUser(Context context, final String login, final String email, boolean isOnline) {
        // TODO - replace with shot to API

        if (!isOnline) {
            return false;
        }

        for(User u : mockUsers) {
            if(u.getLogin().equals(login) && u.getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void updateUserPassword(Context context, String login, String encryptedPassword, boolean isOnline) {
        // TODO - replace with shot to API

        if (!isOnline) {
            // TODO
        }

        for(User u : mockUsers) {
            if(u.getLogin().equals(login)) {
                u.setPassword(encryptedPassword);
            }
        }
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
