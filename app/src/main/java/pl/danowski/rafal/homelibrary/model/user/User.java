package pl.danowski.rafal.homelibrary.model.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private Integer id;
    private String login;
    private String email;
    private String password;
    private Boolean premium;
}
