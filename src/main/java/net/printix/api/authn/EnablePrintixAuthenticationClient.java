package net.printix.api.authn;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import net.printix.api.authn.internal.AuthenticationClientConfiguration;

/**
 * Enables the Printix authentication client.
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = { java.lang.annotation.ElementType.TYPE })
@Documented
@Import({ AuthenticationClientConfiguration.class })
@Configuration
public @interface EnablePrintixAuthenticationClient {
}