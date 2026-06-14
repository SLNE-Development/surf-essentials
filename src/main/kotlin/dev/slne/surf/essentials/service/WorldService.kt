package dev.slne.surf.essentials.service

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import dev.jorel.commandapi.CommandAPI
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.SurfApiPaper
import dev.slne.surf.essentials.command.argument.world.WorldTypeArgument
import dev.slne.surf.essentials.command.argument.world.worldPath
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.util.isFolia
import dev.slne.surf.essentials.util.world.generator.VoidWorldGenerator
import dev.slne.surf.essentials.util.world.unloadCanvasWorld
import io.canvasmc.canvas.WorldUnloadResult
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.command.CommandSender
import org.bukkit.persistence.PersistentDataType
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

object WorldService {
    private val accessKey = NamespacedKey(plugin, "world_access")

    fun isLocked(world: World): Boolean =
        world.persistentDataContainer.getOrDefault(accessKey, PersistentDataType.BOOLEAN, false)

    fun lock(world: World) =
        world.persistentDataContainer.set(accessKey, PersistentDataType.BOOLEAN, true)

    fun unlock(world: World) =
        world.persistentDataContainer.set(accessKey, PersistentDataType.BOOLEAN, false)

    fun create(
        sender: CommandSender,
        name: String,
        environment: World.Environment?,
        type: WorldTypeArgument.WorldType?,
        generateStructures: Boolean?,
        hardcore: Boolean?,
        seed: Long?,
    ) {
        if (Bukkit.getServer().isFolia()) {
            sender.sendText {
                appendErrorPrefix()
                error("Das Erstellen von Welten wird auf Folia-Servern nicht unterstützt.")
            }
            return
        }

        if (Bukkit.getWorld(name) != null) {
            sender.sendText {
                appendErrorPrefix()
                error("Die Welt existiert bereits.")
            }
            return
        }

        val creator = WorldCreator(name)

        environment?.let { creator.environment(it) }
        type?.let { creator.type(it.vanilla()) }
        generateStructures?.let { creator.generateStructures(it) }
        hardcore?.let { creator.hardcore(it) }
        seed?.let { creator.seed(it) }

        sender.sendText {
            appendInfoPrefix()
            info("Die Welt wird erstellt...")
        }

        val world = creator.createWorld() ?: run {
            sender.sendText {
                appendErrorPrefix()
                error("Die Welt konnte nicht erstellt werden.")
            }
            return
        }

        if (type == WorldTypeArgument.WorldType.VOID) {
            creator.generator(VoidWorldGenerator)
            VoidWorldGenerator.addGeneratorToBukkitYml(name)
        }

        sender.sendText {
            appendSuccessPrefix()
            success("Die Welt ")
            variableValue(world.name)
            success(" wurde erstellt.")
        }
    }

    fun load(sender: CommandSender, name: String) {
        if (Bukkit.getServer().isFolia()) {
            sender.sendText {
                appendErrorPrefix()
                error("Das Laden von Welten wird auf Folia-Servern nicht unterstützt.")
            }
            return
        }

        val file = worldPath.resolve(name)
        if (!file.exists() || !file.isDirectory) {
            sender.sendText {
                appendErrorPrefix()
                error("Die Welt existiert nicht.")
            }
            return
        }

        if (Bukkit.getWorld(name) != null) {
            sender.sendText {
                appendErrorPrefix()
                error("Die Welt ist bereits geladen.")
            }
            return
        }

        sender.sendText {
            appendInfoPrefix()
            info("Die Welt wird geladen...")
        }

        val world = runCatching {
            WorldCreator(name).createWorld() ?: run {
                sender.sendText {
                    appendErrorPrefix()
                    error("Die Welt konnte nicht geladen werden.")
                }
                return
            }
        }.getOrNull() ?: return

        sender.sendText {
            appendSuccessPrefix()
            success("Die Welt ")
            variableValue(world.name)
            success(" wurde geladen.")
        }
    }

    suspend fun unload(sender: CommandSender, world: World) {
        val overworld = Bukkit.getWorlds().firstOrNull()
            ?: throw CommandAPI.failWithString("Es gibt keine Overworld")
        val overworldSpawn = withContext(plugin.globalRegionDispatcher) { overworld.spawnLocation }

        sender.sendText {
            appendInfoPrefix()
            info("Teleporiere Spieler aus der Welt...")
        }

        val semaphore = Semaphore(64)
        coroutineScope {
            world.players.forEach {
                launch {
                    semaphore.withPermit {
                        it.teleportAsync(overworldSpawn).await()
                    }
                }
            }
        }

        sender.sendText {
            appendInfoPrefix()
            info("Die Welt wird entladen...")
        }

        val result: Boolean = if (SurfApiPaper.isCanvasMc) {
            val result = world.unloadCanvasWorld()
            result == WorldUnloadResult.SUCCESS
        } else {
            @Suppress("removal", "DEPRECATION")
            Bukkit.unloadWorld(world, true)
        }

        if (!result) {
            sender.sendText {
                appendErrorPrefix()
                error("Die Welt konnte nicht entladen werden.")
            }
        } else {
            sender.sendText {
                appendSuccessPrefix()
                success("Die Welt ")
                variableValue(world.name)
                success(" wurde entladen.")
            }
        }
    }

    @OptIn(ExperimentalPathApi::class)
    suspend fun delete(sender: CommandSender, world: World) {
        val overworld = Bukkit.getWorlds().firstOrNull()
            ?: throw CommandAPI.failWithString("Es gibt keine Overworld")
        val spawnLocation = withContext(plugin.globalRegionDispatcher) { overworld.spawnLocation }

        val semaphore = Semaphore(64)
        coroutineScope {
            world.players.forEach { player ->
                launch {
                    semaphore.withPermit {
                        player.teleportAsync(spawnLocation).await()
                    }
                }
            }
        }

        val result = if (SurfApiPaper.isCanvasMc) {
            val result = world.unloadCanvasWorld()
            result == WorldUnloadResult.SUCCESS
        } else {
            @Suppress("removal", "DEPRECATION")
            Bukkit.unloadWorld(world, true)
        }

        if (!result) {
            sender.sendText {
                appendErrorPrefix()
                error("Die Welt konnte nicht entladen werden.")
            }
        } else {
            val path = world.worldPath
            if (!path.exists() || !path.isDirectory()) {
                sender.sendText {
                    appendErrorPrefix()
                    error("Die Welt existiert nicht.")
                }
                return
            }

            path.deleteRecursively()
            VoidWorldGenerator.removeGeneratorFromBukkitYml(world.name)

            sender.sendText {
                appendSuccessPrefix()
                success("Die Welt ")
                variableValue(world.name)
                success(" wurde gelöscht.")
            }
        }
    }
}