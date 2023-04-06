package dev.slne.surf.essentials.utils.brigadier;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.SurfEssentials;
import net.minecraft.commands.CommandSourceStack;

public abstract class BrigadierCommand {

    public BrigadierCommand(){
        for (String name : names()) {
            SurfEssentials.registerPluginBrigadierCommand(name, this::literal);
        }
    }

    public abstract String[] names();
    public abstract String usage();
    public abstract String description();

    public abstract void literal(LiteralArgumentBuilder<CommandSourceStack> literal);
}
