package dev.slne.surf.essentials.main.commands.general.other.troll.listener;

import dev.slne.surf.essentials.SurfEssentials;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class TrollListner {
    public static void register(){
        Plugin plugin = SurfEssentials.getInstance();
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        // Water troll Listener
        pluginManager.registerEvents(new WaterTrollListner(), plugin);
    }
}
