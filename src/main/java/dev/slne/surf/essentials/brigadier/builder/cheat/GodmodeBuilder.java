package dev.slne.surf.essentials.brigadier.builder.cheat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class GodmodeBuilder {
    public LiteralCommandNode<?> godmodeBuilder(){
        return LiteralArgumentBuilder.literal("godmode").build();
    }
}
