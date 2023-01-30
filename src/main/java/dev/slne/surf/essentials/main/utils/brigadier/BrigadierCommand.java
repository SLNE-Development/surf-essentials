package dev.slne.surf.essentials.main.utils.brigadier;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.SurfEssentials;
import net.minecraft.commands.CommandSourceStack;

public abstract class BrigadierCommand {

    public BrigadierCommand(){
        for (String name : names()) {
            SurfEssentials.registerPluginBrigadierCommand(name, this::literal).setUsage(usage())
                    .setDescription(description());
        }
    }

    public abstract String[] names();
    public abstract String usage();
    public abstract String description();

    public abstract void literal(LiteralArgumentBuilder<CommandSourceStack> literal);
}
