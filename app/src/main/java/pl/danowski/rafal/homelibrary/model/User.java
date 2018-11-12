package pl.danowski.rafal.homelibrary.model;

import lombok.Data;

@Data
public class User {

    private String login;
    private String password;
    private String name;
    private String surname;
    private Boolean premium;
    private String email;

    public User(String login, String password, String name, String surname, Boolean premium, String email) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.premium = premium;
        this.email = email;
    }
}
