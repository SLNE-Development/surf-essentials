package dev.slne.surf.essentials.main.commands;

import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.commands.general.MsgCommand;
import dev.slne.surf.essentials.main.commands.general.other.world.WorldCommand;
import dev.slne.surf.essentials.main.commands.tp.TeleportAll;
import dev.slne.surf.essentials.main.commands.tp.TeleportCommand;
import dev.slne.surf.essentials.main.commands.tp.TeleportToTop;

import java.util.ArrayList;
import java.util.List;


/**
 * The class Commands
 */
public class Commands {
    private List<EssentialsCommand> commands;
    SurfEssentials surf;


    public Commands(){
        this.commands = new ArrayList<>();
        this.surf = SurfEssentials.getInstance();
    }

    /**
     * initialize General Commands
     */
    public void initializeGeneralCommands(){
        //MsgCommand
        this.commands.add(new MsgCommand(this.surf.getCommand("msg")));
        //WorldCommand
        this.commands.add(new WorldCommand(this.surf.getCommand("world")));

    }

    /**
     * initialize Teleport Commands
     */
    public void initializeTpCommands(){
        //TeleportAllCommand
        this.commands.add(new TeleportAll(this.surf.getCommand("tpall")));
        //TeleportToTopCommand
        this.commands.add(new TeleportToTop(this.surf.getCommand("tptop")));
        //TeleportOfflineCommand
        this.commands.add(new TeleportCommand(this.surf.getCommand("teleport")));
    }



    /**
     *
     * Gets the commands
     *
     * @return the commands
     */
    public List<EssentialsCommand> getCommands(){
        return commands;
    }
}
