package pl.danowski.rafal.homelibrary.utiities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RegistrationResult {
    SUCCESS("Poprawna rejestracja"),
    LOGIN_ALREADY_EXISTS("Login jest używany przez innego użytkownika"),
    EMAIL_ALREADY_EXISTS("Email jest używany przez innego użytkownika"),
    CONNECTION_ERROR("Błąd połączenia z serwerem");

    private String text;

}
