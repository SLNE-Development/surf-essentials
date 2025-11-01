package dev.slne.surf.essentials.service

import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.util.isFolia
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.*
import org.bukkit.command.CommandSender
import org.bukkit.persistence.PersistentDataType

class WorldService {
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
        type: WorldType?,
        generateStructures: Boolean?,
        hardcore: Boolean?,
        seed: Long?
    ) {
        if (Bukkit.getServer().isFolia()) {
            sender.sendText {
                appendPrefix()
                error("Das Erstellen von Welten wird auf Folia-Servern nicht unterstützt.")
            }
            return
        }

        if (Bukkit.getWorld(name) != null) {
            sender.sendText {
                appendPrefix()
                error("Die Welt existiert bereits.")
            }
            return
        }

        val creator = WorldCreator(name)

        environment?.let { creator.environment(it) }
        type?.let { creator.type(it) }
        generateStructures?.let { creator.generateStructures(it) }
        hardcore?.let { creator.hardcore(it) }
        seed?.let { creator.seed(it) }

        sender.sendText {
            appendPrefix()
            info("Die Welt wird erstellt...")
        }

        val world = creator.createWorld() ?: run {
            sender.sendText {
                appendPrefix()
                error("Die Welt konnte nicht erstellt werden.")
            }
            return
        }

        sender.sendText {
            appendPrefix()
            success("Die Welt ")
            variableValue(world.name)
            success(" wurde erstellt.")
        }
    }

    fun load(sender: CommandSender, name: String) {
        if (Bukkit.getServer().isFolia()) {
            sender.sendText {
                appendPrefix()
                error("Das Laden von Welten wird auf Folia-Servern nicht unterstützt.")
            }
            return
        }

        val file = Bukkit.getWorldContainer().resolve(name)
        if (!file.exists() || !file.isDirectory) {
            sender.sendText {
                appendPrefix()
                error("Die Welt existiert nicht.")
            }
            return
        }

        if (Bukkit.getWorld(name) != null) {
            sender.sendText {
                appendPrefix()
                error("Die Welt ist bereits geladen.")
            }
            return
        }

        sender.sendText {
            appendPrefix()
            info("Die Welt wird geladen...")
        }

        val world = WorldCreator(name).createWorld() ?: run {
            sender.sendText {
                appendPrefix()
                error("Die Welt konnte nicht geladen werden.")
            }
            return
        }

        sender.sendText {
            appendPrefix()
            success("Die Welt ")
            variableValue(world.name)
            success(" wurde geladen.")
        }
    }

    fun unload(sender: CommandSender, world: World) {
        if (Bukkit.getServer().isFolia()) {
            sender.sendText {
                appendPrefix()
                error("Das Entladen von Welten wird auf Folia-Servern nicht unterstützt.")
            }
            return
        }

        val overworldSpawn = Bukkit.getWorlds().firstOrNull()?.spawnLocation ?: run {
            sender.sendText {
                appendPrefix()
                error("Es gibt keine andere Welt, in die Spieler teleportiert werden können.")
            }
            return
        }

        world.players.forEach {
            it.teleportAsync(overworldSpawn)
        }

        if (!Bukkit.unloadWorld(world, true)) {
            sender.sendText {
                appendPrefix()
                error("Die Welt konnte nicht entladen werden.")
            }
            return
        }

        sender.sendText {
            appendPrefix()
            success("Die Welt ")
            variableValue(world.name)
            success(" wurde entladen.")
        }
    }

    fun delete(sender: CommandSender, world: World) {
        if (Bukkit.getServer().isFolia()) {
            sender.sendText {
                appendPrefix()
                error("Das Löschen von Welten wird auf Folia-Servern nicht unterstützt.")
            }
            return
        }

        val overworldSpawn = Bukkit.getWorlds().firstOrNull()?.spawnLocation ?: run {
            sender.sendText {
                appendPrefix()
                error("Es gibt keine andere Welt, in die Spieler teleportiert werden können.")
            }
            return
        }

        world.players.forEach {
            it.teleportAsync(overworldSpawn)
        }

        if (Bukkit.getWorld(world.name) != null) {
            if (!Bukkit.unloadWorld(world, true)) {
                sender.sendText {
                    appendPrefix()
                    error("Die Welt konnte nicht entladen werden.")
                }
                return
            }
        }

        val file = Bukkit.getWorldContainer().resolve(world.name)
        if (!file.exists() || !file.isDirectory) {
            sender.sendText {
                appendPrefix()
                error("Die Welt existiert nicht.")
            }
            return
        }

        file.deleteRecursively()

        sender.sendText {
            appendPrefix()
            success("Die Welt ")
            variableValue(world.name)
            success(" wurde gelöscht.")
        }
    }

    companion object {
        val INSTANCE = WorldService()
    }
}

val worldService get() = WorldService.INSTANCE