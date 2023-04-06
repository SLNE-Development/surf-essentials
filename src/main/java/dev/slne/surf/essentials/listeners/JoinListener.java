package dev.slne.surf.essentials.listeners;

import dev.slne.surf.essentials.utils.EssentialsUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        EssentialsUtil.sendDebug("Sending commands to " + event.getPlayer().getName() + "...");
        EssentialsUtil.sendCommands(event.getPlayer());
    }
}
