package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentOnePlayer
import dev.jorel.commandapi.kotlindsl.getValue
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

fun deopCommand() = commandTree("deop") {
    withPermission(EssentialsPermissionRegistry.DEOP_COMMAND)
    entitySelectorArgumentOnePlayer("player") {
        anyExecutor { executor, args ->
            val player: Player by args

            if (!player.isOp) {
                executor.sendText {
                    appendPrefix()
                    error("Der Spieler ist kein Operator.")
                }
                return@anyExecutor
            }

            player.isOp = false

            executor.sendText {
                appendPrefix()
                variableValue(player.name)
                success(" ist nun kein Operator mehr.")
            }

            player.sendText {
                appendPrefix()
                info("Du bist nun kein Operator mehr.")
            }
        }
    }
}