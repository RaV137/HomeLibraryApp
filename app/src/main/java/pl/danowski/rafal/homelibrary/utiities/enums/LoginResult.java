package pl.danowski.rafal.homelibrary.utiities.enums;

public enum LoginResult {
    SUCCESS("Udało się zalogować"), FAILURE("Niepoprawne dane"), CONNECTION_ERROR("Błąd połączenia z serwerem");

    private LoginResult(String text) {
        this.text = text;
    }

    private String text;

    public String getText() {
        return text;
    }
}
