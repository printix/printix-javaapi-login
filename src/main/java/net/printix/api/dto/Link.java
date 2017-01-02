package net.printix.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by peter on 02-01-17.
 */

public class Link {
    private final String uri;

    @JsonCreator
    public Link(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
