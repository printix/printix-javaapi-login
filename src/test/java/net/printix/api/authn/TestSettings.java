package net.printix.api.authn;

import java.util.Properties;

/**
 * Created by peter on 02-01-17.
 */

public class TestSettings {

    public final String username;
    public final String password;
    public final String tenantName;

    public TestSettings(Properties settings) {
        this.password = notNull("password",settings.getProperty("password"));
        this.tenantName = notNull("tenantname",settings.getProperty("tenantname"));
        this.username =  notNull("username",settings.getProperty("username"));
    }

    public final String notNull(String name, String value){
        if(value == null){
            throw new IllegalArgumentException("The configuration option "+ name+ " cannot be null");
        }
        return value;
    }
}
