package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Location
import org.bukkit.entity.Player

fun teleportCommand() = commandTree("teleport") {
    withPermission(EssentialsPermissionRegistry.TELEPORT_COMMAND)
    withAliases("tp")
    entitySelectorArgumentOnePlayer("target") {
        playerExecutor { player, args ->
            val target: Player by args

            player.teleportAsync(target.location)

            player.sendText {
                appendPrefix()
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
                appendPrefix()
                success("Du wurdest zu ")
                variableValue("${location.blockX}, ${location.blockY}, ${location.blockZ}")
                success(" teleportiert.")
            }
        }
    }
    entitySelectorArgumentManyPlayers("players") {
        withPermission(EssentialsPermissionRegistry.TELEPORT_COMMAND_OTHERS)
        entitySelectorArgumentOnePlayer("target") {
            anyExecutor { executor, args ->
                val players: Collection<Player> by args
                val target: Player by args

                players.forEach { it.teleportAsync(target.location) }

                if (players.size == 1) {
                    executor.sendText {
                        appendPrefix()
                        variableValue(players.firstOrNull()?.name ?: "Unbekannt")
                        success(" wurde zu ")
                        variableValue(target.name)
                        success(" teleportiert.")
                    }
                } else {
                    executor.sendText {
                        appendPrefix()
                        variableValue(players.size.toString())
                        success(" Spieler wurden zu ")
                        variableValue(target.name)
                        success(" teleportiert.")
                    }
                }

                players.forEach {
                    it.sendText {
                        appendPrefix()
                        success("Du wurdest zu ")
                        variableValue(target.name)
                        success(" teleportiert.")
                    }
                }
            }
        }
        locationArgument("location") {
            anyExecutor { executor, args ->
                val players: Collection<Player> by args
                val location: Location by args

                players.forEach { it.teleportAsync(location) }

                if (players.size == 1) {
                    executor.sendText {
                        appendPrefix()
                        variableValue(players.firstOrNull()?.name ?: "Unbekannt")
                        success(" wurde zu ")
                        variableValue("${location.blockX}, ${location.blockY}, ${location.blockZ}")
                        success(" teleportiert.")
                    }
                } else {
                    executor.sendText {
                        appendPrefix()
                        variableValue(players.size.toString())
                        success(" Spieler wurden zu ")
                        variableValue("${location.blockX}, ${location.blockY}, ${location.blockZ}")
                        success(" teleportiert.")
                    }
                }

                players.forEach {
                    it.sendText {
                        appendPrefix()
                        success("Du wurdest zu ")
                        variableValue("${location.blockX}, ${location.blockY}, ${location.blockZ}")
                        success(" teleportiert.")
                    }
                }
            }
        }
    }
}