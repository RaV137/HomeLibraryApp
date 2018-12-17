package pl.danowski.rafal.homelibrary.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import pl.danowski.rafal.homelibrary.model.user.CreateUser;
import pl.danowski.rafal.homelibrary.model.user.User;

@SuppressWarnings("ALL")
public class UserController {

    private RestTemplate mRestTemplate;

    private UserController() {
        this.mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    }

    private static final class UserControllerHelper {
        private static final UserController INSTANCE = new UserController();
    }

    public static UserController getInstance() {
        return UserControllerHelper.INSTANCE;
    }

    public boolean createUser(CreateUser user) {
        ResponseEntity<User> exchange = mRestTemplate.exchange(Urls.getRegisterUpdateUserUrl(), HttpMethod.POST, new HttpEntity<>(user), User.class);
        HttpStatus statusCode = exchange.getStatusCode();
        return statusCode.is2xxSuccessful();
    }

    public boolean updateUser(User user) {
        String url = Urls.getRegisterUpdateUserUrl();
        ResponseEntity<User> exchange = mRestTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(user), User.class);
        HttpStatus statusCode = exchange.getStatusCode();
        return statusCode.is2xxSuccessful();
    }

    public User findUserByEmail(String email) {
        String url = Urls.getFindUserByEmailUrl(email);
        ResponseEntity<User> exchange;
        try {
            exchange = mRestTemplate.exchange(url, HttpMethod.GET, null, User.class);
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
        String url = Urls.getFindUserByLoginUrl(login);
        ResponseEntity<User> exchange = null;
        try {
            exchange = mRestTemplate.exchange(url, HttpMethod.GET, null, User.class);
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

    public boolean deleteUser(String login) {
        String url = Urls.getDeleteUserUrl(login);
        ResponseEntity<User> exchange = mRestTemplate.exchange(url, HttpMethod.DELETE, null, User.class);
        HttpStatus statusCode = exchange.getStatusCode();
        return statusCode.is2xxSuccessful();
    }
}
