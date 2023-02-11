package dev.slne.surf.essentials.exceptions;

public class UnsupportedServerVersionException extends RuntimeException{
    public UnsupportedServerVersionException(String message){
        super(message);
        printStackTrace();
    }
}
