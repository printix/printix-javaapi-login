package net.printix.api.dto;

import java.util.List;

/**
 * Created by peter on 02-01-17.
 */

public class HalResponse {

    private final List<Link> links;

    public HalResponse(List<Link> links) {
        this.links = links;
    }

    public List<Link> getLinks() {
        return links;
    }
}
