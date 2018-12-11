package pl.danowski.rafal.homelibrary.controllers;

final class Urls {

    private static final String BASE_URL = "http://192.168.1.28:8080";

    // USERS
    private static final String USERS_URL = "/users/";
    private static final String FIND_USER_BY_EMAIL_URL = "email/";
    private static final String FIND_USER_BY_LOGIN_URL = "login/";

    static String getDeleteUpdateUserUrl(String login) {
        return BASE_URL + USERS_URL + login;
    }

    static String getFindUserByEmailUrl(String email) {
        return BASE_URL + USERS_URL + FIND_USER_BY_EMAIL_URL + email;
    }

    static String getFindUserByLoginUrl(String login) {
        return BASE_URL + USERS_URL + FIND_USER_BY_LOGIN_URL + login;
    }

    static String getRegisterUserUrl() {
        return BASE_URL + USERS_URL;
    }

    // ROOMS
    private static final String ROOMS_URL = "/rooms/";
    private static final String FIND_ROOMS_BY_USER_URL = "user/";

    static String getCreateRoomUrl() {
        return BASE_URL + ROOMS_URL;
    }

    static String getFindDeleteUpdateRoomById(int id) {
        return BASE_URL + ROOMS_URL + id;
    }

    static String getFindRoomsByUserLoginUrl(String login) {
        return BASE_URL + ROOMS_URL + FIND_ROOMS_BY_USER_URL + login;
    }

    // BOOKS
    private static String BOOKS_URL = "/books/";
    private static String FIND_BOOKS_BY_QUERY_URL = "/query/";
    private static String FIND_BOOKS_BY_USER_URL = "/user/";

    static String getCreateBookUrl() {
        return BASE_URL + BOOKS_URL;
    }

    static String getFindBookByIdUrl(String id) {
        return BASE_URL + BOOKS_URL + id;
    }

    static String getDeleteUpdateBookUrl(int id) {
        return BASE_URL + BOOKS_URL + id;
    }

    static String getFindBooksByQueryUrl(String query) {
        return BASE_URL + BOOKS_URL + FIND_BOOKS_BY_QUERY_URL + query;
    }

    static String getFindBooksByUserUrl(String login) {
        return BASE_URL + BOOKS_URL + FIND_BOOKS_BY_USER_URL + login;
    }
}
