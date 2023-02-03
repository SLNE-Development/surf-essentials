package dev.slne.surf.essentials;

import aetherial.spigot.plugin.annotation.plugin.*;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.brigadier.GeneralTabComplete;
import dev.slne.surf.essentials.brigadier.TpTabComplete;
import dev.slne.surf.essentials.main.commands.BrigadierCommands;
import dev.slne.surf.essentials.main.commands.Commands;
import dev.slne.surf.essentials.main.commands.general.other.TimerCommand;
import dev.slne.surf.essentials.main.commands.general.other.troll.trolls.MlgTroll;
import dev.slne.surf.essentials.main.exceptions.UnsupportedServerVersionException;
import dev.slne.surf.essentials.main.listeners.ListenerManager;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import dev.slne.surf.essentials.main.utils.brigadier.PluginBrigadierCommand;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static dev.slne.surf.essentials.main.utils.EssentialsUtil.gradientify;
import static net.kyori.adventure.text.Component.text;

@Plugin(name = "SurfEssentials", version = "1.0-SNAPSHOT")
@ApiVersion("1.19")
@Depend({"SurfAPI", "ProtocolLib"})
@LoadBefore({"SurfAPI", "ProtocolLib"})
@Authors({"Twisti_twixi", "SLNE Dev Team"})
@Load(Load.LoadType.POST_WORLD)
@Website("https://git.slne.dev/surf/surf-essentials")
public final class SurfEssentials extends JavaPlugin implements Listener {

    private static SurfEssentials instance;
    Commands commands;
    ListenerManager listeners;
    Commodore commodore;
    BrigadierCommands brigadierCommands;

    @Override
    public void onLoad() {
        commands = new Commands();
        listeners = new ListenerManager();
        brigadierCommands = new BrigadierCommands();
    }

    // Plugin startup logic
    @Override
    public void onEnable() {
        instance = this;
        loadMessage();

        if (!getServer().getMinecraftVersion().equals("1.19.3")){
            getServer().getPluginManager().disablePlugin(instance);
            throw new UnsupportedServerVersionException("This Serverversion (" + getServer().getMinecraftVersion() +") is not supported by the plugin!");
        }

        if (!EssentialsUtil.isBrigadierSupported()) {
            getServer().getPluginManager().disablePlugin(instance);
            return;
        }

        listeners.registerListeners(this);
        commands.initializeGeneralCommands();
        commands.initializeTpCommands();
        commodore = CommodoreProvider.getCommodore(this);

        new GeneralTabComplete().register(commodore);
        new TpTabComplete().register(commodore);

        brigadierCommands.register();


        getLogger().info("The plugin has started successfully!");
    }

    // Plugin shutdown logic
    @Override
    public void onDisable() {
        instance = null;
        MlgTroll.restoreInventoryFromMlgTroll();
        TimerCommand.removeRemainingBossbars();
        getLogger().info("The plugin has stopped!");
    }


    public SurfEssentials() {
        if (SurfEssentials.instance != null) {
            throw new Error("Plugin already initialized!");
        }
        SurfEssentials.instance = this;
    }

    public static SurfEssentials getInstance() {
        return instance;
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
    public static @NotNull ComponentLogger logger(){
        return instance.getComponentLogger();
    }

    public static @NotNull PluginBrigadierCommand registerPluginBrigadierCommand(final String label, final Consumer<LiteralArgumentBuilder<CommandSourceStack>> command) {
        final PluginBrigadierCommand pluginBrigadierCommand = new PluginBrigadierCommand(instance, label, command);
        instance.getServer().getCommandMap().register(instance.getName(), pluginBrigadierCommand);
        ((CraftServer) instance.getServer()).syncCommands();
        return pluginBrigadierCommand;
    }

}
