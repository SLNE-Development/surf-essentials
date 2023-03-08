package dev.slne.surf.essentials.commands.general.other.troll;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.commands.general.other.troll.gui.Boarders;
import dev.slne.surf.essentials.commands.general.other.troll.gui.TrollGuiItems;
import dev.slne.surf.essentials.commands.general.other.troll.listener.TrollListener;
import dev.slne.surf.essentials.commands.general.other.troll.trolls.*;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class TrollManager{
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("troll", TrollManager::literal);
        //Listener
        TrollListener.register();
    }

    private static void literal(@NotNull LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.TROLL_PERMISSION));

        // open the troll gui
        literal.executes(context -> gui(context.getSource()));

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

        //cage
        literal.then(Commands.literal("cage")
                .then(CageTroll.cage(literal)));
    }

    private static int gui(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ChestGui gui = new ChestGui(6, ComponentHolder.of(Component.text("Troll GUI", Colors.SECONDARY)));

        gui.setOnGlobalClick(event -> event.setCancelled(true));
        Boarders.setAllBoarders(gui);

        StaticPane trollSelection = new StaticPane(1, 1,7,3);

        trollSelection.addItem(TrollGuiItems.anvilTroll(), 0,0);
        trollSelection.addItem(TrollGuiItems.bellTroll(), 1,0);
        trollSelection.addItem(TrollGuiItems.boomTroll(), 2,0);
        trollSelection.addItem(TrollGuiItems.cageTroll(), 3,0);
        trollSelection.addItem(TrollGuiItems.demoTroll(), 4,0);
        trollSelection.addItem(TrollGuiItems.herobrineTroll(), 5,0);
        trollSelection.addItem(TrollGuiItems.illusionerTroll(), 6,0);
        trollSelection.addItem(TrollGuiItems.mlgTroll(), 0,1);
        trollSelection.addItem(TrollGuiItems.villagerAnnoyTroll(), 1,1);
        trollSelection.addItem(TrollGuiItems.waterTroll(), 2,1);

        gui.addPane(trollSelection);

        gui.show(player.getBukkitEntity());
        return 1;
    }
}
