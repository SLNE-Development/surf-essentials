package dev.slne.surf.essentials.listener

import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.service.flyService
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import java.util.concurrent.TimeUnit

object FlyCorrectionListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        flyService.updateFlying(event.getPlayer())
    }

    @EventHandler
    fun onPlayerGameModeChange(event: PlayerGameModeChangeEvent) {
        Bukkit.getAsyncScheduler().runDelayed(
            plugin,
            { flyService.updateFlying(event.getPlayer()) },
            5L, TimeUnit.MILLISECONDS
        )
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        Bukkit.getAsyncScheduler().runDelayed(
            plugin,
            { flyService.updateFlying(event.getPlayer()) },
            5L, TimeUnit.MILLISECONDS
        )
    }

    @EventHandler
    fun onPlayerChangedWorld(event: PlayerChangedWorldEvent) {
        Bukkit.getAsyncScheduler().runDelayed(
            plugin,
            { flyService.updateFlying(event.getPlayer()) },
            5L, TimeUnit.MILLISECONDS
        )
    }
}