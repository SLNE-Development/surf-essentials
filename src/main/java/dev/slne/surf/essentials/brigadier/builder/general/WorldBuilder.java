package dev.slne.surf.essentials.brigadier.builder.general;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class WorldBuilder {
    public LiteralCommandNode<?> worldChangeBuilder(){
        return LiteralArgumentBuilder.literal("world")
                .then(LiteralArgumentBuilder.literal("create")
                        .then(RequiredArgumentBuilder.argument("worldName", StringArgumentType.word())
                                .then(RequiredArgumentBuilder.argument("environment", StringArgumentType.word())
                                        .executes(context -> 0)
                                        .then(RequiredArgumentBuilder.argument("worldType", StringArgumentType.word())
                                                .executes(context -> 0)
                                                .then(RequiredArgumentBuilder.argument("generateStructures", BoolArgumentType.bool())
                                                        .executes(context -> 0))))))


                .then(LiteralArgumentBuilder.literal("join")
                        .then(RequiredArgumentBuilder.argument("world", StringArgumentType.word())
                                .executes(context -> 0)
                                .then(RequiredArgumentBuilder.argument("player", StringArgumentType.word()))))

                .then(LiteralArgumentBuilder.literal("load")
                        .then(RequiredArgumentBuilder.argument("world", StringArgumentType.word())))

                .then(LiteralArgumentBuilder.literal("unload")
                        .then(RequiredArgumentBuilder.argument("world", StringArgumentType.word())))

                .then(LiteralArgumentBuilder.literal("remove")
                        .then(RequiredArgumentBuilder.argument("world", StringArgumentType.word()))).build();
    }
}
