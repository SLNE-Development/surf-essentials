package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentOnePlayer
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.GameMode
import org.bukkit.entity.Player

fun spectateCommand() = commandTree("spectate") {
    withPermission(EssentialsPermissionRegistry.SPECTATE_COMMAND)
    playerExecutor { player, _ ->
        val target = player.spectatorTarget ?: run {
            player.sendText {
                appendPrefix()
                error("Du beobachtest gerade keinen Spieler.")
            }
            return@playerExecutor
        }

        player.spectatorTarget = null

        player.sendText {
            appendPrefix()
            success("Du beobachtest nun nicht mehr ")
            variableValue(target.name)
            success(".")
        }
    }

    entitySelectorArgumentOnePlayer("user") {
        playerExecutor { player, args ->
            val user: Player by args

            player.gameMode = GameMode.SPECTATOR
            player.spectatorTarget = user

            player.sendText {
                appendPrefix()
                success("Du beobachtest nun ")
                variableValue(user.name)
                success(".")
            }
        }

        entitySelectorArgumentOnePlayer("target") {
            withPermission(EssentialsPermissionRegistry.SPECTATE_COMMAND_OTHERS)
            playerExecutor { executor, args ->
                val user: Player by args
                val target: Player by args

                user.gameMode = GameMode.SPECTATOR
                user.spectatorTarget = target

                executor.sendText {
                    appendPrefix()
                    variableValue(user.name)
                    success(" beobachtet nun ")
                    variableValue(target.name)
                    success(".")
                }

                user.sendText {
                    appendPrefix()
                    success("Du beobachtest nun ")
                    variableValue(target.name)
                    success(".")
                }
            }
        }
    }
}