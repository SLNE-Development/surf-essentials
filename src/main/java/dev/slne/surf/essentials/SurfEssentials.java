package dev.slne.surf.essentials;

import dev.slne.surf.essentials.commands.cheat.*;
import dev.slne.surf.essentials.commands.gamemode.AdventureCommand;
import dev.slne.surf.essentials.commands.gamemode.CreativeCommand;
import dev.slne.surf.essentials.commands.gamemode.SpectatorCommand;
import dev.slne.surf.essentials.commands.gamemode.SurvivalCommand;
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
        //Survuval
        new SurvivalCommand(getCommand("survival"));





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
