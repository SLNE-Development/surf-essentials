package dev.slne.surf.essentialsold.exceptions;

/**
 * An exception that is thrown when the server version is not supported.
 *
 * @author twisti
 * @since 1.0.0
 */
public class UnsupportedServerVersionException extends RuntimeException {

    /**
     * Constructs a new {@link UnsupportedServerVersionException} with the specified detail message.
     *
     * @param message the detail message
     * @author twisti
     * @since 1.0.0
     */
    public UnsupportedServerVersionException(String message) {
        super(message);
        printStackTrace();
    }
}
