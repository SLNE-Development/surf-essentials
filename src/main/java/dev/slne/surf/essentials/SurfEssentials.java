package dev.slne.surf.essentials;

import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.commands.cheat.*;
import dev.slne.surf.essentials.commands.gamemode.AdventureCommand;
import dev.slne.surf.essentials.commands.gamemode.CreativeCommand;
import dev.slne.surf.essentials.commands.gamemode.SpectatorCommand;
import dev.slne.surf.essentials.commands.gamemode.SurvivalCommand;
import dev.slne.surf.essentials.commands.general.*;
import dev.slne.surf.essentials.commands.general.sign.EditSignListener;
import dev.slne.surf.essentials.commands.tp.TeleportAll;
import dev.slne.surf.essentials.commands.tp.TeleportToTop;
import dev.slne.surf.essentials.commands.weather.RainCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
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
        getLogger().info("The plugin is starting...");
        //fancy Message
        loadMessage();
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
        //Teleport to highest Block
        new TeleportToTop(getCommand("tptop"));
        //Teleport all players to sender
        new TeleportAll(getCommand("tpall"));
        //Information
        new InfoCommand(getCommand("info"));
        //RainCommand
        new RainCommand(getCommand("rain"));
        //Rule Command
        new RuleCommand(getCommand("rule"));
        //Time Command
        new TimeCommand(getCommand("time"));
        //Gamemode Command
        new GamemodeCommand(getCommand("gamemode"));
        //Alert Command
        new AlertCommand(getCommand("alert"));
        //SignEditListener
        //TODO: Make it switchable via command (somethink like /signedit <true|false>)
        pluginManager.registerEvents(new EditSignListener(), this);
        //CustomMsgCommand
        new MsgCommand(getCommand("msg"));




        //Success start message
        getLogger().info("The plugin has started successfully!");
    }
    // Plugin shutdown logic
    @Override
    public void onDisable() {

        //Stop message
        System.out.println("The plugin has stopped!");
    }

    /**
     * A message that prints  a logo of the plugin to the console
     */
    public void loadMessage() {
        ConsoleCommandSender console = instance.getServer().getConsoleSender();
        console.sendMessage(Component.newline()
                .append(Component.text(" ___   ^", SurfColors.AQUA))
                .append(Component.newline())
                .append(Component.text("|     / \\", SurfColors.AQUA))
                .append(Component.newline())
                .append(Component.text("|___  \\\\/", SurfColors.AQUA)
                        .append(Component.text("  SurfEssentials ", SurfColors.DARK_GREEN))
                        .append(Component.text("v0.0.1", SurfColors.DARK_AQUA)))
                .append(Component.newline())
                .append(Component.text("|     /\\\\", SurfColors.AQUA)
                        .append(Component.text("  Running on %s".formatted(instance.getServer().getName()))
                                .color(SurfColors.GRAY)))
                .append(Component.newline())
                .append(Component.text("|___  \\ /", SurfColors.AQUA))
                .append(Component.newline())
                .append(Component.text("       v", SurfColors.AQUA)));
    }
}
