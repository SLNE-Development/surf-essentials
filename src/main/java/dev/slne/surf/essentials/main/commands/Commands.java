package dev.slne.surf.essentials.main.commands;

import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.commands.cheat.*;
import dev.slne.surf.essentials.main.commands.general.*;
import dev.slne.surf.essentials.main.commands.general.other.poll.PollManager;
import dev.slne.surf.essentials.main.commands.general.other.poll.VoteCommand;
import dev.slne.surf.essentials.main.commands.general.other.world.WorldCommand;
import dev.slne.surf.essentials.main.commands.minecraft.TimeCommand;
import dev.slne.surf.essentials.main.commands.minecraft.WeatherCommand;
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
        //fillStackCommand
        this.commands.add(new FillStackCommand(this.surf.getCommand("more")));
        //suicideCommand
        this.commands.add(new SuicideCommand(this.surf.getCommand("suicide")));

    }

    /**
     * initialize General Commands
     */
    public void initializeGeneralCommands(){
        //AlertCommand
        this.commands.add(new AlertCommand(this.surf.getCommand("alert")));
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
        //SpawnerCommand
        this.commands.add(new SpawnerChangeCommand(this.surf.getCommand("spawner")));
        //WorldCommand
        this.commands.add(new WorldCommand(this.surf.getCommand("world")));
        //Poll Command
        this.commands.add(new PollManager(this.surf.getCommand("poll")));
        //Vote Command
        this.commands.add(new VoteCommand(this.surf.getCommand("vote")));
        //Book Command
        this.commands.add(new BookCommand(this.surf.getCommand("book")));

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
