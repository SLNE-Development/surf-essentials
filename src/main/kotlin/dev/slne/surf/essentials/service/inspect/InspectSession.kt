package dev.slne.surf.essentials.service.inspect

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.util.saveOfflineInventory
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Level

private fun ItemStack?.normalized() = this?.takeUnless { it.isEmpty }

sealed class InspectSession(
    val viewer: Player,
    val targetId: UUID,
    val targetName: String,
    val type: InspectType,
    val canEdit: Boolean,
) : InventoryHolder {
    private val view: Inventory = Bukkit.createInventory(this, type.viewSize, buildText {
        append(Component.text("${type.displayName} von ", NamedTextColor.DARK_BLUE))
        append(Component.text(targetName, NamedTextColor.GOLD))
        if (!canEdit) append(Component.text(" (Nur Ansicht)", NamedTextColor.DARK_GRAY))
    }).also { inventory ->
        for (viewSlot in 0 until type.viewSize) {
            type.decorationOf(viewSlot)?.let { inventory.setItem(viewSlot, it) }
        }
    }

    override fun getInventory() = view

    abstract fun isEditable(): Boolean

    open fun beforeInteraction() {}

    abstract fun onClose()

    protected fun viewItem(dataSlot: Int) = view.getItem(type.viewSlotOf(dataSlot)).normalized()

    protected fun setViewItem(dataSlot: Int, item: ItemStack?) =
        view.setItem(type.viewSlotOf(dataSlot), item)

    protected fun viewItems() = Array(type.dataSize) { viewItem(it)?.clone() }
}

class OnlineInspectSession(
    viewer: Player,
    private val target: Player,
    type: InspectType,
    canEdit: Boolean,
) : InspectSession(viewer, target.uniqueId, target.name, type, canEdit) {
    private val baseline = arrayOfNulls<ItemStack>(type.dataSize)
    private var task: ScheduledTask? = null
    private var ticks = 0
    private var closed = false

    @Volatile
    private var remoteSyncRunning = false

    fun start() {
        if (Bukkit.isOwnedByCurrentRegion(target)) sync()
        task = viewer.scheduler.runAtFixedRate(plugin, { tick() }, null, 1L, 1L)
    }

    override fun isEditable() = canEdit &&
            !remoteSyncRunning &&
            target.isOnline &&
            Bukkit.isOwnedByCurrentRegion(target)

    override fun beforeInteraction() {
        if (isEditable()) sync()
    }

    override fun onClose() {
        closed = true
        task?.cancel()

        if (!target.isOnline) return
        if (Bukkit.isOwnedByCurrentRegion(target)) {
            sync()
        } else {
            val pending = pendingChanges()
            if (pending.isNotEmpty()) {
                target.scheduler.run(plugin, { applyToTarget(pending) }, null)
            }
        }
    }

    private fun tick() {
        if (closed) return
        if (!target.isOnline) {
            viewer.closeInventory()
            return
        }
        if (remoteSyncRunning) return

        if (Bukkit.isOwnedByCurrentRegion(target)) {
            sync()
        } else if (ticks++ % REMOTE_SYNC_INTERVAL_TICKS == 0) {
            remoteSync()
        }
    }

    private fun sync() {
        val data = type.dataInventoryOf(target)
        for (slot in 0 until type.dataSize) {
            val viewItem = viewItem(slot)
            if (viewItem != baseline[slot]) {
                data.setItem(slot, viewItem)
                baseline[slot] = viewItem?.clone()
                continue
            }

            val dataItem = data.getItem(slot).normalized()
            if (dataItem != baseline[slot]) {
                setViewItem(slot, dataItem)
                baseline[slot] = dataItem?.clone()
            }
        }
    }

    private fun remoteSync() {
        remoteSyncRunning = true
        val pending = pendingChanges()

        val scheduled = target.scheduler.run(plugin, {
            applyToTarget(pending)
            val data = type.dataInventoryOf(target)
            val snapshot = Array(type.dataSize) { data.getItem(it).normalized()?.clone() }

            val returned = viewer.scheduler.run(plugin, {
                merge(snapshot, pending)
                remoteSyncRunning = false
            }, { remoteSyncRunning = false })
            if (returned == null) remoteSyncRunning = false
        }, { remoteSyncRunning = false })

        if (scheduled == null) remoteSyncRunning = false
    }

    private fun pendingChanges(): Map<Int, ItemStack?> = buildMap {
        for (slot in 0 until type.dataSize) {
            val viewItem = viewItem(slot)
            if (viewItem != baseline[slot]) put(slot, viewItem?.clone())
        }
    }

    private fun applyToTarget(changes: Map<Int, ItemStack?>) {
        val data = type.dataInventoryOf(target)
        changes.forEach { (slot, item) -> data.setItem(slot, item) }
    }

    private fun merge(snapshot: Array<ItemStack?>, pushed: Map<Int, ItemStack?>) {
        if (closed) return

        for (slot in 0 until type.dataSize) {
            val expected = if (pushed.containsKey(slot)) pushed[slot] else baseline[slot]
            baseline[slot] = snapshot[slot]
            if (viewItem(slot) == expected) setViewItem(slot, snapshot[slot])
        }
    }

    private companion object {
        const val REMOTE_SYNC_INTERVAL_TICKS = 10
    }
}

class OfflineInspectSession(
    viewer: Player,
    targetId: UUID,
    targetName: String,
    type: InspectType,
    items: Array<ItemStack?>,
    private val lock: OfflineInventoryLock?,
) : InspectSession(viewer, targetId, targetName, type, lock != null) {
    private val finished = AtomicBoolean(false)

    init {
        items.forEachIndexed { slot, item -> setViewItem(slot, item) }
    }

    override fun isEditable() = canEdit && !finished.get()

    override fun onClose() {
        if (!finished.compareAndSet(false, true)) return
        val lock = lock ?: return
        val items = viewItems()

        plugin.launch {
            try {
                saveOfflineInventory(targetId, type, items)
            } catch (e: Exception) {
                plugin.logger.log(Level.SEVERE, "Could not save ${type.name} of $targetName ($targetId)", e)
            } finally {
                lock.release()
            }
        }
    }

    fun requestClose() {
        viewer.scheduler.run(plugin, {
            if (viewer.openInventory.topInventory.holder === this) viewer.closeInventory()
        }, null)
    }

    suspend fun saveNow() {
        if (!finished.compareAndSet(false, true)) return
        val lock = lock ?: return

        try {
            saveOfflineInventory(targetId, type, viewItems())
        } finally {
            lock.release()
        }
    }
}
