package dev.slne.surf.essentials.exceptions;

/**
 * An exception that is thrown when the server version is not supported.
 *
 * @since 1.0.0
 * @author twisti
 */
public class UnsupportedServerVersionException extends RuntimeException{

    /**
     * Constructs a new {@link UnsupportedServerVersionException} with the specified detail message.
     *
     * @param message the detail message
     * @since 1.0.0
     * @author twisti
     */
    public UnsupportedServerVersionException(String message){
        super(message);
        printStackTrace();
    }
}
