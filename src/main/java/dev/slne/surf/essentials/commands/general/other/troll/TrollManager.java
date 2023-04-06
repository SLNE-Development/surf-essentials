package dev.slne.surf.essentials.commands.general.other.troll;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.commands.general.other.troll.gui.Boarders;
import dev.slne.surf.essentials.commands.general.other.troll.gui.TrollGuiItems;
import dev.slne.surf.essentials.commands.general.other.troll.trolls.*;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import static dev.slne.surf.essentials.utils.permission.Permissions.*;

public class TrollManager extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"troll"};
    }

    @Override
    public String usage() {
        return "/troll <troll>";
    }

    @Override
    public String description() {
        return "troll players";
    }

    @Override
    public void literal(@NotNull LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(EssentialsUtil.checkPermissions(TROLL_ALL_PERMISSION, TROLL_BOOM_PERMISSION, TROLL_DEMO_PERMISSION,
                TROLL_ILLUSIONER_PERMISSION, TROLL_ANVIL_PERMISSION, TROLL_VILLAGER_PERMISSION, TROLL_WATER_PERMISSION,
                TROLL_MLG_PERMISSION, TROLL_BELL_PERMISSION, TROLL_HEROBRINE_PERMISSION, TROLL_CAGE_PERMISSION, TROLL_CRASH_PERMISSION));

        literal.executes(context -> gui(context.getSource()));

        literal.then(Commands.literal("boom")
                .requires(EssentialsUtil.checkPermissions(TROLL_BOOM_PERMISSION, TROLL_ALL_PERMISSION))
                .then(BoomTroll.boom(literal)));

        literal.then(Commands.literal("demo")
                .requires(EssentialsUtil.checkPermissions(TROLL_DEMO_PERMISSION, TROLL_ALL_PERMISSION))
                .then(DemoTroll.demo(literal)));

        literal.then(Commands.literal("illusioner")
                .requires(EssentialsUtil.checkPermissions(TROLL_ILLUSIONER_PERMISSION, TROLL_ALL_PERMISSION))
                .then(IllusionerTroll.illusioner(literal)));

        literal.then(Commands.literal("anvil")
                .requires(EssentialsUtil.checkPermissions(TROLL_ANVIL_PERMISSION, TROLL_ALL_PERMISSION))
                .then(AnvilTroll.anvil(literal)));

        literal.then(Commands.literal("villager")
                .requires(EssentialsUtil.checkPermissions(TROLL_VILLAGER_PERMISSION, TROLL_ALL_PERMISSION))
                .then(VillagerAnnoyTroll.villager(literal)));

        literal.then(Commands.literal("water")
                .requires(EssentialsUtil.checkPermissions(TROLL_WATER_PERMISSION, TROLL_ALL_PERMISSION))
                .then(WaterTroll.water(literal)));

        literal.then(Commands.literal("mlg")
                .requires(EssentialsUtil.checkPermissions(TROLL_MLG_PERMISSION, TROLL_ALL_PERMISSION))
                .then(MlgTroll.mlg(literal)));

        literal.then(Commands.literal("bell")
                .requires(EssentialsUtil.checkPermissions(TROLL_BELL_PERMISSION, TROLL_ALL_PERMISSION))
                .then(BellTroll.bell(literal)));

        literal.then(Commands.literal("herobrine")
                .requires(EssentialsUtil.checkPermissions(TROLL_HEROBRINE_PERMISSION, TROLL_ALL_PERMISSION))
                .then(HerobrineTroll.herobrine(literal)));

        literal.then(Commands.literal("cage")
                .requires(EssentialsUtil.checkPermissions(TROLL_CAGE_PERMISSION, TROLL_ALL_PERMISSION))
                .then(CageTroll.cage(literal)));

        literal.then(Commands.literal("crash")
                .requires(EssentialsUtil.checkPermissions(4, TROLL_CRASH_PERMISSION, TROLL_ALL_PERMISSION))
                .then(CrashTroll.crashTroll(literal)));
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
        trollSelection.addItem(TrollGuiItems.waterTroll(), 2, 1);
        trollSelection.addItem(TrollGuiItems.crashTroll(), 3, 1);

        gui.addPane(trollSelection);

        gui.show(player.getBukkitEntity());
        return 1;
    }
}
