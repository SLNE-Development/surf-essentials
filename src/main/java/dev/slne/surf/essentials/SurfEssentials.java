package dev.slne.surf.essentials;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.commands.BrigadierCommands;
import dev.slne.surf.essentials.commands.general.other.TimerCommand;
import dev.slne.surf.essentials.commands.general.other.troll.trolls.MlgTroll;
import dev.slne.surf.essentials.exceptions.UnsupportedServerVersionException;
import dev.slne.surf.essentials.listeners.ListenerManager;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.brigadier.PluginBrigadierCommand;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.PermissionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static dev.slne.surf.essentials.utils.EssentialsUtil.gradientify;
import static net.kyori.adventure.text.Component.text;

public final class SurfEssentials extends JavaPlugin{

    private static SurfEssentials instance;
    ListenerManager listeners;
    BrigadierCommands brigadierCommands;
    PermissionManager permissionManager;

    @Override
    public void onLoad() {
        listeners = new ListenerManager();
        brigadierCommands = new BrigadierCommands();
        permissionManager = new PermissionManager(this);
    }

    @Override
    public void onEnable() {
        instance = this;
        loadMessage();

        if (!EssentialsUtil.isNmsSupported()){
            getServer().getPluginManager().disablePlugin(instance);
            throw new UnsupportedServerVersionException("This Serverversion (" + getServer().getMinecraftVersion() +") is not supported by the plugin!");
        }

        permissionManager.initializePermissions();
        listeners.registerListeners(this);
        brigadierCommands.register();

        getLogger().info("The plugin has started successfully!");
    }

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

    /**
     * A message that prints  a logo of the plugin to the console
     */
    public void loadMessage() {
        ConsoleCommandSender console = instance.getServer().getConsoleSender();
        String version = "v" + getPluginMeta().getVersion();
        console.sendMessage(Component.newline()
                .append(text("  _____ _____ ", Colors.AQUA))
                .append(Component.newline())
                .append(text("|  ___/  ___|", Colors.AQUA))
                .append(Component.newline())
                .append(text("| |__ \\ `--. ", Colors.AQUA))
                        .append(gradientify("  SurfEssentials ", "#009245", "#FCEE21"))
                        .append(gradientify(version, "#FC4A1A", "#F7B733"))
                .append(Component.newline())
                .append(text("|  __| `--. \\", Colors.AQUA)
                        .append(gradientify("  Running on %s ".formatted(instance.getServer().getName()), "#fdfcfb", "#e2d1c3")))
                        .append(gradientify(instance.getServer().getVersion(), "#93a5cf", "#e4efe9").decorate(TextDecoration.ITALIC))
                .append(Component.newline())
                .append(text("| |___/\\__/ /", Colors.AQUA))
                .append(Component.newline())
                .append(text("\\____/\\____/ ", Colors.AQUA)));
    }

    /**
     * Component Logger
     */
    public static @NotNull ComponentLogger logger(){
        ComponentLogger bootStrapLogger = SurfEssentialsBootstrap.getLogger();
        if (bootStrapLogger != null){
            return bootStrapLogger;
        }
        return instance.getComponentLogger();
    }

    public static @NotNull PluginBrigadierCommand registerPluginBrigadierCommand(final String label, final Consumer<LiteralArgumentBuilder<CommandSourceStack>> command) {
        final PluginBrigadierCommand pluginBrigadierCommand = new PluginBrigadierCommand(instance, label, command);
        instance.getServer().getCommandMap().register(instance.getName(), pluginBrigadierCommand);
        ((CraftServer) instance.getServer()).syncCommands();
        return pluginBrigadierCommand;
    }
}
