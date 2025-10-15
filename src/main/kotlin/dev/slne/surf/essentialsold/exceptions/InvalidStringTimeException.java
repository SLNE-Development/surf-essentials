package dev.slne.surf.essentialsold.exceptions;

/**
 * An exception that is thrown when a given string does not represent a valid time value.
 *
 * @author twisti
 * @since 1.0.0
 */
public class InvalidStringTimeException extends RuntimeException {

    /**
     * Constructs a new InvalidStringTimeException with the specified detail message.
     *
     * @param message the detail message
     * @author twisti
     * @since 1.0.0
     */
    public InvalidStringTimeException(String message) {
        super(message);
        printStackTrace();
    }
}
