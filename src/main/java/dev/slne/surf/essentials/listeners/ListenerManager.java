package dev.slne.surf.essentials.listeners;

import dev.slne.surf.essentials.commands.general.sign.EditSignListener;
import dev.slne.surf.essentials.utils.brigadier.CommandRegistered;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class ListenerManager {
    public void registerListeners(Plugin plugin){
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        pluginManager.registerEvents(new TeleportListener(), plugin);
        pluginManager.registerEvents(new EditSignListener(), plugin);
        pluginManager.registerEvents(new CommandRegistered(), plugin);
        pluginManager.registerEvents(new InfinityListener(), plugin);
    }
}
