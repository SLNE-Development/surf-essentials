package dev.slne.surf.essentials.main.commands.general.other.troll;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.commands.general.other.troll.listener.TrollListener;
import dev.slne.surf.essentials.main.commands.general.other.troll.trolls.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;

public class TrollManager {
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("troll", TrollManager::literal);
        //Listener
        TrollListener.register();
    }

    private static void literal(@NotNull LiteralArgumentBuilder<CommandSourceStack> literal){
        //boom troll
        literal.then(Commands.literal("boom")
                .then(BoomTroll.boom(literal)));

        //demo troll
        literal.then(Commands.literal("demo")
                .then(DemoTroll.demo(literal)));

        //illusioner troll
        literal.then(Commands.literal("illusioner")
                .then(IllusionerTroll.illusioner(literal)));

        //anvil troll
        literal.then(Commands.literal("anvil")
                .then(AnvilTroll.anvil(literal)));

        //villager
        literal.then(Commands.literal("villager")
                .then(VillagerAnnoyTroll.villager(literal)));

        //water
        literal.then(Commands.literal("water")
                .then(WaterTroll.water(literal)));

        //mlg
        literal.then(Commands.literal("mlg")
                .then(MlgTroll.mlg(literal)));

        //bell
        literal.then(Commands.literal("bell")
                .then(BellTroll.bell(literal)));

        //herobrine
        literal.then(Commands.literal("herobrine")
                .then(HerobrineTroll.herobrine(literal)));
    }
}
