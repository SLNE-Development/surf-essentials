package dev.slne.surf.essentials.main.commands.general.other.troll;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.SurfEssentials;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class TrollManager {
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("troll", TrollManager::literal);
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        //boom troll
        literal.then(Commands.literal("boom")
                .then(BoomTroll.boom(literal)));

        //demo troll
        literal.then(Commands.literal("demo")
                .then(DemoTroll.demo(literal)));
    }
}
