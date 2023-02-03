package dev.slne.surf.essentials.main.exceptions;

public class UnsupportedServerVersionException extends RuntimeException{
    public UnsupportedServerVersionException(String message){
        super(message);
        printStackTrace();
    }
}
