package dev.slne.surf.essentials.main.commands;

import dev.slne.surf.essentials.main.commands.general.BroadcastWorldCommand;
import dev.slne.surf.essentials.main.commands.general.ListCommand;

public class BrigadierCommands {
    public static void register(){
        //world-broadcast command
        BroadcastWorldCommand.register();
        //list command
        ListCommand.register();
    }
}
