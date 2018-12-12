package pl.danowski.rafal.homelibrary.utiities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortOptionEnum {
    SORT_BY_SCORE_ASC("sortByScoreAsc"),
    SORT_BY_SCORE_DESC("sortByScoreDesc"),
    SORT_BY_TITLE_ASC("sortByTitleAsc"),
    SORT_BY_TITLE_DESC("sortByTitleDesc"),
    SORT_BY_AUTHOR_ASC("sortByAuthorAsc"),
    SORT_BY_AUTHOR_DESC("sortByAuthorDesc");

    private String name;
}
