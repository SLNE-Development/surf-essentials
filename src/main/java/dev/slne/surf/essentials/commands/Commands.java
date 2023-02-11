package dev.slne.surf.essentials.commands;

import dev.slne.surf.essentials.SurfEssentials;

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
