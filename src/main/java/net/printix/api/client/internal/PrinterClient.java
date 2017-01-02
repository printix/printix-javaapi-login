package net.printix.api.client.internal;

import net.printix.api.client.login.oauth.BearerToken;
import net.printix.api.dto.PrinterList;
import net.printix.api.dto.TenantId;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by peter on 02-01-17.
 */

public interface PrinterClient {

    @GET("/v1/tenants/{tenantId}/printers")
    public Call<PrinterList> getPrinters(@Header("Authorization") BearerToken token, @Path("tenantId") TenantId tenantId);
}
