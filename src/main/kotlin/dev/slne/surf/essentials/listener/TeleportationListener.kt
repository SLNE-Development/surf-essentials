package dev.slne.surf.essentials.listener

import dev.slne.surf.essentials.service.lastLocationService
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

object TeleportationListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.getPlayer()

        if (event.isCancelled) {
            return
        }

        if (event.cause != PlayerTeleportEvent.TeleportCause.COMMAND && event.cause != PlayerTeleportEvent.TeleportCause.PLUGIN) {
            return
        }

        lastLocationService.setLatestLocation(player.uniqueId, event.from)
    }
}