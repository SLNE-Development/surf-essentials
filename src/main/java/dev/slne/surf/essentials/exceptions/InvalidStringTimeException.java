package dev.slne.surf.essentials.exceptions;

/**
 * An exception that is thrown when a given string does not represent a valid time value.
 *
 * @since 1.0.0
 * @author twisti
 */
public class InvalidStringTimeException extends RuntimeException{

    /**
     * Constructs a new InvalidStringTimeException with the specified detail message.
     *
     * @param message the detail message
     * @since 1.0.0
     * @author twisti
     */
    public InvalidStringTimeException(String message){
        super(message);
        printStackTrace();
    }
}
