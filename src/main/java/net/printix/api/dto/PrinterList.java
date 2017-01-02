package net.printix.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by peter on 02-01-17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrinterList {

    @JsonProperty("embedded")
    private List<Printer> printers;
    @JsonProperty("_links.item")
    private final List<Link> links;

    @JsonCreator
    public PrinterList(@JsonProperty("links.item") List<Link> links, @JsonProperty("embedded") List<Printer> printers) {
        this.printers = printers;
        this.links = links;
    }

    public List<Printer> getPrinters() {
        return printers;
    }
}
