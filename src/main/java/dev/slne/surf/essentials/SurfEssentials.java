package dev.slne.surf.essentials;

import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.commands.Commands;
import dev.slne.surf.essentials.commands.general.sign.EditSignListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SurfEssentials extends JavaPlugin implements Listener {

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
        Commands commands = new Commands();
        //Start message
        getLogger().info("The plugin is starting...");
        //logo if the plugin
        loadMessage();
        //Plugin Manager shortcut
        PluginManager pluginManager = Bukkit.getPluginManager();
        //SignEditListener
        //TODO: Make it switchable via command (somethink like /signedit <true|false>)
        pluginManager.registerEvents(new EditSignListener(), this);

        //Register Commands
        commands.initializeCheatCommands();
        commands.initializeGeneralCommands();
        commands.initializeTpCommands();




        //Success start message
        getLogger().info("The plugin has started successfully!");
    }

    // Plugin shutdown logic
    @Override
    public void onDisable() {
        instance = null;
        //Stop message
        getLogger().info("The plugin has stopped!");
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

    /**
     * Component Logger
     */
    public static ComponentLogger logger(){
        return SurfEssentials.getInstance().getComponentLogger();
    }

    /**
     *
     * Check if arg is int.
     *
     * @param s  the string to be checked for an int
     */
    public boolean isInt(String s) {
        int i;
        try {
            i = Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            //string is not an integer
            return false;
        }
    }

    public static Component NO_PERMISSION(){
        return Component.text("Dafür hast du keine Berechtigung!", SurfColors.ERROR);
    }

}
