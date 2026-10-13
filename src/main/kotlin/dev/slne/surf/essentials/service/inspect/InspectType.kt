package dev.slne.surf.essentials.service.inspect

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.essentials.plugin
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

enum class InspectType(
    val dataSize: Int,
    val viewSize: Int,
    val displayName: String,
    val article: String,
) {
    INVENTORY(41, 54, "Inventar", "das") {
        override fun dataSlotOf(viewSlot: Int) = when (viewSlot) {
            in 0..26 -> viewSlot + 9
            in 27..35 -> viewSlot - 27
            HELMET_VIEW_SLOT -> 39
            HELMET_VIEW_SLOT + 1 -> 38
            HELMET_VIEW_SLOT + 2 -> 37
            HELMET_VIEW_SLOT + 3 -> 36
            HELMET_VIEW_SLOT + 4 -> 40
            else -> null
        }

        override fun decorationOf(viewSlot: Int): ItemStack? {
            if (dataSlotOf(viewSlot) != null) return null
            val label = EQUIPMENT_LABELS.getOrNull(viewSlot - LABEL_VIEW_SLOT)
                ?: return filler(Material.GRAY_STAINED_GLASS_PANE, Component.empty())

            return filler(Material.BLACK_STAINED_GLASS_PANE, buildText { info("$label ↓") })
        }

        override fun dataInventoryOf(player: Player): Inventory = player.inventory
    },

    ENDER_CHEST(27, 27, "Endertruhe", "die") {
        override fun dataSlotOf(viewSlot: Int) = viewSlot.takeIf { it in 0 until dataSize }
        override fun decorationOf(viewSlot: Int): ItemStack? = null
        override fun dataInventoryOf(player: Player): Inventory = player.enderChest
    };

    private val viewSlots by lazy {
        IntArray(dataSize).also { slots ->
            for (viewSlot in 0 until viewSize) {
                dataSlotOf(viewSlot)?.let { slots[it] = viewSlot }
            }
        }
    }

    abstract fun dataSlotOf(viewSlot: Int): Int?

    fun viewSlotOf(dataSlot: Int) = viewSlots[dataSlot]

    abstract fun decorationOf(viewSlot: Int): ItemStack?

    abstract fun dataInventoryOf(player: Player): Inventory

    companion object {
        private const val LABEL_VIEW_SLOT = 36
        private const val HELMET_VIEW_SLOT = 45
        private val EQUIPMENT_LABELS = listOf("Helm", "Brustplatte", "Hose", "Schuhe", "Zweithand")

        private val decorationKey = NamespacedKey(plugin, "inspect_decoration")

        private fun filler(material: Material, name: Component) = ItemStack.of(material).apply {
            editMeta {
                it.displayName(name)
                it.persistentDataContainer.set(decorationKey, PersistentDataType.BOOLEAN, true)
            }
        }
    }
}
