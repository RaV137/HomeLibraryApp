package pl.danowski.rafal.homelibrary.model.book;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Book {
    private Integer id;
    private String title;
    private String author;
    private Integer shelfNumber;
    private String googleBooksId;
    private Integer rating;
    private Boolean favourite;
    private Boolean read;
    private Integer userId;
    private Integer roomId;
    private String smallImageUrl;
}
