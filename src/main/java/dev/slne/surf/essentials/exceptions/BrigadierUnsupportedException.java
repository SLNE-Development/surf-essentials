package dev.slne.surf.essentials.exceptions;

/**
 * Exception thrown when Brigadier is not supported by the server.
 */
public final class BrigadierUnsupportedException extends UnsupportedOperationException{

    public BrigadierUnsupportedException(String message){
        super(message);
    }
}
