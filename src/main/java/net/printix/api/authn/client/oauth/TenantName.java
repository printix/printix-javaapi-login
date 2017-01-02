package net.printix.api.authn.client.oauth;

/**
 * Created by peter on 31-12-16.
 */

public class TenantName {
    private final String tenantName;

    public TenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantName() {
        return tenantName;
    }

    @Override
    public String toString() {
        return tenantName;
    }
}
