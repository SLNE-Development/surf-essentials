package dev.slne.surf.essentials.brigadier.builder.general;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class TimeBuilder {
    public LiteralCommandNode<?> timeBuilder(){
        return LiteralArgumentBuilder.literal("time")
                .then(LiteralArgumentBuilder.literal("day"))
                .then(LiteralArgumentBuilder.literal("evening"))
                .then(LiteralArgumentBuilder.literal("night"))
                .then(LiteralArgumentBuilder.literal("midnight"))
                .then(LiteralArgumentBuilder.literal("set")
                        .then(RequiredArgumentBuilder.argument("time", IntegerArgumentType.integer(0, 24000)))
                ).build();
    }
}
