package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.service.worldService
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.util.isFolia
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit
import org.bukkit.World

fun worldCommand() = commandTree("world") {
    withPermission(EssentialsPermissionRegistry.WORLD_COMMAND)

    literalArgument("lock") {
        worldArgument("world") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_LOCK)
            anyExecutor { executor, args ->
                val world: World by args

                if (Bukkit.getServer().isFolia()) {
                    executor.sendText {
                        appendPrefix()
                        error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                    }
                    return@anyExecutor
                }

                if (worldService.isLocked(world)) {
                    executor.sendText {
                        appendPrefix()
                        error("Die Welt ist bereits gesperrt.")
                    }
                    return@anyExecutor
                }

                worldService.lock(world)
                executor.sendText {
                    appendPrefix()
                    success("Die Welt ")
                    variableValue(world.name)
                    success(" wurde gesperrt.")
                }
            }
        }
    }

    literalArgument("unlock") {
        worldArgument("world") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_UNLOCK)
            anyExecutor { executor, args ->
                val world: World by args

                if (Bukkit.getServer().isFolia()) {
                    executor.sendText {
                        appendPrefix()
                        error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                    }
                    return@anyExecutor
                }

                if (!worldService.isLocked(world)) {
                    executor.sendText {
                        appendPrefix()
                        error("Die Welt ist nicht gesperrt.")
                    }
                    return@anyExecutor
                }

                worldService.unlock(world)
                executor.sendText {
                    appendPrefix()
                    success("Die Welt ")
                    variableValue(world.name)
                    success(" wurde entsperrt.")
                }
            }
        }
    }

    literalArgument("join") {
        worldArgument("world") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_JOIN)
            playerExecutor { player, args ->
                val world: World by args

                player.sendText {
                    appendPrefix()
                    info("Du wirst in die Welt ")
                    variableValue(world.name)
                    info(" teleportiert...")
                }

                player.teleportAsync(world.spawnLocation).thenRun {
                    player.sendText {
                        appendPrefix()
                        success("Du wurdest in die Welt ")
                        variableValue(world.name)
                        success(" teleportiert.")
                    }
                }
            }
        }
    }

    literalArgument("create") {
        stringArgument("name") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_CREATE)
            anyExecutor { executor, args ->
                val name: String by args

                executor.sendText {
                    appendPrefix()
                    info("Dieser Befehl ist zurzeit deaktiviert.")
                }
                return@anyExecutor

                if (Bukkit.getServer().isFolia()) {
                    executor.sendText {
                        appendPrefix()
                        error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                    }
                    return@anyExecutor
                }

                worldService.create(executor, name, null, null, null, null, null)
            }
        }
    }

    literalArgument("delete") {
        worldArgument("world") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_DELETE)
            anyExecutor { executor, args ->
                val world: World by args

                executor.sendText {
                    appendPrefix()
                    info("Dieser Befehl ist zurzeit deaktiviert.")
                }
                return@anyExecutor

                if (Bukkit.getServer().isFolia()) {
                    executor.sendText {
                        appendPrefix()
                        error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                    }
                    return@anyExecutor
                }

                worldService.delete(executor, world)
            }
        }
    }

    literalArgument("load") {
        stringArgument("name") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_LOAD)
            anyExecutor { executor, args ->
                val name: String by args

                if (Bukkit.getServer().isFolia()) {
                    executor.sendText {
                        appendPrefix()
                        error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                    }
                    return@anyExecutor
                }


                worldService.load(executor, name)
            }
        }
    }

    literalArgument("unload") {
        worldArgument("world") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_UNLOAD)
            anyExecutor { executor, args ->
                val world: World by args

                executor.sendText {
                    appendPrefix()
                    info("Dieser Befehl ist zurzeit deaktiviert.")
                }
                return@anyExecutor

                if (Bukkit.getServer().isFolia()) {
                    executor.sendText {
                        appendPrefix()
                        error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                    }
                    return@anyExecutor
                }

                worldService.unload(executor, world)
            }
        }
    }
}