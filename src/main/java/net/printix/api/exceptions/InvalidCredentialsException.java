package net.printix.api.exceptions;

/**
 *
 * @author peter
 */
@SuppressWarnings("serial")
public class InvalidCredentialsException extends  RuntimeException{

    public InvalidCredentialsException() {
    }

    public InvalidCredentialsException(String string) {
        super(string);
    }

    public InvalidCredentialsException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public InvalidCredentialsException(Throwable thrwbl) {
        super(thrwbl);
    }

}
