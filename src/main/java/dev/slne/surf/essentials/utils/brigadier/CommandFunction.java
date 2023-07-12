package dev.slne.surf.essentials.utils.brigadier;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

/**
 * Represents a function that takes in an argument of type {@code T} and returns a result of type {@code R},
 * while potentially throwing a {@link CommandSyntaxException}.
 *
 * <p>This is a functional interface whose functional method is {@link #apply(Object)}.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface CommandFunction<T, R> {

    /**
     * Applies this function to the given argument, potentially throwing a {@link CommandSyntaxException}.
     *
     * @param value the function argument
     * @return the function result
     * @throws CommandSyntaxException if an error occurs while processing the command
     */
    R apply(T value) throws CommandSyntaxException;
}
