package dev.slne.surf.essentialsold;

import com.github.retrooper.packetevents.PacketEvents;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.slne.surf.essentialsold.commands.BrigadierCommands;
import dev.slne.surf.essentialsold.listener.ListenerManager;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.SafeLocationFinder;
import dev.slne.surf.essentialsold.utils.brigadier.RecodedCommands;
import dev.slne.surf.essentialsold.utils.color.Colors;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
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

import java.util.Objects;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.text;

public final class SurfEssentials extends JavaPlugin {

    @Getter
    private static SurfEssentials instance;
    private ListenerManager listeners;
    private RecodedCommands recodedCommands;
    private BrigadierCommands brigadierCommands;
    private static CoreProtectAPI CORE_PROTECT_API;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();

        CommandAPI.onLoad(new CommandAPIBukkitConfig(this)
                .initializeNBTAPI(Object.class, Function.identity())
                .shouldHookPaperReload(true));

        recodedCommands = new RecodedCommands();
        recodedCommands.unregisterVanillaCommands();
        brigadierCommands = new BrigadierCommands();
        listeners = new ListenerManager(this);

        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        instance = this;
        loadMessage();

        PacketEvents.getAPI().init();
        CommandAPI.onEnable();

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
        CommandAPI.onDisable();
        logger().info(text("The plugin has stopped!", Colors.INFO));
        instance = null;
    }


    public SurfEssentials() {
        if (SurfEssentials.instance != null) {
            throw new Error("Plugin already initialized!");
        }
        SurfEssentials.instance = this;
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
