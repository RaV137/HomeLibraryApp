package pl.danowski.rafal.homelibrary.controllers;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import pl.danowski.rafal.homelibrary.model.book.Book;
import pl.danowski.rafal.homelibrary.model.book.CreateBook;
import pl.danowski.rafal.homelibrary.model.gba.GBABook;

public class BookController {

    private RestTemplate mRestTemplate;

    public BookController() {
        this.mRestTemplate = new RestTemplate();
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    }

    public Book createBook(final CreateBook book) {
        ResponseEntity<Book> exchange = mRestTemplate.exchange(Urls.getCreateUpdateBookUrl(), HttpMethod.POST, new HttpEntity<>(book), Book.class);
        HttpStatus statusCode = exchange.getStatusCode();
        if(!statusCode.is2xxSuccessful()) {
            return null;
        }
        return exchange.getBody();
    }

    public boolean updateBook(final Book book) {
        String url = Urls.getCreateUpdateBookUrl();
        ResponseEntity<Book> exchange = mRestTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(book), Book.class);
        HttpStatus statusCode = exchange.getStatusCode();
        return statusCode.is2xxSuccessful();
    }

    public boolean deleteBook(final int id) {
        String url = Urls.getDeleteBookUrl(id);
        ResponseEntity<Book> exchange = mRestTemplate.exchange(url, HttpMethod.DELETE, null, Book.class);
        HttpStatus statusCode = exchange.getStatusCode();
        return statusCode.is2xxSuccessful();
    }

    public GBABook findGBABookById(final String id) {
        String url = Urls.getFindBookByIdUrl(id);
        ResponseEntity<GBABook> exchange;
        try {
            exchange = mRestTemplate.exchange(url, HttpMethod.GET, null, GBABook.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        HttpStatus statusCode = exchange.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            return null;
        } else {
            return exchange.getBody();
        }
    }

    public List<GBABook> findGBABooksByQuery(final String query) {
        String url = Urls.getFindBooksByQueryUrl(query);
        ResponseEntity<List<GBABook>> exchange;
        try {
            exchange = mRestTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<GBABook>>(){});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        HttpStatus statusCode = exchange.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            return null;
        } else {
            return exchange.getBody();
        }
    }

    public List<Book> findBooksByUserLogin(final String login) {
        String url = Urls.getFindBooksByUserUrl(login);
        ResponseEntity<List<Book>> exchange;
        try {
            exchange = mRestTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Book>>(){});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        HttpStatus statusCode = exchange.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            return null;
        } else {
            return exchange.getBody();
        }
    }

    public Book findBookById(final Integer id) {
        String url = Urls.getFindBookByIdUrl(id);
        ResponseEntity<Book> exchange;
        try {
            exchange = mRestTemplate.exchange(url, HttpMethod.GET, null, Book.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        HttpStatus statusCode = exchange.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            return null;
        } else {
            return exchange.getBody();
        }
    }
}
