package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.command.argument.gameModeArgument
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.translatable
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
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
                appendPrefix()
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
                    appendPrefix()
                    success("Der Spielmodus von ")
                    variableValue(changedPlayers.keys.size)
                    success(" Spielern wurde zu ")
                    translatable(gameMode.translationKey()).color(Colors.VARIABLE_VALUE)
                    success(" geändert.")
                }

                for (player in changedPlayers.keys) {
                    player.sendText {
                        appendPrefix()
                        info("Dein Spielmodus wurde zu ")
                        translatable(gameMode.translationKey()).color(Colors.VARIABLE_VALUE)
                        info(" geändert.")
                    }
                }
            }
        }
    }
}