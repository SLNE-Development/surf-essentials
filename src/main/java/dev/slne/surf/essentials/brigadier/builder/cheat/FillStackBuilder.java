package dev.slne.surf.essentials.brigadier.builder.cheat;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class FillStackBuilder {
    public LiteralCommandNode<?> fillStackBuilder(){
        return LiteralArgumentBuilder.literal("more")
                .then(RequiredArgumentBuilder.argument("amount", IntegerArgumentType.integer(0, 64))).build();
    }
}
