package dev.slne.surf.essentials.listener

import dev.slne.surf.essentials.service.worldService
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.player.PlayerTeleportEvent

object WorldListener : Listener {
    @EventHandler
    fun onWorldChanged(event: PlayerTeleportEvent) {
        val player = event.player

        if (event.player.hasPermission(EssentialsPermissionRegistry.WORLD_BYPASS)) {
            return
        }

        if (event.from.world == event.to.world) {
            return
        }

        if (worldService.isLocked(event.to.world)) {
            event.isCancelled = true

            val reason = when (event.to.world.environment) {
                World.Environment.NORMAL -> "Die Oberwelt ist aktuell gesperrt."
                World.Environment.NETHER -> "Der Nether ist aktuell gesperrt."
                World.Environment.THE_END -> "Das Ende ist aktuell gesperrt."
                else -> "Diese Welt ist aktuell gesperrt."
            }

            player.sendText {
                appendPrefix()
                error(reason)
            }
        }
    }

    @EventHandler
    fun onPortalUse(event: PlayerPortalEvent) {
        if (event.player.hasPermission(EssentialsPermissionRegistry.WORLD_BYPASS)) {
            return
        }

        if (worldService.isLocked(event.to.world)) {
            event.isCancelled = true

            val reason = when (event.to.world.environment) {
                World.Environment.NORMAL -> "Die Oberwelt ist aktuell gesperrt."
                World.Environment.NETHER -> "Der Nether ist aktuell gesperrt."
                World.Environment.THE_END -> "Das Ende ist aktuell gesperrt."
                else -> "Diese Welt ist aktuell gesperrt."
            }

            event.player.sendText {
                appendPrefix()
                error(reason)
            }
        }
    }
}