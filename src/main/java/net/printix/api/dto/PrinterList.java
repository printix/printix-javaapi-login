package net.printix.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by peter on 02-01-17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrinterList extends HalResponse<List<Printer>>{

    @JsonProperty("printers")
    private List<Printer> printers;

    @JsonCreator
    public PrinterList(@JsonProperty("links") List<Link> links,@JsonProperty("printers") List<Printer> printers) {
        super(links, printers);
        this.printers = printers;
    }

    public List<Printer> getPrinters() {
        return printers;
    }
}
