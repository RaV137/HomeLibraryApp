package pl.danowski.rafal.homelibrary.controllers;

import java.util.ArrayList;
import java.util.List;

import pl.danowski.rafal.homelibrary.controllers.interfaces.ILoginRegistrationController;
import pl.danowski.rafal.homelibrary.model.User;
import pl.danowski.rafal.homelibrary.utiities.enums.LoginResult;
import pl.danowski.rafal.homelibrary.utiities.enums.RegistrationResult;

public class LoginRegistrationController implements ILoginRegistrationController {

    private List<User> mockUsers;

    public LoginRegistrationController() {
        mockUsers = new ArrayList<>();
        mockUsers.add(new User("admin", "e00cf25ad42683b3df678c61f42c6bda",
                "Admin", "Adminowski", false, "adminadminowski@yopmail.com"));
    }

    public LoginResult attemptLogin(String loginOrEmail, String password) {
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
    public RegistrationResult attemptRegistration(String login, String email, String password, String name, String surname) {
        for(User u : mockUsers) {
            if(u.getLogin().equals(login)) {
                return RegistrationResult.LOGIN_ALREADY_EXISTS;
            } else if (u.getEmail().equals(email)) {
                return RegistrationResult.EMAIL_ALREADY_EXISTS;
            }
        }

        mockUsers.add(new User(login, password,name, surname, false, email));
        return RegistrationResult.SUCCESS;
    }
}
