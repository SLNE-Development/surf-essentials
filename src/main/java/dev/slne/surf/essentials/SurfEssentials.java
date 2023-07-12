package dev.slne.surf.essentials;

import com.github.retrooper.packetevents.PacketEvents;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.commands.BrigadierCommands;
import dev.slne.surf.essentials.listener.ListenerManager;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.SafeLocationFinder;
import dev.slne.surf.essentials.utils.Validate;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.brigadier.RecodedCommands;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;

public final class SurfEssentials extends JavaPlugin {

    private static SurfEssentials instance;
    private ListenerManager listeners;
    private RecodedCommands recodedCommands;
    private BrigadierCommands brigadierCommands;
    private static CoreProtectAPI CORE_PROTECT_API;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();

        recodedCommands = new RecodedCommands();
        recodedCommands.unregisterVanillaCommands();
        brigadierCommands = new BrigadierCommands();
        listeners = new ListenerManager(this);

        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        instance = this;
        PacketEvents.getAPI().init();
        try {
            loadMessage();
        } catch (CommandSyntaxException e) {
            logger().error(text("Failed to display load message!", Colors.ERROR));
            e.printStackTrace();
        }

        EssentialsUtil.setPrefix();
        System.err.println("Commands");
        brigadierCommands.register();
        System.err.println("Listeners");
        listeners.registerListeners();

        CORE_PROTECT_API = getCoreProtectAPI_Internal();

        logger().info(text("The plugin has started successfully!", Colors.INFO));

        getServer().getScheduler().runTask(this, () -> {
            logger().info(text("Running delayed tasks...", Colors.INFO));
            //noinspection ResultOfMethodCallIgnored
            SafeLocationFinder.getSaveMaterials();
        });
    }

    @Override
    public void onDisable() {
        brigadierCommands.unregister();
        listeners.unregisterListeners();
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
    public void loadMessage() throws CommandSyntaxException {
        ConsoleCommandSender console = instance.getServer().getConsoleSender();
        String version = "v" + Validate.notNull(getPluginMeta()).getVersion();
        console.sendMessage(Component.newline()
                .append(text("  _____ _____ ", Colors.AQUA))
                .append(Component.newline())
                .append(text("|  ___/  ___|", Colors.AQUA))
                .append(Component.newline())
                .append(text("| |__ \\ `--. ", Colors.AQUA))
                .append(EssentialsUtil.gradientify("  SurfEssentials ", "#009245", "#FCEE21"))
                .append(EssentialsUtil.gradientify(version, "#FC4A1A", "#F7B733"))
                .append(Component.newline())
                .append(text("|  __| `--. \\", Colors.AQUA)
                        .append(EssentialsUtil.gradientify("  Running on %s ".formatted(instance.getServer().getName()), "#fdfcfb", "#e2d1c3")))
                .append(EssentialsUtil.gradientify(instance.getServer().getVersion(), "#93a5cf", "#e4efe9").decorate(TextDecoration.ITALIC))
                .append(Component.newline())
                .append(text("| |___/\\__/ /", Colors.AQUA))
                .append(Component.newline())
                .append(text("\\____/\\____/ ", Colors.AQUA)));
    }

    /**
     * Component Logger
     */
    public static @NotNull ComponentLogger logger() {
        var bootStrapLogger = SurfEssentialsBootstrap.getLogger();
        if (bootStrapLogger != null) {
            return bootStrapLogger;
        }
        return instance.getComponentLogger();
    }

    public static CoreProtectAPI getCoreProtectApi() {
        return CORE_PROTECT_API;
    }


    private @Nullable CoreProtectAPI getCoreProtectAPI_Internal() {
        final var plugin = Bukkit.getPluginManager().getPlugin("CoreProtect");

        if (!(plugin instanceof CoreProtect coreProtect)) return null;
        if (!coreProtect.isEnabled()) return null;
        if (coreProtect.getAPI().APIVersion() < 9) return null;

        return coreProtect.getAPI();
    }
}
