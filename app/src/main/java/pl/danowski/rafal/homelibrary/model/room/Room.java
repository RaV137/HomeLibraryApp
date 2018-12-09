package pl.danowski.rafal.homelibrary.model.room;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Room {

    private Integer id;
    private String name;
    private String colour;
    private String shortName;
    private Integer userId;
}
