package dev.slne.surf.essentials.listeners;

import dev.slne.surf.essentials.SurfEssentials;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SurfEssentials.getMinecraftServer().getCommands().sendCommands(((CraftPlayer) event.getPlayer()).getHandle());
    }
}
