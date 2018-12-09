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

    private RestTemplate mRestTemplate = new RestTemplate();

    public boolean createUser(CreateUser user) {
        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        ResponseEntity<User> exchange = mRestTemplate.exchange(Urls.getRegisterUserUrl(), HttpMethod.POST, new HttpEntity<>(user), User.class);
        HttpStatus statusCode = exchange.getStatusCode();
        return statusCode != HttpStatus.OK;
    }

    public void updateUser(User user) {
        String url = Urls.getDeleteUpdateUserUrl(user.getLogin());

        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        mRestTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(user), User.class);
    }

    public User findUserByEmail(String email) {
        String url = Urls.getFindUserByEmailUrl(email);

        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        ResponseEntity<User> exchange;
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
        String url = Urls.getFindUserByLoginUrl(login);

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
        String url = Urls.getDeleteUpdateUserUrl(login);

        mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        mRestTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(""), User.class);
    }
}
