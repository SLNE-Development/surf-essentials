package dev.slne.surf.essentials.listener

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.essentials.service.inspect.InspectService
import dev.slne.surf.essentials.service.inspect.InspectSession
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

object InspectListener : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onClick(event: InventoryClickEvent) {
        val session = event.view.topInventory.holder as? InspectSession ?: return

        if (!session.isEditable()) {
            event.isCancelled = true
            return
        }

        if (event.clickedInventory == event.view.topInventory && session.type.dataSlotOf(event.slot) == null) {
            event.isCancelled = true
            return
        }

        session.beforeInteraction()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onDrag(event: InventoryDragEvent) {
        val session = event.view.topInventory.holder as? InspectSession ?: return
        val topSize = event.view.topInventory.size

        if (!session.isEditable() || event.rawSlots.any { it < topSize && session.type.dataSlotOf(it) == null }) {
            event.isCancelled = true
            return
        }

        session.beforeInteraction()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onClose(event: InventoryCloseEvent) {
        val session = event.inventory.holder as? InspectSession ?: return
        if (session.viewer != event.player) return

        session.onClose()
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPreLogin(event: AsyncPlayerPreLoginEvent) {
        if (event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) return
        if (InspectService.prepareJoin(event.uniqueId)) return

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, buildText {
            error("Dein Inventar wird gerade bearbeitet. Bitte versuche es gleich erneut.")
        })
    }
}
