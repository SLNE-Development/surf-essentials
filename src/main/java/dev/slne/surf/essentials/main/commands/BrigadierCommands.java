package dev.slne.surf.essentials.main.commands;

import dev.slne.surf.essentials.main.commands.general.BroadcastWorldCommand;

public class BrigadierCommands {
    public static void register(){
        //world-broadcast command
        BroadcastWorldCommand.register();
    }
}
