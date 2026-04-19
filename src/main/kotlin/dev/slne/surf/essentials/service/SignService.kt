package dev.slne.surf.essentials.service

import dev.slne.surf.essentials.plugin
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object SignService {
    val keySignedBy = NamespacedKey(plugin, "signed_by")
    val keySignedText = NamespacedKey(plugin, "signed_text")
    val keySignedAt = NamespacedKey(plugin, "signed_at")

    fun sign(item: ItemStack, signerName: String, text: String?) {
        item.editPersistentDataContainer {
            it.set(keySignedBy, PersistentDataType.STRING, signerName)
            it.set(keySignedAt, PersistentDataType.LONG, System.currentTimeMillis())
            if (text != null) {
                it.set(keySignedText, PersistentDataType.STRING, text)
            }
        }
    }
}

