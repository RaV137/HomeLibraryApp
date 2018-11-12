package pl.danowski.rafal.homelibrary.model;

import lombok.Data;

@Data
public class Room {

    private String name;
    private String shortName;
    private String colour;

    public Room(String name, String shortName, String colour) {
        this.name = name;
        this.shortName = shortName;
        this.colour = colour;
    }
}
