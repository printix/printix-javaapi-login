package net.printix.api.dto;

import java.util.UUID;

/**
 * Created by peter on 02-01-17.
 */

public class TenantId {
    private final UUID tenantId;

    public TenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return  tenantId.toString();
    }
}
