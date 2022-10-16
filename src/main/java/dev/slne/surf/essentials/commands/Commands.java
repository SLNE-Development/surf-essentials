package dev.slne.surf.essentials.commands;

import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.commands.cheat.*;
import dev.slne.surf.essentials.commands.general.*;
import dev.slne.surf.essentials.commands.tp.TeleportAll;
import dev.slne.surf.essentials.commands.tp.TeleportToTop;

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
     * initialize Cheat Commands
     */
    public void initializeCheatCommands(){
        //SpeedCommand
        this.commands.add(new SpeedCommand(this.surf.getCommand("speed")));
        //FlyCommand
        this.commands.add(new FlyCommand(this.surf.getCommand("fly")));
        //FeedCommand
        this.commands.add(new FoodCommand(this.surf.getCommand("feed")));
        //GodmodeCommand
        this.commands.add(new GodmodeCommand(this.surf.getCommand("godmode")));
        //heahlCommand
        this.commands.add(new HealCommand(this.surf.getCommand("heal")));
        //repairCommand
        this.commands.add(new RepairCommand(this.surf.getCommand("repair")));
    }

    /**
     * initialize General Commands
     */
    public void initializeGeneralCommands(){
        //AlertCommand
        this.commands.add(new AlertCommand(this.surf.getCommand("alert")));
        //GamemodeCommand
        this.commands.add(new GamemodeCommand(this.surf.getCommand("gamemode")));
        //InfoCommand
        this.commands.add(new InfoCommand(this.surf.getCommand("info")));
        //MsgCommand
        this.commands.add(new MsgCommand(this.surf.getCommand("msg")));
        //RuleCommand
        this.commands.add(new RuleCommand(this.surf.getCommand("rule")));
        //TimeCommand
        this.commands.add(new TimeCommand(this.surf.getCommand("time")));
        //WeatherCommand
        this.commands.add(new WeatherCommand(this.surf.getCommand("weather")));
    }

    /**
     * initialize Teleport Commands
     */
    public void initializeTpCommands(){
        //TeleportAllCommand
        this.commands.add(new TeleportAll(this.surf.getCommand("tpall")));
        //TeleportToTopCommand
        this.commands.add(new TeleportToTop(this.surf.getCommand("tptop")));
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
