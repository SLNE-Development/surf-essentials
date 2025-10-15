package dev.slne.surf.essentialsold.utils.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.essentialsold.utils.brigadier.Exceptions;
import lombok.val;
import org.bukkit.World;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * Custom argument for {@link World.Environment}
 */
public class WorldEnvironmentArgument extends CustomArgument<World.Environment, String> {
    /**
     * A {@link World.Environment} argument
     * @param nodeName the name of the node for this argument
     */
    public WorldEnvironmentArgument(String nodeName) {
        super(new StringArgument(nodeName), info -> {
            val input = info.currentInput();
            final World.Environment environment;
            try {
                environment = World.Environment.valueOf(input.toUpperCase());
                if (environment == World.Environment.CUSTOM) throw new IllegalArgumentException(); // throw an error if the input is CUSTOM
                return environment; // try to parse the input as a World.Environment
            } catch (IllegalArgumentException e) {
                throw CustomArgumentException.fromAdventureComponent(Exceptions.ERROR_WORLD_ENVIRONMENT_NOT_EXISTS.message(input)); // throw an error if the input is not a World.Environment
            }
        });
        replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.supplyAsync(() -> Arrays.stream(World.Environment.values()) // suggest all World.Environments
                .filter(environment -> environment != World.Environment.CUSTOM) // don't suggest CUSTOM
                .map(worldEnvironment -> worldEnvironment.name().toLowerCase())
                .toList())));
    }
}
