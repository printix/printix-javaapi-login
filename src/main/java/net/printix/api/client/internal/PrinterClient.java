package net.printix.api.client.internal;

import net.printix.api.dto.Printer;

import java.util.List;

/**
 * Created by peter on 02-01-17.
 */

public interface PrinterClient {

    public List<Printer> getPrinters();
}
