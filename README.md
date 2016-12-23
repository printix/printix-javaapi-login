# printix-javaapi-login
Java client for programmatic login to Printix without user interaction.

# Usage and configuration

In a Spring application simply `@EnablePrintixAuthenticationClient` in your Spring `@Configuration` class
and then you will have a `AuthenticationClient` available for injection in your Spring components.

The only configuration required is that you must set `printix.oauth.client_id` to an id identifying your
application.

See the unittest and its configuration file
(src/test/resources/application.yml)[`https://github.com/printix/printix-javaapi-login/blob/master/src/test/resources/application.yml]
for a complete list af available configuration properties.
