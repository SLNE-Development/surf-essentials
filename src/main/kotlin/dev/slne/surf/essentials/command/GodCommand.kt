package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import org.bukkit.entity.Player

fun godCommand() = commandTree("god") {
    withPermission(EssentialsPermissionRegistry.GOD_COMMAND)
    playerExecutor { player, _ ->
        player.isInvulnerable = !player.isInvulnerable

        player.sendText {
            appendSuccessPrefix()
            success("Du bist nun ")

            if (player.isInvulnerable) {
                variableValue("unverwundbar.")
            } else {
                variableValue("verwundbar.")
            }
        }
    }

    entitySelectorArgumentOnePlayer("player") {
        withPermission(EssentialsPermissionRegistry.GOD_COMMAND_OTHER)
        anyExecutor { executor, args ->
            val player: Player by args

            player.isInvulnerable = !player.isInvulnerable

            executor.sendText {
                appendSuccessPrefix()
                variableValue(player.name)
                success(" ist nun ")

                if (player.isInvulnerable) {
                    variableValue("unverwundbar.")
                } else {
                    variableValue("verwundbar.")
                }
            }

            player.sendText {
                appendSuccessPrefix()
                success("Du bist nun ")

                if (player.isInvulnerable) {
                    variableValue("unverwundbar.")
                } else {
                    variableValue("verwundbar.")
                }
            }
        }
    }
}