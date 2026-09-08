package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity


private const val MAX_TELEPORT_ENTITIES = 10

fun teleportCommand() = commandTree("teleport") {
    withPermission(EssentialsPermissionRegistry.TELEPORT_COMMAND)
    withAliases("tp")
    entitySelectorArgumentOneEntity("target") {
        playerExecutor { player, args ->
            val target: Entity by args

            player.teleportAsync(target.location)

            player.sendText {
                appendSuccessPrefix()
                success("Du wurdest zu ")
                variableValue(target.name)
                success(" teleportiert.")
            }
        }
    }
    locationArgument("location") {
        playerExecutor { player, args ->
            val location: Location by args

            player.teleportAsync(location)

            player.sendText {
                appendSuccessPrefix()
                success("Du wurdest zu ")
                variableValue("${location.blockX}, ${location.blockY}, ${location.blockZ}")
                success(" teleportiert.")
            }
        }

        worldArgument("world") {
            playerExecutor { player, args ->
                val location: Location by args
                val world: World by args

                player.teleportAsync(location.clone().apply {
                    this.world = world
                })

                player.sendText {
                    appendSuccessPrefix()
                    success("Du wurdest zu ")
                    variableValue("${location.blockX}, ${location.blockY}, ${location.blockZ} in Welt ${world.name}")
                    success(" teleportiert.")
                }
            }
        }
    }
    entitySelectorArgumentManyEntities("players") {
        withPermission(EssentialsPermissionRegistry.TELEPORT_COMMAND_OTHERS)
        entitySelectorArgumentOneEntity("target") {
            anyExecutor { executor, args ->
                val players: Collection<Entity> by args
                val target: Entity by args

                if (players.size > MAX_TELEPORT_ENTITIES && !executor.hasPermission(
                        EssentialsPermissionRegistry.TELEPORT_COMMAND_MANY
                    )
                ) {
                    executor.sendText {
                        appendErrorPrefix()
                        error("Du kannst maximal $MAX_TELEPORT_ENTITIES Entitäten auf einmal teleportieren.")
                    }
                    return@anyExecutor
                }

                players.forEach { it.teleportAsync(target.location) }

                if (players.size == 1) {
                    executor.sendText {
                        appendSuccessPrefix()
                        variableValue(players.firstOrNull()?.name ?: "Unbekannt")
                        success(" wurde zu ")
                        variableValue(target.name)
                        success(" teleportiert.")
                    }
                } else {
                    executor.sendText {
                        appendSuccessPrefix()
                        variableValue(players.size.toString())
                        success(" Entitäten wurden zu ")
                        variableValue(target.name)
                        success(" teleportiert.")
                    }
                }
            }
        }
        locationArgument("location") {
            anyExecutor { executor, args ->
                val players: Collection<Entity> by args
                val location: Location by args

                if (players.size > MAX_TELEPORT_ENTITIES && !executor.hasPermission(
                        EssentialsPermissionRegistry.TELEPORT_COMMAND_MANY
                    )
                ) {
                    executor.sendText {
                        appendErrorPrefix()
                        error("Du kannst maximal $MAX_TELEPORT_ENTITIES Entitäten auf einmal teleportieren.")
                    }
                    return@anyExecutor
                }

                players.forEach { it.teleportAsync(location) }

                if (players.size == 1) {
                    executor.sendText {
                        appendSuccessPrefix()
                        variableValue(players.firstOrNull()?.name ?: "Unbekannt")
                        success(" wurde zu ")
                        variableValue("${location.blockX}, ${location.blockY}, ${location.blockZ}")
                        success(" teleportiert.")
                    }
                } else {
                    executor.sendText {
                        appendSuccessPrefix()
                        variableValue(players.size.toString())
                        success(" Entities wurden zu ")
                        variableValue("${location.blockX}, ${location.blockY}, ${location.blockZ}")
                        success(" teleportiert.")
                    }
                }

                players.forEach {
                    it.sendText {
                        appendSuccessPrefix()
                        success("Du wurdest zu ")
                        variableValue("${location.blockX}, ${location.blockY}, ${location.blockZ}")
                        success(" teleportiert.")
                    }
                }
            }
        }
    }
}