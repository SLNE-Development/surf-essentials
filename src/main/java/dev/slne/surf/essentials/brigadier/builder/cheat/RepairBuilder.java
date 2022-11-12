package dev.slne.surf.essentials.brigadier.builder.cheat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class RepairBuilder {
    public LiteralCommandNode<?> repairBuilder(){
        return LiteralArgumentBuilder.literal("repair").build();
    }
}
