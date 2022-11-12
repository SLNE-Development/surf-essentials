package dev.slne.surf.essentials.brigadier.builder.cheat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class HealBuilder {
    public LiteralCommandNode<?> healBuilder(){
        return LiteralArgumentBuilder.literal("heaL").build();
    }
}
