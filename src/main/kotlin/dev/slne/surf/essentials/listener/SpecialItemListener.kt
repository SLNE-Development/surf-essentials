package dev.slne.surf.essentials.listener

import dev.slne.surf.essentials.service.specialItemService
import dev.slne.surf.essentials.util.util.translatable
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent

object SpecialItemListener : Listener {
    @EventHandler
    fun onPickup(event: EntityPickupItemEvent) {
        val player = event.entity as? Player ?: return
        val itemStack = event.item.itemStack

        if (!specialItemService.isSpecial(itemStack)) {
            return
        }

        if (specialItemService.isAnnounced(itemStack)) {
            return
        }

        specialItemService.markAsAnnounced(itemStack)

        forEachPlayer {
            it.sendText {
                appendPrefix()
                variableValue(player.name)
                success(" hat ")
                translatable(itemStack.translationKey()).colorIfAbsent(Colors.VARIABLE_VALUE)
                success(" erhalten!")
            }
        }
    }
}