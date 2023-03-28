package dev.slne.surf.essentials;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.commands.BrigadierCommands;
import dev.slne.surf.essentials.commands.general.other.TimerCommand;
import dev.slne.surf.essentials.commands.general.other.troll.trolls.MlgTroll;
import dev.slne.surf.essentials.exceptions.UnsupportedServerVersionException;
import dev.slne.surf.essentials.listeners.ListenerManager;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.brigadier.RecodedCommands;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.PermissionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

import static dev.slne.surf.essentials.utils.EssentialsUtil.gradientify;
import static net.kyori.adventure.text.Component.text;

public final class SurfEssentials extends JavaPlugin{

    private static SurfEssentials instance;
    private ListenerManager listeners;
    private RecodedCommands recodedCommands;
    private BrigadierCommands brigadierCommands;
    private PermissionManager permissionManager;
    private static MinecraftServer minecraftServer;

    @Override
    public void onLoad() {
        listeners = new ListenerManager(this);
        recodedCommands = new RecodedCommands();
        brigadierCommands = new BrigadierCommands();
        permissionManager = new PermissionManager(this);
        minecraftServer = MinecraftServer.getServer();
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        instance = this;
        loadMessage();

        if (!EssentialsUtil.isNmsSupported()){
            getServer().getPluginManager().disablePlugin(instance);
            throw new UnsupportedServerVersionException("This Serverversion (" + getServer().getMinecraftVersion() +") is not supported by the plugin!");
        }

        EssentialsUtil.setPrefix();
        permissionManager.initializePermissions();
        listeners.registerListeners();
        recodedCommands.unregisterVanillaCommands();
        brigadierCommands.register();

        logger().info(text("The plugin has started successfully!", Colors.INFO));

        getServer().getScheduler().runTask(this, () -> {
            logger().info(text("Running delayed tasks...", Colors.INFO));
            EssentialsUtil.syncCommands();
        });
    }

    @Override
    public void onDisable() {
        MlgTroll.restoreInventoryFromMlgTroll();
        TimerCommand.removeRemainingBossbars();
        listeners.unregisterListeners();
        brigadierCommands.unregister();
        recodedCommands.addVanillaCommands();
        logger().info(text("The plugin has stopped!", Colors.INFO));
        instance = null;
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
    @SuppressWarnings("UnstableApiUsage")
    public void loadMessage() {
        ConsoleCommandSender console = instance.getServer().getConsoleSender();
        String version = "v" + Objects.requireNonNull(getPluginMeta()).getVersion();
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
        var bootStrapLogger = SurfEssentialsBootstrap.getLogger();
        if (bootStrapLogger != null){
            return bootStrapLogger;
        }
        return instance.getComponentLogger();
    }

    public static MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }

    public static void registerPluginBrigadierCommand(final String label, final Consumer<LiteralArgumentBuilder<CommandSourceStack>> command) {
        EssentialsUtil.sendDebug("Registering command: " + label);
        var builder = LiteralArgumentBuilder.<CommandSourceStack>literal(label);
        command.accept(builder);
        EssentialsUtil.registerCommand(builder.build());
    }
}
