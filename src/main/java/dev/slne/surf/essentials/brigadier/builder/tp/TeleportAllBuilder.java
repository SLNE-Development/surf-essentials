package dev.slne.surf.essentials.brigadier.builder.tp;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class TeleportAllBuilder {
    public LiteralCommandNode<?> teleportAllBuilder(){
        return LiteralArgumentBuilder.literal("tpall")
                .then(LiteralArgumentBuilder.literal("confirm")).build();
    }
}
