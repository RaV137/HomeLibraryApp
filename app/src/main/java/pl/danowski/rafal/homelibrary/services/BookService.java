package pl.danowski.rafal.homelibrary.services;

import android.content.Context;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import pl.danowski.rafal.homelibrary.controllers.BookController;
import pl.danowski.rafal.homelibrary.exceptions.NoNetworkConnectionException;
import pl.danowski.rafal.homelibrary.model.book.Book;
import pl.danowski.rafal.homelibrary.model.book.CreateBook;
import pl.danowski.rafal.homelibrary.model.gba.GBABook;
import pl.danowski.rafal.homelibrary.network.OnlineCheck;

public class BookService {

    private final BookController controller;

    private BookService() {
        this.controller = BookController.getInstance();
    }

    private static final class BookControllerHelper {
        private static final BookService INSTANCE = new BookService();
    }

    public static BookService getInstance() {
        return BookControllerHelper.INSTANCE;
    }

    public Book createBook(Context context, final CreateBook book) throws NoNetworkConnectionException {
        OnlineCheck.isOnline(context);
        return controller.createBook(book);
    }

    public boolean updateBook(Context context, final Book book) throws NoNetworkConnectionException {
        OnlineCheck.isOnline(context);
        return controller.updateBook(book);
    }

    public boolean deleteBook(Context context, final int id) throws NoNetworkConnectionException {
        OnlineCheck.isOnline(context);
        return controller.deleteBook(id);
    }

    public GBABook findGBABookById(Context context, final String id) throws NoNetworkConnectionException {
        OnlineCheck.isOnline(context);
        return controller.findGBABookById(id);
    }

    public List<GBABook> findGBABooksByQuery(Context context, final String query) throws NoNetworkConnectionException {
        OnlineCheck.isOnline(context);
        return controller.findGBABooksByQuery(query);
    }

    public List<Book> findBooksByUserLogin(Context context, final String login) throws NoNetworkConnectionException {
        OnlineCheck.isOnline(context);
        return controller.findBooksByUserLogin(login);
    }

    public Book findBookById(Context context, final Integer id) throws NoNetworkConnectionException {
        OnlineCheck.isOnline(context);
        return controller.findBookById(id);
    }

}
