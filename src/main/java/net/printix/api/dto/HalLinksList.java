package net.printix.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by peter on 02-01-17.
 */

public class HalLinksList {

    @JsonProperty("item")
    private List<Link> itemLinks;
}
