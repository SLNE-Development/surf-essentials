package dev.slne.surf.essentials.brigadier.builder.general;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.bukkit.entity.EntityType;

public class SpawnerBuilder {
    public LiteralCommandNode<?> spawnerBuilder(){
        return LiteralArgumentBuilder.literal("spawner")
                .executes(context -> 0)
                .then(RequiredArgumentBuilder.argument("entity", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            EntityType[] entitys = EntityType.values();
                            for (EntityType entity : entitys) {
                                builder.suggest(entity.name());
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> 0)
                        .then(RequiredArgumentBuilder.argument("minSpawnDelay", IntegerArgumentType.integer(0, 9999999))
                                .executes(context -> 0)
                                .then(RequiredArgumentBuilder.argument("maxSpawnDelay", IntegerArgumentType.integer(1, 9999999))
                                        .executes(context -> 0)
                                        .then(RequiredArgumentBuilder.argument("spawnRange", IntegerArgumentType.integer(1, 500))
                                                .executes(context -> 0)
                                                .then(RequiredArgumentBuilder.argument("requiredPlayerRange", IntegerArgumentType.integer(1, 9999999))
                                                        .executes(context -> 0)))))).build();
    }
}
