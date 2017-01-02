package net.printix.api.client;

import net.printix.api.client.internal.PrinterClient;
import net.printix.api.dto.OAuthToken;
import net.printix.api.dto.PrinterList;
import net.printix.api.dto.TenantId;

import java.io.IOException;

import retrofit2.Response;

/**
 * Created by peter on 02-01-17.
 */

public class Printix {


    private final PrinterClient printerClient;

    public Printix(PrinterClient printerClient) {
        this.printerClient = printerClient;
    }


    public Response<PrinterList> getPrinters(OAuthToken token, TenantId tenantId) throws IOException {
        return printerClient.getPrinters(token.getBearerToken(), tenantId).execute();
    }

}
