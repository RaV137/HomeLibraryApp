package pl.danowski.rafal.homelibrary.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUser {
    private String login;
    private String email;
    private String password;
    private Boolean premium;
}
