package dev.slne.surf.essentials.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.essentials.command.argument.gameModeArgument
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.setOfflineGameMode
import dev.slne.surf.essentials.util.translatable
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlinx.coroutines.Dispatchers
import org.bukkit.Bukkit
import org.bukkit.GameMode

fun gameModeOfflineCommand() = commandTree("gamemodeoffline") {
    withAliases("gmo")
    withPermission(EssentialsPermissionRegistry.GAME_MODE_COMMAND_OFFLINE)
    gameModeArgument("gameMode") {
        stringArgument("target") {
            anyExecutor { executor, args ->
                val gameMode: GameMode by args
                val target: String by args

                plugin.launch(Dispatchers.IO) {
                    val player = Bukkit.getOfflinePlayer(target)

                    player.setOfflineGameMode(gameMode)
                    executor.sendText {
                        appendPrefix()
                        success("Der Spielmodus von ")
                        variableValue(player.name ?: target)
                        success(" wurde zu ")
                        translatable(gameMode.translationKey()).color(Colors.VARIABLE_VALUE)
                        success(" geändert.")
                    }
                }
            }
        }
    }
}