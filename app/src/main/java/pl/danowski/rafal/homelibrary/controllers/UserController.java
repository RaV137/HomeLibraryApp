package pl.danowski.rafal.homelibrary.controllers;

import com.google.gson.Gson;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import pl.danowski.rafal.homelibrary.model.user.User;

// TODO: poczyścić, zostawić tylko bezpośrednie odwołania do API, resztę wywalić do serwisu (ma zostać: create, delete, update, getByEmail, getByLogin)

@SuppressWarnings("ALL")
public class UserController {

    private RestTemplate mRestTemplate = new RestTemplate();

    private final String URL_BASE = "http://192.168.1.28:8080";

    private final String REGISTER_USER_ULR = URL_BASE + "/users";
    private final String DELETE_UPDATE_USER_ULR = URL_BASE + "/users/";
    private final String FIND_USER_BY_EMAIL_URL = URL_BASE + "/users/email/";
    private final String FIND_USER_BY_LOGIN_URL = URL_BASE + "/users/login/";

    private String getDeleteUpdateUserUrl(String login) {
        return DELETE_UPDATE_USER_ULR + login;
    }

    private String getFindUserByEmailUrl(String email) {
        return FIND_USER_BY_EMAIL_URL + email;
    }

    private String getFindUserByLoginUrl(String login) {
        return FIND_USER_BY_LOGIN_URL + login;
    }

    public boolean confirmCredentials(final String login, final String password) {
        User user = findUserByLogin(login);
        if (user == null) {
            return false;
        }

        return password.equals(user.getPassword());
    }

    public boolean createUser(User user) {
        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        ResponseEntity<User> exchange = mRestTemplate.exchange(REGISTER_USER_ULR, HttpMethod.POST, new HttpEntity<>(user), User.class);
        HttpStatus statusCode = exchange.getStatusCode();

        return statusCode != HttpStatus.OK;
    }

    public boolean isUserRegistered(final String login, final String email) {
        User user;
        user = findUserByLogin(login);
        if (user != null) {
            return true;
        }

        user = findUserByEmail(email);
        return user != null;
    }

    public void updateUser(User user) {
        String url = getDeleteUpdateUserUrl(user.getLogin());

        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        mRestTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(user), User.class);
    }

    private User findUserByEmail(String email) {
        String url = getFindUserByEmailUrl(email);

        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        ResponseEntity<User> exchange = null;
        try {
            exchange = mRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(""), User.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        HttpStatus statusCode = exchange.getStatusCode();

        if (statusCode != HttpStatus.OK) {
            return null;
        } else {
            return exchange.getBody();
        }
    }

    public User findUserByLogin(String login) {
        String url = getFindUserByLoginUrl(login);

        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        ResponseEntity<User> exchange = null;
        try {
            exchange = mRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(""), User.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        HttpStatus statusCode = exchange.getStatusCode();

        if (statusCode != HttpStatus.OK) {
            return null;
        } else {
            return exchange.getBody();
        }
    }

    public void deleteUser(String login) {
        String url = getDeleteUpdateUserUrl(login);

        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        mRestTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(""), User.class);
    }

//    private void addNewRequest(Context context, String url) {
//        JsonObjectRequest request = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        User user = getUserDtofromJson(response.toString());
//
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // TODO: Handle error
//
//                    }
//
//                });
//
//        NetworkSingleton.getInstance(context).addToRequestQueue(request);
//    }

}
