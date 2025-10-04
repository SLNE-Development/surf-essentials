package dev.slne.surf.essentials.listener

import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPortalEnterEvent

object WorldListener : Listener {
    @EventHandler
    fun onEntityPortalEnter(event: EntityPortalEnterEvent) {
        val entity = event.entity
        val portal = event.portalType

        (entity as? Player)?.let {
            if (it.hasPermission(EssentialsPermissionRegistry.WORLD_BYPASS)) {
                it.sendText {
                    appendPrefix()
                    success("")
                }
                return
            }
        }

        portal
    }
}