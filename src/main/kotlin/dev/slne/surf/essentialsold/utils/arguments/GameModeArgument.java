package dev.slne.surf.essentialsold.utils.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.essentialsold.utils.brigadier.Exceptions;
import org.bukkit.GameMode;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

/**
 * Custom argument for {@link GameMode}
 */
public class GameModeArgument extends CustomArgument<GameMode, String> {
    /**
     * A {@link GameMode} argument
     *
     * @param nodeName the name of the node for this argument
     */
    public GameModeArgument(String nodeName) {
        super(new StringArgument(nodeName), info -> {
            try {
                return GameMode.valueOf(info.currentInput().toUpperCase(Locale.ENGLISH)); // try to parse the input as a GameMode
            } catch (IllegalArgumentException e) {
                throw CustomArgumentException.fromAdventureComponent(Exceptions.ERROR_INVALID_GAME_MODE.message(info.currentInput())); // throw an error if the input is not a GameMode
            }
        });
        replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.supplyAsync(() -> Arrays.stream(GameMode.values()) // suggest all GameModes
                .map(gameMode -> gameMode.name().toLowerCase(Locale.ENGLISH))
                .toList())));
    }
}
