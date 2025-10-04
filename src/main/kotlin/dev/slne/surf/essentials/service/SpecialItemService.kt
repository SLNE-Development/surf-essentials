package dev.slne.surf.essentials.service

import dev.slne.surf.essentials.plugin
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class SpecialItemService {
    private val specialKey = NamespacedKey(plugin, "special")
    private val specialAnnounced = NamespacedKey(plugin, "special_announced")

    fun makeSpecial(item: ItemStack) = item.itemMeta.persistentDataContainer.set(
        specialKey,
        PersistentDataType.BOOLEAN, true
    )

    fun isSpecial(item: ItemStack): Boolean = item.itemMeta.persistentDataContainer.getOrDefault(
        specialKey,
        PersistentDataType.BOOLEAN, false
    )

    fun markAsAnnounced(item: ItemStack) = item.itemMeta.persistentDataContainer.set(
        specialAnnounced,
        PersistentDataType.BOOLEAN, true
    )

    fun isAnnounced(item: ItemStack) = item.itemMeta.persistentDataContainer.has(
        specialAnnounced,
        PersistentDataType.BOOLEAN
    )

    companion object {
        val INSTANCE = SpecialItemService()
    }
}

val specialItemService get() = SpecialItemService.INSTANCE