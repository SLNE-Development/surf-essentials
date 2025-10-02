package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
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
    entitySelectorArgumentManyPlayers("players") {
        withPermission(EssentialsPermissionRegistry.TELEPORT_COMMAND_OTHERS)
        entitySelectorArgumentOnePlayer("target") {
            anyExecutor { executor, args ->
                val players: Collection<Player> by args
                val target: Player by args

                players.forEach { it.teleportAsync(target.location) }

                executor.sendText {
                    appendPrefix()
                    variableValue(players.size.toString())
                    success(" Spieler wurden zu ")
                    variableValue(target.name)
                    success(" teleportiert.")
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
    }
}