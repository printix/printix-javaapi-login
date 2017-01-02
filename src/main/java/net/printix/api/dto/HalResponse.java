package net.printix.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by peter on 02-01-17.
 */

public class HalResponse<T> {

    @JsonProperty("links")
    private final List<Link> links;

    @JsonProperty("_embedded")
    private final T embedded;

    public HalResponse(List<Link> links, T embedded) {
        this.links = links;
        this.embedded = embedded;
    }
}
