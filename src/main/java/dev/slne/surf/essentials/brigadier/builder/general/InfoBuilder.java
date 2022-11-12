package dev.slne.surf.essentials.brigadier.builder.general;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.bukkit.Bukkit;

public class InfoBuilder {
    public LiteralCommandNode infoBuilder() {
        return LiteralArgumentBuilder.literal("information")
                .then(RequiredArgumentBuilder.argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            Bukkit.getOnlinePlayers().forEach(player -> {
                                builder.suggest(player.getName());
                            });
                            return builder.buildFuture();
                        })).build();
    }
}
