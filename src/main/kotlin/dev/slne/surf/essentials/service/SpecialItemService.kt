package dev.slne.surf.essentials.service

import dev.slne.surf.essentials.plugin
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class SpecialItemService {
    private val specialKey = NamespacedKey(plugin, "special")
    private val specialAnnounced = NamespacedKey(plugin, "special_announced")
    private val specializedDate = NamespacedKey(plugin, "special_date")

    fun makeSpecial(item: ItemStack) = item.editPersistentDataContainer {
        it.set(specialKey, PersistentDataType.BOOLEAN, true)
    }

    fun setSpecializedDate(item: ItemStack, unix: Long) = item.editPersistentDataContainer {
        it.set(specializedDate, PersistentDataType.LONG, unix)
    }

    fun isSpecial(item: ItemStack): Boolean = item.itemMeta.persistentDataContainer.getOrDefault(
        specialKey,
        PersistentDataType.BOOLEAN, false
    )

    fun markAsAnnounced(item: ItemStack) = item.editPersistentDataContainer {
        it.set(specialAnnounced, PersistentDataType.BOOLEAN, true)
    }

    fun unMarkAsAnnounced(item: ItemStack) = item.editPersistentDataContainer {
        it.remove(specialAnnounced)
    }

    fun isAnnounced(item: ItemStack) = item.itemMeta.persistentDataContainer.has(
        specialAnnounced,
        PersistentDataType.BOOLEAN
    )

    companion object {
        val INSTANCE = SpecialItemService()
    }
}

val specialItemService get() = SpecialItemService.INSTANCE