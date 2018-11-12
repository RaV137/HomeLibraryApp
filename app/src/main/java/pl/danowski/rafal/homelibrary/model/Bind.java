package pl.danowski.rafal.homelibrary.model;

import lombok.Data;

@Data
public class Bind {

    private User user;
    private Position book;
    private Room room;

    private Integer shelf;
    private String comment;
    private Boolean favourite;
    private Integer rating;
    private Boolean read;

    public Bind(User user, Position book, Room room, Integer shelf, String comment, Boolean favourite, Integer rating, Boolean read) {
        this.user = user;
        this.book = book;
        this.room = room;
        this.shelf = shelf;
        this.comment = comment;
        this.favourite = favourite;
        this.rating = rating;
        this.read = read;
    }
}
