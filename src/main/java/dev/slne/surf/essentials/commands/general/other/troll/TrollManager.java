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

        literal.then(new BoomTroll().build());
        literal.then(new DemoTroll().build());
        literal.then(new IllusionerTroll().build());
        literal.then(new AnvilTroll().build());
        literal.then(new VillagerAnnoyTroll().build());
        literal.then(new WaterTroll().build());
        literal.then(new MlgTroll().build());
        literal.then(new BellTroll().build());
        literal.then(new HerobrineTroll().build());
        literal.then(new CageTroll().build());
        literal.then(new CrashTroll().build());
        literal.then(new FakeBlockRainTroll().build());
        literal.then(new FollowingBlockTroll().build());
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
