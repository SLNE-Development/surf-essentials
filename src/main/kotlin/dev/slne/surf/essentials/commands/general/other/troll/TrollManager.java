package dev.slne.surf.essentials.commands.general.other.troll;


import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.commands.general.other.troll.gui.Boarders;
import dev.slne.surf.essentials.commands.general.other.troll.gui.TrollGuiItems;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import static dev.slne.surf.essentials.utils.permission.Permissions.*;


public class TrollManager extends EssentialsCommand {

    public TrollManager() {
        super("troll", "troll <troll>", "troll players");

        withRequirement(EssentialsUtil.checkPermissions(TROLL_ALL_PERMISSION, TROLL_BOOM_PERMISSION, TROLL_DEMO_PERMISSION,
                TROLL_ILLUSIONER_PERMISSION, TROLL_ANVIL_PERMISSION, TROLL_VILLAGER_PERMISSION, TROLL_WATER_PERMISSION,
                TROLL_MLG_PERMISSION, TROLL_BELL_PERMISSION, TROLL_HEROBRINE_PERMISSION, TROLL_CAGE_PERMISSION, TROLL_CRASH_PERMISSION));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> gui(getPlayerOrException(sender)));

    }

//    @Override
//    public void literal(@NotNull LiteralArgumentBuilder<CommandSourceStack> literal){
//
//        literal.executes(context -> gui(context.getSource()));
//
//        literal.then(new BoomTroll().build());
//        literal.then(new DemoTroll().build());
//        literal.then(new IllusionerTroll().build());
//        literal.then(new AnvilTroll().build());
//        literal.then(new VillagerAnnoyTroll().build());
//        literal.then(new WaterTroll().build());
//        literal.then(new MlgTroll().build());
//        literal.then(new BellTroll().build());
//        literal.then(new HerobrineTroll().build());
//        literal.then(new CageTroll().build());
//        literal.then(new CrashTroll().build());
//        literal.then(new FakeBlockRainTroll().build());
//        literal.then(new FollowingBlockTroll().build());
//    }

    private int gui(Player target) throws WrapperCommandSyntaxException {
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

        gui.show(target);
        return 1;
    }
}

