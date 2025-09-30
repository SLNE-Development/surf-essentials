package dev.slne.surf.essentials.service

import dev.slne.surf.essentials.plugin
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class FlyService {
    private val pdcKey = NamespacedKey(plugin, "flying")

    fun setFlying(player: Player, flying: Boolean, save: Boolean) {
        player.allowFlight = flying
        player.isFlying = flying

        if (save) {
            player.persistentDataContainer.set(pdcKey, PersistentDataType.BOOLEAN, flying)
        }
    }

    fun canFly(player: Player) = player.allowFlight

    fun updateFlying(player: Player) {
        if (player.persistentDataContainer.getOrDefault(
                pdcKey,
                PersistentDataType.BOOLEAN,
                false
            )
        ) {
            setFlying(player, flying = true, save = false)
        }
    }

    companion object {
        val INSTANCE = FlyService()
    }
}

val flyService get() = FlyService.INSTANCE