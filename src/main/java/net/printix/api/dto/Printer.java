package net.printix.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by peter on 02-01-17.
 */

public class Printer {

    private final String name;

    @JsonCreator
    public Printer(@JsonProperty("name") String name) {
        this.name = name;
    }
}
