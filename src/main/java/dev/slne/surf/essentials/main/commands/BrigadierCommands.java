package dev.slne.surf.essentials.main.commands;

import dev.slne.surf.essentials.main.commands.cheat.HurtCommand;
import dev.slne.surf.essentials.main.commands.cheat.LightningCommand;
import dev.slne.surf.essentials.main.commands.cheat.gui.*;
import dev.slne.surf.essentials.main.commands.general.*;
import dev.slne.surf.essentials.main.commands.general.other.troll.TrollManager;
import dev.slne.surf.essentials.main.commands.minecraft.*;

public class BrigadierCommands {
    public static void register(){
        //world-broadcast command
        BroadcastWorldCommand.register();
        //list command
        ListCommand.register();
        //lighting command
        LightningCommand.register();
        //kill command
        KillCommand.register();
        //enchant command
        EnchantCommand.register();
        //workbench command
        WorkbenchCommand.register();
        //anvil command
        AnvilCommand.register();
        //smithing table command
        SmithingTableCommand.register();
        //cartographytable command
        CartographyTableCommand.register();
        //grindtsone command
        GrindstoneCommand.register();
        //loom command
        LoomCommand.register();
        //stonecutter
        StonecutterCommand.register();
        //give command
        GiveCommand.register();
        //getpos command
        GetPosCommand.register();
        //troll command
        TrollManager.register();
        //hurt command
        HurtCommand.register();
        //bossbar command
        BossbarCommand.register();
        //op command
        OpCommand.register();
        //deop command
        DeopCommand.register();
        //effect command
        EffectCommand.register();
        //default game mode command
        DefaultGamemodeCommand.register();
        //difficulty command
        DifficultyCommand.register();
    }
}
