package dev.slne.surf.essentials.main.commands;

import dev.slne.surf.essentials.main.commands.cheat.LightningCommand;
import dev.slne.surf.essentials.main.commands.cheat.WorkbenchCommand;
import dev.slne.surf.essentials.main.commands.general.BroadcastWorldCommand;
import dev.slne.surf.essentials.main.commands.general.EnchantCommand;
import dev.slne.surf.essentials.main.commands.general.KillCommand;
import dev.slne.surf.essentials.main.commands.general.ListCommand;

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
    }
}
