package dev.slne.surf.essentialsold.listener.listeners;

import dev.slne.surf.essentialsold.SurfEssentials;
import dev.slne.surf.essentialsold.commands.cheat.FlyCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.persistence.PersistentDataType;

public class FlyListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateFlyMode(event.getPlayer());
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Bukkit.getScheduler().runTaskLater(SurfEssentials.getInstance(), () -> updateFlyMode(event.getPlayer()), 5L);
    } // TODO

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(SurfEssentials.getInstance(), () -> updateFlyMode(event.getPlayer()), 5L);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Bukkit.getScheduler().runTaskLater(SurfEssentials.getInstance(), () -> updateFlyMode(event.getPlayer()), 5L);
    }

    private void updateFlyMode(Player player) {
        if (player.getPersistentDataContainer().getOrDefault(FlyCommand.PDC_IN_FLY_MODE, PersistentDataType.BOOLEAN, false)) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }
}
