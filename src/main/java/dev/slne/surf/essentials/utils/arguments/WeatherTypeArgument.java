package dev.slne.surf.essentials.utils.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.essentials.commands.minecraft.WeatherCommand;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * Custom argument for {@link WeatherCommand.WeatherType}s
 */
public class WeatherTypeArgument extends CustomArgument<WeatherCommand.WeatherType, String> {

    /**
     * A {@link WeatherCommand.WeatherType} argument
     *
     * @param nodeName the name of the node for this argument
     */
    public WeatherTypeArgument(String nodeName) {
        super(new StringArgument(nodeName), info -> {
            String type = info.currentInput().toUpperCase();
            try {
                return WeatherCommand.WeatherType.valueOf(type); // try to parse the input as a WeatherType
            } catch (IllegalArgumentException e) {
                throw CustomArgumentException.fromAdventureComponent(Exceptions.WEATHER_TYPE_NOT_EXISTS.message(type)); // throw an error if the input is not a WeatherType
            }
        });
        replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.supplyAsync(() -> Arrays.stream(WeatherCommand.WeatherType.values()) // suggest all WeatherTypes
                .map(weatherType -> weatherType.name().toLowerCase())
                .toList())));
    }
}
