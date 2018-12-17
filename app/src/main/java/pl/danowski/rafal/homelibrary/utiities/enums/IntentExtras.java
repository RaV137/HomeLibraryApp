package pl.danowski.rafal.homelibrary.utiities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IntentExtras {
    LOGIN("login"),
    USER_ID("userId"),
    ROOM_ID("roomId"),
    EDIT_ROOM("editRoom"),
    BOOK_ID("bookId"),
    GBA_ID("gbaId");

    private String name;
}
