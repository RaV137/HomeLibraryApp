package pl.danowski.rafal.homelibrary.model.gba;

import lombok.Getter;
import lombok.Setter;


/**
 *  Google Books Api Book Dto
 */
@Getter
@Setter
public class GBABook {

    private String id;
    private String title;
    private String author;
    private String publisher;
    private String publishedYear;
    private String isbn13;
    private Integer pageCount;
    private String smallThumbnailURL;
    private String thumbnailURL;
    private String previewLinkURL;

}
