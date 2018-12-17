package pl.danowski.rafal.homelibrary.utiities.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class Validator {

    public static boolean isValidLoginFormat(final String login) {
        String regex = "^(?=.*[\\w^_])(?=\\S+$).{5,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(login);
        return matcher.matches();
    }

    public static boolean isValidPasswordFormat(final String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean isValidEmailFormat(final String email) {
        boolean result = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public static boolean isValidRating(final int rating) {
        return !(rating <= 0 || rating > 10);
    }

}
