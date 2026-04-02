package dev.slne.surf.essentials.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent

object HungerListener : Listener {
    @EventHandler
    fun onHungerLose(event: FoodLevelChangeEvent) {
        val player = event.entity as? Player ?: return

        if (player.isInvulnerable) {
            event.isCancelled = true
        }
    }
}