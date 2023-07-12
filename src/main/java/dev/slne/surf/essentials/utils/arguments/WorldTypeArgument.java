package dev.slne.surf.essentials.utils.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import org.bukkit.WorldType;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * Custom argument for {@link WorldType}
 */
public class WorldTypeArgument extends CustomArgument<WorldType, String> {
    /**
     * A {@link WorldType} argument
     *
     * @param nodeName the name of the node for this argument
     */
    public WorldTypeArgument(String nodeName) {
        super(new StringArgument(nodeName), info -> {
            String input = info.input();
            WorldType worldType = WorldType.getByName(input);

            if (worldType == null)
                throw CustomArgumentException.fromAdventureComponent(Exceptions.ERROR_WORLD_TYPE_NOT_EXISTS.message(input)); // throw an error if the input is not a WorldType
            return worldType;
        });
        replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> // suggest all WorldTypes
                CompletableFuture.supplyAsync(() ->
                        Arrays.stream(WorldType.values()).map(worldType -> worldType.getName().toLowerCase()).toList())));
    }
}
