package dev.slne.surf.essentials.exceptions;

public class InvalidStringTimeException extends RuntimeException{
    public InvalidStringTimeException(String message){
        super(message);
        printStackTrace();
    }
}
