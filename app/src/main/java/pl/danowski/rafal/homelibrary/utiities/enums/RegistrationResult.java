package pl.danowski.rafal.homelibrary.utiities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RegistrationResult {
    SUCCESS("Poprawna rejestracja"),
    USER_ALREADY_EXISTS("Login lub email jest już używany przez innego użytkownika"),
    CONNECTION_ERROR("Błąd połączenia z serwerem");

    private String text;

}
