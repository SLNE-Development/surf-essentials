package dev.slne.surf.essentials.brigadier.builder.general;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class PollBuilder {
    public LiteralCommandNode<?> pollBuilder(){
        return LiteralArgumentBuilder.literal("poll")
                .then(LiteralArgumentBuilder.literal("create")
                        .then(RequiredArgumentBuilder.argument("name", StringArgumentType.string())
                                .then(RequiredArgumentBuilder.argument("timeInSeconds", IntegerArgumentType.integer(1))
                                        .then(RequiredArgumentBuilder.argument("question", StringArgumentType.greedyString())))))

                .then(LiteralArgumentBuilder.literal("end")
                        .then(RequiredArgumentBuilder.argument("poll", StringArgumentType.word())))
                .then(LiteralArgumentBuilder.literal("delete")
                        .then(RequiredArgumentBuilder.argument("poll", StringArgumentType.word())))
                .then(LiteralArgumentBuilder.literal("list")).build();
    }
}
