package net.printix.api.authn.internal;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import net.printix.api.authn.AuthenticationClient;

@Configuration
@ComponentScan(basePackageClasses = AuthenticationClient.class)
@EnableConfigurationProperties
public class AuthenticationClientConfiguration {

}
