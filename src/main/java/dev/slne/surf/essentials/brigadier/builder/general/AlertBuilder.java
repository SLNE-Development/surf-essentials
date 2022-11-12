package dev.slne.surf.essentials.brigadier.builder.general;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class AlertBuilder {
    public LiteralCommandNode<?> alertBuilder(){
        return LiteralArgumentBuilder.literal("alert")
                .then(RequiredArgumentBuilder.argument("message", StringArgumentType.greedyString())).build();
    }
}
