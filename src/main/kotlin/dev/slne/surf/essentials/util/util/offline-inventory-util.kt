package dev.slne.surf.essentials.util.util

import dev.slne.surf.essentials.service.inspect.InspectType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.nbt.BinaryTagIO
import net.kyori.adventure.nbt.BinaryTagIO.Compression.GZIP
import net.kyori.adventure.nbt.BinaryTagTypes
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.ListBinaryTag
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*

private const val INVENTORY_TAG = "Inventory"
private const val ENDER_ITEMS_TAG = "EnderItems"
private const val EQUIPMENT_TAG = "equipment"
private const val DATA_VERSION_TAG = "DataVersion"
private const val SLOT_TAG = "Slot"

private val EQUIPMENT_SLOTS = mapOf(
    "feet" to 36,
    "legs" to 37,
    "chest" to 38,
    "head" to 39,
    "offhand" to 40,
)

private const val MAIN_INVENTORY_SIZE = 36

class OfflineInventoryData(val items: Array<ItemStack?>, val upToDate: Boolean)

suspend fun loadOfflineInventory(uuid: UUID, type: InspectType): OfflineInventoryData? =
    withContext(Dispatchers.IO) {
        val file = getPlayerFile(uuid) ?: return@withContext null
        val root = BinaryTagIO.unlimitedReader().read(file.toPath(), GZIP)
        val dataVersion = root.getInt(DATA_VERSION_TAG)
        val items = arrayOfNulls<ItemStack>(type.dataSize)

        when (type) {
            InspectType.INVENTORY -> {
                root.getList(INVENTORY_TAG, BinaryTagTypes.COMPOUND).forEach { entry ->
                    val tag = entry as CompoundBinaryTag
                    val slot = tag.getByte(SLOT_TAG).toInt()
                    if (slot in 0 until MAIN_INVENTORY_SIZE) {
                        items[slot] = tag.remove(SLOT_TAG).toItemStack(dataVersion)
                    }
                }

                val equipment = root.getCompound(EQUIPMENT_TAG)
                for ((key, slot) in EQUIPMENT_SLOTS) {
                    val tag = equipment.get(key) as? CompoundBinaryTag ?: continue
                    items[slot] = tag.toItemStack(dataVersion)
                }
            }

            InspectType.ENDER_CHEST -> {
                root.getList(ENDER_ITEMS_TAG, BinaryTagTypes.COMPOUND).forEach { entry ->
                    val tag = entry as CompoundBinaryTag
                    val slot = tag.getByte(SLOT_TAG).toInt()
                    if (slot in 0 until type.dataSize) {
                        items[slot] = tag.remove(SLOT_TAG).toItemStack(dataVersion)
                    }
                }
            }
        }

        OfflineInventoryData(items, dataVersion == currentDataVersion())
    }

suspend fun saveOfflineInventory(uuid: UUID, type: InspectType, items: Array<ItemStack?>) =
    withContext(Dispatchers.IO) {
        val file = getPlayerFile(uuid) ?: error("Player data file of $uuid does not exist")
        val root = BinaryTagIO.unlimitedReader().read(file.toPath(), GZIP)

        val updated = when (type) {
            InspectType.INVENTORY -> {
                val inventory = ListBinaryTag.builder(BinaryTagTypes.COMPOUND)
                root.getList(INVENTORY_TAG, BinaryTagTypes.COMPOUND).forEach { entry ->
                    val slot = (entry as CompoundBinaryTag).getByte(SLOT_TAG).toInt()
                    if (slot !in 0 until MAIN_INVENTORY_SIZE) inventory.add(entry)
                }
                for (slot in 0 until MAIN_INVENTORY_SIZE) {
                    val item = items[slot]?.takeUnless { it.isEmpty } ?: continue
                    inventory.add(item.toTag().putByte(SLOT_TAG, slot.toByte()))
                }

                var equipment = root.getCompound(EQUIPMENT_TAG)
                for ((key, slot) in EQUIPMENT_SLOTS) {
                    val item = items[slot]?.takeUnless { it.isEmpty }
                    equipment = if (item == null) equipment.remove(key) else equipment.put(key, item.toTag())
                }

                val withInventory = root.put(INVENTORY_TAG, inventory.build())
                if (equipment.size() == 0) {
                    withInventory.remove(EQUIPMENT_TAG)
                } else {
                    withInventory.put(EQUIPMENT_TAG, equipment)
                }
            }

            InspectType.ENDER_CHEST -> {
                val enderItems = ListBinaryTag.builder(BinaryTagTypes.COMPOUND)
                items.forEachIndexed { slot, item ->
                    if (item == null || item.isEmpty) return@forEachIndexed
                    enderItems.add(item.toTag().putByte(SLOT_TAG, slot.toByte()))
                }
                root.put(ENDER_ITEMS_TAG, enderItems.build())
            }
        }

        val path = file.toPath()
        val tempFile = Files.createTempFile(path.parent, "${uuid}-", ".dat_tmp")
        try {
            BinaryTagIO.writer().write(updated, tempFile, GZIP)
            Files.move(
                tempFile,
                path,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            )
        } finally {
            Files.deleteIfExists(tempFile)
        }
    }

@Suppress("DEPRECATION")
private fun currentDataVersion() = Bukkit.getUnsafe().dataVersion

private fun CompoundBinaryTag.toItemStack(dataVersion: Int): ItemStack {
    val output = ByteArrayOutputStream()
    BinaryTagIO.writer().write(putInt(DATA_VERSION_TAG, dataVersion), output, GZIP)
    return ItemStack.deserializeBytes(output.toByteArray())
}

private fun ItemStack.toTag(): CompoundBinaryTag = BinaryTagIO.unlimitedReader()
    .read(ByteArrayInputStream(serializeAsBytes()), GZIP)
    .remove(DATA_VERSION_TAG)
