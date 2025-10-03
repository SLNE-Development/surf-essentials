package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.slne.surf.essentials.command.argument.gameModeArgument
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.translatable
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit
import org.bukkit.GameMode

fun defaultGameModeCommand() = commandTree("defaultgamemode") {
    withPermission(EssentialsPermissionRegistry.GAME_MODE_COMMAND_DEFAULT)
    anyExecutor { executor, _ ->
        executor.sendText {
            appendPrefix()
            info("Der Standard-Spielmodus ist zurzeit auf ")
            translatable(Bukkit.getDefaultGameMode().translationKey()).color(Colors.VARIABLE_VALUE)
            info("gesetzt.")
        }
    }

    gameModeArgument("gameMode") {
        anyExecutor { executor, args ->
            val gameMode: GameMode by args
            val defaultGameMode = Bukkit.getDefaultGameMode()

            if (gameMode == defaultGameMode) {
                executor.sendText {
                    appendPrefix()
                    error("Der Standard-Spielmodus ist bereits auf ")
                    translatable(gameMode.translationKey()).color(Colors.VARIABLE_VALUE)
                    error(" gesetzt.")
                }
                return@anyExecutor
            }

            Bukkit.setDefaultGameMode(gameMode)

            executor.sendText {
                appendPrefix()
                success("Du hast den Standard-Spielmodus auf ")
                translatable(gameMode.translationKey()).color(Colors.VARIABLE_VALUE)
                success(" gesetzt.")
            }
        }
    }
}