package pl.danowski.rafal.homelibrary.services;

import android.content.Context;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import pl.danowski.rafal.homelibrary.controllers.BookController;
import pl.danowski.rafal.homelibrary.model.book.Book;
import pl.danowski.rafal.homelibrary.model.book.CreateBook;
import pl.danowski.rafal.homelibrary.model.gba.GBABook;
import pl.danowski.rafal.homelibrary.network.OnlineCheck;

public class BookService {

    private final BookController controller = new BookController();

    public Book createBook(Context context, final CreateBook book) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return null;
        }

        return controller.createBook(book);
    }

    public boolean updateBook(Context context, final Book book) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return false;
        }

        return controller.updateBook(book);
    }

    public boolean deleteBook(Context context, final int id) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return false;
        }

        return controller.deleteBook(id);
    }

    public GBABook findGBABookById(Context context, final String id) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return null;
        }

        return controller.findGBABookById(id);
    }

    public List<GBABook> findGBABooksByQuery(Context context, final String query) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return null;
        }

        return controller.findGBABooksByQuery(query);
    }

    public List<Book> findBooksByUserLogin(Context context, final String login) {
        boolean isOnline = OnlineCheck.isOnline(context);

        if (!isOnline) {
            return null;
        }

        return controller.findBooksByUserLogin(login);
    }

}
