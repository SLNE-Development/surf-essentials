package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.util.mutableObject2ObjectMapOf
import dev.slne.surf.essentials.command.argument.gameModeArgument
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import org.bukkit.GameMode
import org.bukkit.entity.Player

fun gameModeCommand() = commandTree("gamemode") {
    withAliases("gm")
    withPermission(EssentialsPermissionRegistry.GAME_MODE_COMMAND)
    gameModeArgument("gameMode") {
        playerExecutor { player, args ->
            val gameMode: GameMode by args

            player.gameMode = gameMode
            player.sendText {
                appendSuccessPrefix()
                success("Dein Spielmodus wurde zu ")
                translatable(gameMode.translationKey()).color(Colors.VARIABLE_VALUE)
                success(" geändert.")
            }
        }

        entitySelectorArgumentManyPlayers("players") {
            withPermission(EssentialsPermissionRegistry.GAME_MODE_COMMAND_OTHERS)
            anyExecutor { executor, args ->
                val gameMode: GameMode by args
                val players: Collection<Player> by args
                val changedPlayers = mutableObject2ObjectMapOf<Player, GameMode>()

                for (player in players) {
                    player.gameMode = gameMode
                    changedPlayers[player] = gameMode
                }

                executor.sendText {
                    appendSuccessPrefix()
                    success("Der Spielmodus von ")

                    if (changedPlayers.keys.size == 1) {
                        variableValue(changedPlayers.keys.firstOrNull()?.name ?: "Unbekannt")
                    } else {
                        variableValue("${changedPlayers.keys.size} Spielern")
                    }

                    success(" wurde zu ")
                    translatable(gameMode.translationKey()).color(Colors.VARIABLE_VALUE)
                    success(" geändert.")
                }

                for (player in changedPlayers.keys) {
                    player.sendText {
                        appendInfoPrefix()
                        info("Dein Spielmodus wurde zu ")
                        translatable(gameMode.translationKey()).color(Colors.VARIABLE_VALUE)
                        info(" geändert.")
                    }
                }
            }
        }
    }
}