package dev.slne.surf.essentials.brigadier.builder.cheat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class SuicideBuilder {
    public LiteralCommandNode<?> suicideBuilder(){
        return LiteralArgumentBuilder.literal("suicide").build();
    }

}
