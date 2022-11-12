package dev.slne.surf.essentials.brigadier.builder.cheat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class FlyBuilder {
    public LiteralCommandNode<?> flyBuilder(){
        return LiteralArgumentBuilder.literal("fly").build();
    }
}
