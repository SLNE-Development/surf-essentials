package dev.slne.surf.essentials.main.commands;

import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.commands.general.other.world.WorldCommand;

import java.util.ArrayList;
import java.util.List;


/**
 * The class Commands
 */
public class Commands {
    private final List<EssentialsCommand> commands;
    SurfEssentials surf;


    public Commands(){
        this.commands = new ArrayList<>();
        this.surf = SurfEssentials.getInstance();
    }

    /**
     * initialize General Commands
     */
    public void initializeGeneralCommands(){
        //WorldCommand
        this.commands.add(new WorldCommand(this.surf.getCommand("world")));
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
