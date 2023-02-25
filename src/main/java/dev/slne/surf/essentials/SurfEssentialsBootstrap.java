package dev.slne.surf.essentials;

import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SurfEssentialsBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull PluginProviderContext context) {

    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new SurfEssentials();
    }
}
