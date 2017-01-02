package net.printix.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by peter on 02-01-17.
 */

public class Link {
    @JsonProperty("href")
    private final String uri;

    @JsonCreator
    public Link(@JsonProperty("href") String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
