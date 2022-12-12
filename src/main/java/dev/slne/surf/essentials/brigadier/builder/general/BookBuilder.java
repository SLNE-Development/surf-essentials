package dev.slne.surf.essentials.brigadier.builder.general;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class BookBuilder {
    public LiteralCommandNode<?> bookBuilder(){
        return LiteralArgumentBuilder.literal("book")
                .executes(context -> 0)
                .then(LiteralArgumentBuilder.literal("author")
                        .then(RequiredArgumentBuilder.argument("author", StringArgumentType.string())))
                .then(LiteralArgumentBuilder.literal("title")
                        .then(RequiredArgumentBuilder.argument("title", StringArgumentType.string()))).build();
    }
}
