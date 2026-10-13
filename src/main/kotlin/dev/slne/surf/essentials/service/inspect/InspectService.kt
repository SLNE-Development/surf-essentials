package dev.slne.surf.essentials.service.inspect

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import dev.slne.surf.api.core.luckperms.LuckPermsAccess
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.util.getPlayerFile
import dev.slne.surf.essentials.util.util.loadOfflineInventory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.logging.Level

class OfflineInventoryLock(val key: Key, val holderName: String) {
    data class Key(val targetId: UUID, val type: InspectType)

    private val released = CompletableFuture<Unit>()
    private var invalidated = false
    private var session: OfflineInspectSession? = null

    @Synchronized
    fun bind(session: OfflineInspectSession): Boolean {
        if (invalidated) return false
        this.session = session
        return true
    }

    fun invalidate(): Boolean {
        val session = synchronized(this) {
            invalidated = true
            session
        }
        session?.requestClose()
        return session != null
    }

    fun boundSession() = synchronized(this) { session }

    fun release() {
        InspectService.unregister(this)
        released.complete(Unit)
    }

    fun awaitRelease(timeoutSeconds: Long) = try {
        released.get(timeoutSeconds, TimeUnit.SECONDS)
        true
    } catch (_: Exception) {
        false
    }
}

object InspectService {
    private const val JOIN_TIMEOUT_SECONDS = 10L

    private val offlineLocks = ConcurrentHashMap<OfflineInventoryLock.Key, OfflineInventoryLock>()

    fun editPermission(type: InspectType) = when (type) {
        InspectType.INVENTORY -> EssentialsPermissionRegistry.OPENINV_COMMAND_EDIT
        InspectType.ENDER_CHEST -> EssentialsPermissionRegistry.OPENENDER_COMMAND_EDIT
    }

    suspend fun open(viewer: Player, targetName: String, type: InspectType) {
        val onlineTarget = Bukkit.getPlayerExact(targetName)
        if (onlineTarget != null) {
            openOnline(viewer, onlineTarget, type)
        } else {
            openOffline(viewer, targetName, type)
        }
    }

    private fun openOnline(viewer: Player, target: Player, type: InspectType) {
        if (target == viewer) {
            if (type == InspectType.ENDER_CHEST) {
                viewer.openInventory(viewer.enderChest)
                viewer.sendText {
                    appendSuccessPrefix()
                    success("Du hast deine Endertruhe geöffnet.")
                }
            } else {
                viewer.sendText {
                    appendErrorPrefix()
                    error("Du kannst dein eigenes Inventar nicht öffnen.")
                }
            }
            return
        }

        if (target.hasPermission(EssentialsPermissionRegistry.OPENINV_EXEMPT) && !canBypassExempt(viewer)) {
            viewer.sendExempt(target.name)
            return
        }

        val session = OnlineInspectSession(viewer, target, type, viewer.hasPermission(editPermission(type)))
        if (viewer.openInventory(session.inventory) == null) return
        session.start()
        viewer.sendOpened(session)
    }

    private suspend fun openOffline(viewer: Player, targetName: String, type: InspectType) {
        val offlineTarget = withContext(Dispatchers.IO) {
            Bukkit.getOfflinePlayerIfCached(targetName) ?: Bukkit.getOfflinePlayer(targetName)
        }
        val targetId = offlineTarget.uniqueId
        val name = offlineTarget.name ?: targetName

        if (getPlayerFile(targetId) == null) {
            viewer.sendText {
                appendErrorPrefix()
                error("Für ")
                variableValue(name)
                error(" wurden keine Spielerdaten gefunden.")
            }
            return
        }

        if (isExemptOffline(targetId) && !canBypassExempt(viewer)) {
            viewer.sendExempt(name)
            return
        }

        val wantsEdit = viewer.hasPermission(editPermission(type))
        val lock = if (wantsEdit) {
            val newLock = OfflineInventoryLock(OfflineInventoryLock.Key(targetId, type), viewer.name)
            val existing = offlineLocks.putIfAbsent(newLock.key, newLock)
            if (existing != null) {
                viewer.sendText {
                    appendErrorPrefix()
                    error("${type.article.replaceFirstChar { it.uppercase() }} ${type.displayName} von ")
                    variableValue(name)
                    error(" wird bereits von ")
                    variableValue(existing.holderName)
                    error(" bearbeitet.")
                }
                return
            }
            newLock
        } else {
            null
        }

        val data = try {
            loadOfflineInventory(targetId, type)
        } catch (e: Exception) {
            plugin.logger.log(Level.SEVERE, "Could not load ${type.name} of $name ($targetId)", e)
            null
        }

        withContext(plugin.entityDispatcher(viewer)) {
            if (data == null) {
                lock?.release()
                viewer.sendText {
                    appendErrorPrefix()
                    error("Die Spielerdaten von ")
                    variableValue(name)
                    error(" konnten nicht geladen werden.")
                }
                return@withContext
            }

            if (Bukkit.getPlayer(targetId) != null) {
                lock?.release()
                viewer.sendText {
                    appendErrorPrefix()
                    variableValue(name)
                    error(" ist gerade online gekommen. Bitte versuche es erneut.")
                }
                return@withContext
            }

            val editLock = lock?.takeIf { data.upToDate }
            if (lock != null && editLock == null) {
                lock.release()
                viewer.sendText {
                    appendWarningPrefix()
                    warning("Die Spielerdaten stammen aus einer älteren Version und können nur angesehen werden.")
                }
            }

            val session = OfflineInspectSession(viewer, targetId, name, type, data.items, editLock)
            if (editLock != null && !editLock.bind(session)) {
                editLock.release()
                viewer.sendText {
                    appendErrorPrefix()
                    variableValue(name)
                    error(" ist gerade online gekommen. Bitte versuche es erneut.")
                }
                return@withContext
            }

            if (viewer.openInventory(session.inventory) == null) {
                session.onClose()
                return@withContext
            }
            viewer.sendOpened(session, offline = true)
        }
    }

    fun prepareJoin(targetId: UUID): Boolean {
        val locks = offlineLocks.values.filter { it.key.targetId == targetId }
        val bound = locks.filter { lock ->
            lock.invalidate().also { if (!it) unregister(lock) }
        }
        return bound.all { it.awaitRelease(JOIN_TIMEOUT_SECONDS) }
    }

    fun shutdown() = runBlocking {
        offlineLocks.values.toList().forEach { lock ->
            try {
                lock.boundSession()?.saveNow() ?: lock.release()
            } catch (e: Exception) {
                plugin.logger.log(Level.SEVERE, "Could not save offline inventory ${lock.key}", e)
            }
        }
    }

    internal fun unregister(lock: OfflineInventoryLock) {
        offlineLocks.remove(lock.key, lock)
    }

    private fun canBypassExempt(viewer: Player) =
        viewer.hasPermission(EssentialsPermissionRegistry.OPENINV_EXEMPT_BYPASS)

    private suspend fun isExemptOffline(targetId: UUID): Boolean {
        val user = LuckPermsAccess.luckperms.userManager.loadUser(targetId).await()
        return user.cachedData.permissionData
            .checkPermission(EssentialsPermissionRegistry.OPENINV_EXEMPT)
            .asBoolean()
    }

    private fun Player.sendExempt(targetName: String) = sendText {
        appendErrorPrefix()
        error("Das Inventar von ")
        variableValue(targetName)
        error(" kann nicht geöffnet werden.")
    }

    private fun Player.sendOpened(session: InspectSession, offline: Boolean = false) = sendText {
        appendSuccessPrefix()
        success("Du hast ${session.type.article} ${session.type.displayName} von ")
        variableValue(session.targetName)
        if (offline) spacer(" (offline)")
        success(" geöffnet.")
        if (!session.canEdit) spacer(" (Nur Ansicht)")
    }
}
