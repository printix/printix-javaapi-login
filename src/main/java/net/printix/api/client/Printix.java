package net.printix.api.client;

import net.printix.api.dto.OAuthToken;
import net.printix.api.dto.Printer;
import net.printix.api.dto.TenantId;

import java.util.Collections;
import java.util.List;

/**
 * Created by peter on 02-01-17.
 */

public class Printix {

    private final OAuthToken oAuthToken;

    public Printix(OAuthToken oAuthToken) {
        this.oAuthToken = oAuthToken;
    }


    public List<Printer> getPrinters(OAuthToken token, TenantId tenantId){
        return Collections.emptyList();
    }

}
