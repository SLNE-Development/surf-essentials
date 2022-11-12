package dev.slne.surf.essentials.brigadier.builder.cheat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class FoodBuilder {
    public LiteralCommandNode<?> foodBuilder(){
        return LiteralArgumentBuilder.literal("food").build();
    }
}
