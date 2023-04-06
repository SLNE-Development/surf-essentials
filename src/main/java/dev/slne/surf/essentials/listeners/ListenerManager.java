package dev.slne.surf.essentials.listeners;

import dev.slne.surf.essentials.commands.general.other.troll.listener.CageTrollListener;
import dev.slne.surf.essentials.commands.general.other.troll.listener.MlgTrollListener;
import dev.slne.surf.essentials.commands.general.other.troll.listener.WaterTrollListener;
import dev.slne.surf.essentials.commands.general.other.troll.trolls.AnvilTroll;
import dev.slne.surf.essentials.commands.general.sign.EditSignListener;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class ListenerManager {
    private final Plugin plugin;

    public ListenerManager(Plugin plugin){
        this.plugin = plugin;
    }
    public void registerListeners(){
        PluginManager pluginManager = this.plugin.getServer().getPluginManager();

        pluginManager.registerEvents(new TeleportListener(), plugin);
        pluginManager.registerEvents(new EditSignListener(), plugin);
        pluginManager.registerEvents(new InfinityListener(), plugin);
        pluginManager.registerEvents(new WaterTrollListener(), plugin);
        pluginManager.registerEvents(new MlgTrollListener(), plugin);
        pluginManager.registerEvents(new CageTrollListener(), plugin);
        pluginManager.registerEvents(new AnvilTroll(), plugin);
        pluginManager.registerEvents(new CommandRegisterListener(), plugin);
        pluginManager.registerEvents(new JoinListener(), plugin);
    }

    public void unregisterListeners(){
        HandlerList.unregisterAll(this.plugin);
    }
}
