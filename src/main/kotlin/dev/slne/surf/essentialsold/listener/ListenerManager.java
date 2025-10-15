package dev.slne.surf.essentialsold.listener;


import dev.slne.surf.essentialsold.listener.listeners.*;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

/**
 * A class that manages listeners.
 */
@RequiredArgsConstructor
public class ListenerManager {
    private final Plugin plugin;
    private final PluginManager pluginManager;


    /**
     * Constructs a new {@link ListenerManager}
     *
     * @param plugin the plugin
     */
    public ListenerManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.pluginManager = plugin.getServer().getPluginManager();
    }

    /**
     * Registers all listeners.
     */
    public void registerListeners() {
        pluginManager.registerEvents(new TeleportListener(), plugin);
        pluginManager.registerEvents(new InfinityListener(), plugin);
        pluginManager.registerEvents(new CommandRegisterListener(), plugin);
        pluginManager.registerEvents(new JoinListener(), plugin);
        pluginManager.registerEvents(new AdvancementListener(), plugin);
        // pluginManager.registerEvents(new DeathListener(), plugin);
        pluginManager.registerEvents(new FlyListener(), plugin);
    }

    /**
     * Unregisters all listeners from this plugin
     */
    public void unregisterListeners() {
        HandlerList.unregisterAll(this.plugin);
    }
}
