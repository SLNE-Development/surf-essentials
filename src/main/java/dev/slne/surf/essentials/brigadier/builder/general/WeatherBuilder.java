package dev.slne.surf.essentials.brigadier.builder.general;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import java.util.List;

public class WeatherBuilder {
    public LiteralCommandNode<?> weatherBuilder(){
        return LiteralArgumentBuilder.literal("weather")
                .then(LiteralArgumentBuilder.literal("clear")
                        .then(RequiredArgumentBuilder.argument("duration", IntegerArgumentType.integer(0))
                                .suggests((context, builder) -> {
                                    List.of(TIME).forEach(builder::suggest);
                                    return builder.buildFuture();
                                })))
                .then(LiteralArgumentBuilder.literal("rain")
                        .then(RequiredArgumentBuilder.argument("duration", IntegerArgumentType.integer(0))
                                .suggests((context, builder) -> {
                                    List.of(TIME).forEach(builder::suggest);
                                    return builder.buildFuture();
                                })))
                .then(LiteralArgumentBuilder.literal("thunder")
                        .then(RequiredArgumentBuilder.argument("duration", IntegerArgumentType.integer(0))
                                .suggests((context, builder) -> {
                                    List.of(TIME).forEach(builder::suggest);
                                    return builder.buildFuture();
                                }))).build();
    }





    private final String[] TIME = {
            "150",
            "300",
            "600",
            "900",
            "1200",
            "1800",
    };
}
