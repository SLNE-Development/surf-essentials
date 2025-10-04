package dev.slne.surf.essentials.service

import dev.slne.surf.essentials.plugin
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.persistence.PersistentDataType

class WorldService {
    private val accessKey = NamespacedKey(plugin, "world_access")

    fun isLocked(world: World): Boolean =
        world.persistentDataContainer.getOrDefault(accessKey, PersistentDataType.BOOLEAN, false)

    fun lock(world: World) =
        world.persistentDataContainer.set(accessKey, PersistentDataType.BOOLEAN, true)

    fun unlock(world: World) =
        world.persistentDataContainer.set(accessKey, PersistentDataType.BOOLEAN, false)

    companion object {
        val INSTANCE = WorldService()
    }
}

val worldService get() = WorldService.INSTANCE