package pl.danowski.rafal.homelibrary.model;

import lombok.Data;

@Data
public class Position {

    private String title;
    private String author;
    private String publisher;
    private Integer volume;
    private Integer serialNumber;
    private String serialName;
    private Integer isbn;
    private Integer year;

    public Position(String title, String author, String publisher, Integer volume, Integer serialNumber, String serialName, Integer isbn, Integer year) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.volume = volume;
        this.serialNumber = serialNumber;
        this.serialName = serialName;
        this.isbn = isbn;
        this.year = year;
    }
//    private Integer cover; // TODO ??
}
