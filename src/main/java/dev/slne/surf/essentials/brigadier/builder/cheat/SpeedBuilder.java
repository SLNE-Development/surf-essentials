package dev.slne.surf.essentials.brigadier.builder.cheat;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class SpeedBuilder {
    public LiteralCommandNode<?> speedBuilder(){
        return LiteralArgumentBuilder.literal("speed")
                .then(LiteralArgumentBuilder.literal("default"))
                .then(RequiredArgumentBuilder.argument("speed", FloatArgumentType.floatArg(0, 1))).build();
    }
}
