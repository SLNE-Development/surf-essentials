package dev.slne.surf.essentials;

import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class SurfEssentialsBootstrap implements PluginBootstrap {
    private static @Nullable ComponentLogger logger;
    private static final TextColor DEBUG = TextColor.fromHexString("#a6c7e6");

    @Override
    public void bootstrap(@NotNull PluginProviderContext context) {
        logger = context.getLogger();
        logger.debug(Component.text("Initialized logger", DEBUG));
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new SurfEssentials();
    }

    public static @Nullable ComponentLogger getLogger() {
        return logger;
    }
}
