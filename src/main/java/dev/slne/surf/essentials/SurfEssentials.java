package dev.slne.surf.essentials;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.brigadier.CheatTabComplete;
import dev.slne.surf.essentials.brigadier.GeneralTabComplete;
import dev.slne.surf.essentials.brigadier.TpTabComplete;
import dev.slne.surf.essentials.commands.Commands;
import dev.slne.surf.essentials.commands.general.sign.EditSignListener;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
        instance = this;
        Commands commands = new Commands();
        //Start message
        getLogger().info("The plugin is starting...");
        //logo if the plugin
        loadMessage();
        //Plugin Manager shortcut
        PluginManager pluginManager = Bukkit.getPluginManager();
        //SignEditListener
        //TODO: Make it switchable via command (something like /signedit <true|false>)
        pluginManager.registerEvents(new EditSignListener(), this);

        //Register Commands
        commands.initializeCheatCommands();
        commands.initializeGeneralCommands();
        commands.initializeTpCommands();

        /**
         * This section deals with brigadier TabCompletion that uses {@link Commodore}
         */
        //check if brigadier is supported
        if (!CommodoreProvider.isSupported()) {
            throw new IllegalStateException("Brigadier is not supported! Most commands will not work properly.");
        }
        // get a commodore instance
        Commodore commodore = CommodoreProvider.getCommodore(this);

        //Brigadier tab Complete for cheat commands
        new CheatTabComplete().register(commodore);
        //Brigadier tab Complete for general commands
        new GeneralTabComplete().register(commodore);
        //Brigadier tab Complete for tp commands
        new TpTabComplete().register(commodore);

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
        String version = "v" + getDescription().getVersion();
        console.sendMessage(Component.newline()
                .append(Component.text("  _____ _____ ", SurfColors.AQUA))
                .append(Component.newline())
                .append(Component.text("|  ___/  ___|", SurfColors.AQUA))
                .append(Component.newline())
                .append(Component.text("| |__ \\ `--. ", SurfColors.AQUA))
                        .append(gradientify("  SurfEssentials ", "#009245", "#FCEE21"))
                        .append(gradientify(version, "#FC4A1A", "#F7B733"))
                .append(Component.newline())
                .append(Component.text("|  __| `--. \\", SurfColors.AQUA)
                        .append(gradientify("  Running on %s ".formatted(instance.getServer().getName()), "#fdfcfb", "#e2d1c3")))
                        .append(gradientify(instance.getServer().getVersion(), "#93a5cf", "#e4efe9").decorate(TextDecoration.ITALIC))
                .append(Component.newline())
                .append(Component.text("| |___/\\__/ /", SurfColors.AQUA))
                .append(Component.newline())
                .append(Component.text("\\____/\\____/ ", SurfColors.AQUA)));
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
    public static boolean isInt(String s) {
        int i;
        try {
            i = Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            //string is not an integer
            return false;
        }
    }

    /**
     *
     * Simple "No permission" message.
     *
     */
    public static Component NO_PERMISSION(){
        return SurfApi.getPrefix()
                .append(Component.text("You do not have permission to execute this command!", SurfColors.ERROR));
    }

    /**
     *
     * Converts the color from the input string to a gradient.
     *
     * @param input  the string to convert the color from
     * @param firstHex  the first hex color
     * @param secondHex  the second hex color
     */
    public static Component gradientify(String input, String firstHex, String secondHex) {

        TextColor gradientFirst = TextColor.fromHexString(firstHex);
        TextColor gradientSecond = TextColor.fromHexString(secondHex);

        if (gradientFirst == null || gradientSecond == null) {
            return Component.text(input);
        }

        TextComponent.Builder builder = Component.text();
        float step = 1.0f / (input.length() - 1);
        float current = 0.0f;
        for (char c : input.toCharArray()) {
            builder.append(Component.text(c, TextColor.lerp(current, gradientFirst, gradientSecond)));
            current += step;
        }

        return builder.build();
    }

    /**
     *
     * converts ticks in a time format.
     *
     * @param ticks  the ticks to convert
     * @return Time format in string
     */
    public static String ticksToString(int ticks){
        int totalSeconds = ticks/20;
        int hours, minutes, seconds;
        hours = totalSeconds / 3600;
        minutes = (totalSeconds % 3600) / 60;
        seconds = totalSeconds % 60;
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }

}
