package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.service.worldService
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.World

fun worldCommand() = commandTree("world") {
    withPermission(EssentialsPermissionRegistry.WORLD_COMMAND)

    literalArgument("lock") {
        worldArgument("world") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_LOCK)
            anyExecutor { executor, args ->
                val world: World by args

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

                player.teleport(world.spawnLocation)
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