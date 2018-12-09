package pl.danowski.rafal.homelibrary.utiities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LoginResult {
    SUCCESS("Udało się zalogować"),
    FAILURE("Niepoprawne dane"),
    CONNECTION_ERROR("Błąd połączenia z serwerem");

    private String text;
}
