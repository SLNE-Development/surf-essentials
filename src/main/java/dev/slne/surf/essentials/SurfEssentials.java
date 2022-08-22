package dev.slne.surf.essentials;

import dev.slne.surf.essentials.commands.cheat.*;
import dev.slne.surf.essentials.commands.gamemode.AdventureCommand;
import dev.slne.surf.essentials.commands.gamemode.CreativeCommand;
import dev.slne.surf.essentials.commands.gamemode.SpectatorCommand;
import dev.slne.surf.essentials.commands.gamemode.SurvivalCommand;
import dev.slne.surf.essentials.commands.time.DayCommand;
import dev.slne.surf.essentials.commands.time.EveningCommand;
import dev.slne.surf.essentials.commands.time.MidnightCommand;
import dev.slne.surf.essentials.commands.time.NightCommand;
import dev.slne.surf.essentials.commands.tp.TeleportToTop;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SurfEssentials extends JavaPlugin {

    private static SurfEssentials instance;
    //Check if the Plugin is already initialized
    public SurfEssentials() {
        if (SurfEssentials.instance != null) {
            throw new Error("Plugin already initialized!");
        }
        //Plugin constructor
        SurfEssentials.instance = this;
    }
    //Get Plugin instance
    public static SurfEssentials getInstance() {
        return instance;
    }

    // Plugin startup logic
    @Override
    public void onEnable() {
        //Start message
        System.out.println("The plugin is starting...");
        //Plugin Manager shortcut
        PluginManager pluginManager = Bukkit.getPluginManager();
        //fly Command
        new FlyCommand(getCommand("fly"));
        //Heal
        new HealCommand(getCommand("heal"));
        //Godmode
        new GodmodeCommand(getCommand("godmode"));
        //Food
        new FoodCommand(getCommand("feed"));
        //Repair
        new RepairCommand(getCommand("repair"));
        //Adventure
        new AdventureCommand(getCommand("adventure"));
        //Creative
        new CreativeCommand(getCommand("creative"));
        //Spectator
        new SpectatorCommand(getCommand("spectator"));
        //Survival
        new SurvivalCommand(getCommand("survival"));
        //Day
        new DayCommand(getCommand("day"));
        //Evening
        new EveningCommand(getCommand("evening"));
        //Midnight
        new MidnightCommand(getCommand("midnight"));
        //night
        new NightCommand(getCommand("night"));
        //Teleport to highest Block
        new TeleportToTop(getCommand("tptop"));





        //Success start message
        System.out.println("The plugin has started successfully!");
    }
    // Plugin shutdown logic
    @Override
    public void onDisable() {

        //Stop message
        System.out.println("The plugin has stopped!");
    }
}
