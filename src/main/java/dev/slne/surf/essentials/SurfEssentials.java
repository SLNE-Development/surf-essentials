package dev.slne.surf.essentials;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.brigadier.CheatTabComplete;
import dev.slne.surf.essentials.brigadier.GeneralTabComplete;
import dev.slne.surf.essentials.brigadier.TpTabComplete;
import dev.slne.surf.essentials.main.commands.BrigadierCommands;
import dev.slne.surf.essentials.main.commands.Commands;
import dev.slne.surf.essentials.main.commands.general.sign.EditSignListener;
import dev.slne.surf.essentials.main.utils.brigadier.CommandRegistered;
import dev.slne.surf.essentials.main.utils.brigadier.PluginBrigadierCommand;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

import static dev.slne.surf.essentials.main.utils.EssentialsUtil.gradientify;
import static net.kyori.adventure.text.Component.text;

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
        //onCommandRegistered
        pluginManager.registerEvents(new CommandRegistered(), this);

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

        //register brigadier commands
        BrigadierCommands.register();
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

public static ProtocolManager manager(){
        return ProtocolLibrary.getProtocolManager();
}

    /**
     * A message that prints  a logo of the plugin to the console
     */
    public void loadMessage() {
        ConsoleCommandSender console = instance.getServer().getConsoleSender();
        String version = "v" + getDescription().getVersion();
        console.sendMessage(Component.newline()
                .append(text("  _____ _____ ", SurfColors.AQUA))
                .append(Component.newline())
                .append(text("|  ___/  ___|", SurfColors.AQUA))
                .append(Component.newline())
                .append(text("| |__ \\ `--. ", SurfColors.AQUA))
                        .append(gradientify("  SurfEssentials ", "#009245", "#FCEE21"))
                        .append(gradientify(version, "#FC4A1A", "#F7B733"))
                .append(Component.newline())
                .append(text("|  __| `--. \\", SurfColors.AQUA)
                        .append(gradientify("  Running on %s ".formatted(instance.getServer().getName()), "#fdfcfb", "#e2d1c3")))
                        .append(gradientify(instance.getServer().getVersion(), "#93a5cf", "#e4efe9").decorate(TextDecoration.ITALIC))
                .append(Component.newline())
                .append(text("| |___/\\__/ /", SurfColors.AQUA))
                .append(Component.newline())
                .append(text("\\____/\\____/ ", SurfColors.AQUA)));
    }

    /**
     * Component Logger
     */
    public static ComponentLogger logger(){
        return SurfEssentials.getInstance().getComponentLogger();
    }

    public static PluginBrigadierCommand registerPluginBrigadierCommand(final String label, final Consumer<LiteralArgumentBuilder<CommandSourceStack>> command) {
        final PluginBrigadierCommand pluginBrigadierCommand = new PluginBrigadierCommand(SurfEssentials.getInstance(), label, command);
        SurfEssentials.getInstance().getServer().getCommandMap().register(SurfEssentials.getInstance().getName(), pluginBrigadierCommand);
        ((CraftServer) SurfEssentials.getInstance().getServer()).syncCommands();
        return pluginBrigadierCommand;
    }
}
