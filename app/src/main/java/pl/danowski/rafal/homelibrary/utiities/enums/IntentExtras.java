package pl.danowski.rafal.homelibrary.utiities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IntentExtras {
    LOGIN("login"),
    USER_ID("userId"),
    ROOM_ID("roomId"),
    EDIT_ROOM("editRoom");

    private String name;
}
