package dev.slne.surf.essentials.brigadier.builder.general;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class VoteBuilder {
    public LiteralCommandNode<?> voteBuilder(){
        return LiteralArgumentBuilder.literal("poll")
                .then(RequiredArgumentBuilder.argument("poll", StringArgumentType.word())
                        .then(LiteralArgumentBuilder.literal("yes"))
                        .then(LiteralArgumentBuilder.literal("no"))).build();
    }
}
