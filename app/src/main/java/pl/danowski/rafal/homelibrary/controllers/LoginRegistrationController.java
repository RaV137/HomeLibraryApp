package pl.danowski.rafal.homelibrary.controllers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pl.danowski.rafal.homelibrary.controllers.interfaces.ILoginRegistrationController;
import pl.danowski.rafal.homelibrary.model.User;
import pl.danowski.rafal.homelibrary.network.NetworkSingleton;
import pl.danowski.rafal.homelibrary.utiities.enums.LoginResult;
import pl.danowski.rafal.homelibrary.utiities.enums.RegistrationResult;

public class LoginRegistrationController implements ILoginRegistrationController {

    private static List<User> mockUsers;

    public LoginRegistrationController() {
        if (mockUsers == null) {
            mockUsers = new ArrayList<>();
            mockUsers.add(new User("admin", "e00cf25ad42683b3df678c61f42c6bda",
                    "Admin", "Adminowski", false, "adminadminowski@yopmail.com"));
        }
    }

    public LoginResult attemptLogin(Context context, String loginOrEmail, String password, boolean isOnline) {
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
    public RegistrationResult attemptRegistration(Context context, String login, String email, String encryptedPassword, boolean isOnline) {
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
