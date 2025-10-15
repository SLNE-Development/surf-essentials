package dev.slne.surf.essentialsold.utils.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.essentialsold.utils.brigadier.Exceptions;
import org.bukkit.Difficulty;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

/**
 * A custom argument that represents a {@link Difficulty}
 */
public class DifficultyArgument extends CustomArgument<Difficulty, String> {
    /**
     * A {@link Difficulty} argument
     *
     * @param nodeName the name of the node for this argument
     */
    public DifficultyArgument(String nodeName) {
        super(new StringArgument(nodeName), info -> {
            try {
                return Difficulty.valueOf(info.currentInput().toUpperCase(Locale.ENGLISH)); // try to parse the input as a Difficulty
            } catch (IllegalArgumentException e) {
                throw CustomArgumentException.fromAdventureComponent(Exceptions.ERROR_INVALID_DIFFICULTY.message(info.currentInput())); // throw an error if the input is not a Difficulty
            }
        });
        replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.supplyAsync(() -> Arrays.stream(Difficulty.values()) // suggest all Difficulties
                .map(difficulty -> difficulty.name().toLowerCase(Locale.ENGLISH))
                .toList())));
    }
}
