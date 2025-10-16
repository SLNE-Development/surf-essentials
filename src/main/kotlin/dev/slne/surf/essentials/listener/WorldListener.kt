package dev.slne.surf.essentials.listener

import dev.slne.surf.essentials.service.worldService
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.bukkit.api.event.cancel
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPortalEvent

object WorldListener : Listener {
    @EventHandler
    fun onPortal(event: PlayerPortalEvent) {
        val player = event.player
        val world = event.to.world

        if (worldService.isLocked(world)) {
            if (!player.hasPermission(EssentialsPermissionRegistry.WORLD_BYPASS)) {
                event.cancel()
                player.sendText {
                    appendPrefix()

                    when (world.environment) {
                        World.Environment.NETHER -> error("Der Nether ist zurzeit deaktiviert.")
                        World.Environment.THE_END -> error("Das End ist zurzeit deaktiviert.")
                        else -> error("Du kannst dieses Portal nicht benutzen!")
                    }
                }
            } else {
                player.sendText {
                    appendPrefix()
                    success("Du hast die Portal-Sperre umgangen.")
                }
            }
        }
    }
}