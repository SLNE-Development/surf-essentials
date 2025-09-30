package dev.slne.surf.essentialsold.utils.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.slne.surf.essentialsold.utils.brigadier.Exceptions;
import io.papermc.paper.datapack.Datapack;
import io.papermc.paper.datapack.DatapackManager;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * An argument that represents a datapack
 * <br>
 * It returns a {@link Datapack} object
 */
public class DatapackArgument extends CustomArgument<Datapack, String> {
    /**
     * A Datapack argument
     *
     * @param nodeName         the name of this argument
     * @param onlyNotEnabledPacks whether to only allow enabled datapacks
     */
    public DatapackArgument(String nodeName, TriState onlyNotEnabledPacks) {
        super(new TextArgument(nodeName), info -> {
            final String packName = info.currentInput();
            final DatapackManager datapackManager = Bukkit.getDatapackManager();
            final Datapack datapack = datapackManager.getPacks() // get all datapacks
                    .stream()
                    .filter(pack -> pack.getName().equals(packName)) // search for datapack with the given name
                    .findFirst()
                    .orElseThrow(() -> CustomArgumentException.fromAdventureComponent(Exceptions.ERROR_UNKNOWN_DATA_PACK.message(packName))); // no datapack found

            final boolean isSelectedPack = datapackManager.getEnabledPacks().contains(datapack); // check if datapack is enabled
            final Datapack.Compatibility compatibility = datapack.getCompatibility(); // get datapack compatibility

            if (onlyNotEnabledPacks == TriState.TRUE && isSelectedPack) // if only not enabled datapacks are allowed and the datapack is enabled
                throw CustomArgumentException.fromAdventureComponent(Exceptions.ERROR_DATA_PACK_ALREADY_ENABLED.message(datapack));
            if (onlyNotEnabledPacks == TriState.FALSE && !isSelectedPack) // if only enabled datapacks are allowed and the datapack is not enabled
                throw CustomArgumentException.fromAdventureComponent(Exceptions.ERROR_DATA_PACK_ALREADY_DISABLED.message(datapack));
            if (compatibility != Datapack.Compatibility.COMPATIBLE) // if the datapack is not compatible
                throw CustomArgumentException.fromAdventureComponent(Exceptions.ERROR_DATA_PACK_INCOMPATIBLE.message(datapack));

            return datapack;
        });

        replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.supplyAsync(() -> { // suggest all datapacks
            final DatapackManager datapackManager = Bukkit.getDatapackManager();
            final Collection<String> suggestions = new ArrayList<>();

            switch (onlyNotEnabledPacks) {
                case FALSE ->
                        suggestions.addAll(datapackManager.getEnabledPacks().stream().map(Datapack::getName).toList()); // add all enabled datapacks
                case TRUE ->
                        suggestions.addAll(datapackManager.getPacks().stream().filter(datapack -> !datapack.isEnabled()).map(Datapack::getName).toList()); // add all not enabled datapacks
                case NOT_SET -> suggestions.addAll(datapackManager.getPacks().stream().map(Datapack::getName).toList()); // add all datapacks
            }

            return suggestions;
        })));
    }
}
