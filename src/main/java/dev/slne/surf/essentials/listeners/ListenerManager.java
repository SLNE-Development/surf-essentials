package dev.slne.surf.essentials.listeners;

import dev.slne.surf.essentials.commands.general.other.troll.listener.CageTrollListener;
import dev.slne.surf.essentials.commands.general.other.troll.listener.MlgTrollListener;
import dev.slne.surf.essentials.commands.general.other.troll.listener.WaterTrollListener;
import dev.slne.surf.essentials.commands.general.other.troll.trolls.AnvilTroll;
import dev.slne.surf.essentials.commands.general.sign.EditSignListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class ListenerManager {
    public void registerListeners(Plugin plugin){
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        pluginManager.registerEvents(new TeleportListener(), plugin);
        pluginManager.registerEvents(new EditSignListener(), plugin);
        pluginManager.registerEvents(new InfinityListener(), plugin);
        pluginManager.registerEvents(new WaterTrollListener(), plugin);
        pluginManager.registerEvents(new MlgTrollListener(), plugin);
        pluginManager.registerEvents(new CageTrollListener(), plugin);
        pluginManager.registerEvents(new AnvilTroll(), plugin);
        pluginManager.registerEvents(new CommandRegisterListener(), plugin);
    }
}
