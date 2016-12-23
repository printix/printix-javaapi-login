# printix-javaapi-login
Java client for programmatic login to Printix without user interaction.

In order to use this you must have a Printix account. You can sign up for one at http://www.printix.net.

# Usage and configuration

In a Spring application simply `@EnablePrintixAuthenticationClient` in your Spring `@Configuration` class
and then you will have a `AuthenticationClient` available for injection in your Spring components.

The only configuration required is that you must set `printix.oauth.client_id` to an id identifying your
application.

See the unit-test and its configuration file
[src/test/resources/application.yml](https://github.com/printix/printix-javaapi-login/blob/master/src/test/resources/application.yml)
for a complete list of available configuration properties.
