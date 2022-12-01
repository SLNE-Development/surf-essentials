package dev.slne.surf.essentials.brigadier.builder.tp;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.bukkit.Bukkit;
import org.bukkit.Warning;

@Warning(reason = "not finished yet")
public class TeleportBuilder {
    public LiteralCommandNode<?> teleportBuilder(){
        return LiteralArgumentBuilder.literal("teleport")
                .then(RequiredArgumentBuilder.argument("locationx", DoubleArgumentType.doubleArg(-30000000, 30000000))
                        .suggests((context, builder) -> builder.suggest("~ ~ ~").buildFuture())
                        .then(RequiredArgumentBuilder.argument("locationy", DoubleArgumentType.doubleArg(-30000000, 30000000))
                                .suggests((context, builder) -> builder.suggest(" ~ ~").buildFuture())
                                .then(RequiredArgumentBuilder.argument("locationz", DoubleArgumentType.doubleArg(-30000000, 30000000))
                                        .suggests((context, builder) -> builder.suggest(" ~").buildFuture()))))

                .then(RequiredArgumentBuilder.argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            Bukkit.getOnlinePlayers().forEach(player -> builder.suggest(player.getName()));
                            return builder.buildFuture();
                        })
                        .then(RequiredArgumentBuilder.argument("player", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    Bukkit.getOnlinePlayers().forEach(player -> builder.suggest(player.getName()));
                                    return builder.buildFuture();
                                }))
                        .then(RequiredArgumentBuilder.argument("locationx", DoubleArgumentType.doubleArg(-30000000, 30000000))
                                .suggests((context, builder) -> builder.suggest("~ ~ ~").buildFuture())
                                .then(RequiredArgumentBuilder.argument("locationy", DoubleArgumentType.doubleArg(-30000000, 30000000))
                                        .suggests((context, builder) -> builder.suggest(" ~ ~").buildFuture())
                                        .then(RequiredArgumentBuilder.argument("locationz", DoubleArgumentType.doubleArg(-30000000, 30000000))
                                                .suggests((context, builder) -> builder.suggest(" ~").buildFuture()))))).build();

    }
    public LiteralCommandNode<?> teleport(){
        return LiteralArgumentBuilder.literal("teleport")
                .then(LiteralArgumentBuilder.literal("~")
                        .then(RequiredArgumentBuilder.argument("location", StringArgumentType.string()))
                        .then(LiteralArgumentBuilder.literal("~")
                                .then(RequiredArgumentBuilder.argument("location", StringArgumentType.string()))
                                .then(LiteralArgumentBuilder.literal("~")
                                        .then(RequiredArgumentBuilder.argument("location", StringArgumentType.string())))))

                .then(RequiredArgumentBuilder.argument("player", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            Bukkit.getOnlinePlayers().forEach(player -> builder.suggest(player.getName()));
                            return builder.buildFuture();
                        })
                        .then(LiteralArgumentBuilder.literal("~")
                                .then(LiteralArgumentBuilder.literal("~")
                                        .then(LiteralArgumentBuilder.literal("~"))))


                        .then(RequiredArgumentBuilder.argument("player", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    Bukkit.getOnlinePlayers().forEach(player -> builder.suggest(player.getName()));
                                    return builder.buildFuture();
                                }))

                ).build();
    }
}
