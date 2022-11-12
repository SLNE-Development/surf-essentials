package dev.slne.surf.essentials.brigadier.builder.general;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.bukkit.Bukkit;

public class GamemodeBuilder {
    public LiteralCommandNode<?> gamemodeBuilder(){
        return LiteralArgumentBuilder.literal("gamemode")
                .then(LiteralArgumentBuilder.literal("creative")
                        .then(LiteralArgumentBuilder.literal("@a"))
                        .then(RequiredArgumentBuilder.argument("player", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    Bukkit.getOnlinePlayers().forEach(player -> {
                                        builder.suggest(player.getName());
                                    });
                                    return builder.buildFuture();
                                })))
                .then(LiteralArgumentBuilder.literal("survival")
                        .then(LiteralArgumentBuilder.literal("@a"))
                        .then(RequiredArgumentBuilder.argument("player", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    Bukkit.getOnlinePlayers().forEach(player -> {
                                        builder.suggest(player.getName());
                                    });
                                    return builder.buildFuture();
                                })))
                .then(LiteralArgumentBuilder.literal("spectator")
                        .then(LiteralArgumentBuilder.literal("@a"))
                        .then(RequiredArgumentBuilder.argument("player", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    Bukkit.getOnlinePlayers().forEach(player -> {
                                        builder.suggest(player.getName());
                                    });
                                    return builder.buildFuture();
                                })))
                .then(LiteralArgumentBuilder.literal("adventure")
                        .then(LiteralArgumentBuilder.literal("@a"))
                        .then(RequiredArgumentBuilder.argument("player", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    Bukkit.getOnlinePlayers().forEach(player -> {
                                        builder.suggest(player.getName());
                                    });
                                    return builder.buildFuture();
                                }))).build();
    }

}
