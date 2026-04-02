package dev.slne.surf.essentials.listener

import dev.slne.surf.essentials.service.worldService
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.bukkit.api.event.cancel
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPortalEvent

object WorldListener : Listener {
    @EventHandler
    fun onPortal(event: EntityPortalEvent) {
        val world = event.to?.world ?: return

        if (!worldService.isLocked(world)) {
            return
        }

        val player = event.entity as? Player ?: run {
            event.cancel()
            return
        }

        if (!player.hasPermission(EssentialsPermissionRegistry.WORLD_BYPASS)) {
            event.cancel()
            player.sendText {
                appendErrorPrefix()

                when (world.environment) {
                    World.Environment.NETHER -> error("Der Nether ist zurzeit deaktiviert.")
                    World.Environment.THE_END -> error("Das End ist zurzeit deaktiviert.")
                    else -> error("Du kannst dieses Portal nicht benutzen!")
                }
            }
        } else {
            player.sendText {
                appendSuccessPrefix()
                success("Du hast die Portal-Sperre umgangen.")
            }
        }
    }
}