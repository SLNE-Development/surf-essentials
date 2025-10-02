package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

fun godCommand() = commandTree("god") {
    withPermission(EssentialsPermissionRegistry.GOD_COMMAND)
    playerExecutor { player, _ ->
        player.isInvulnerable = !player.isInvulnerable

        player.sendText {
            appendPrefix()
            success("Du bist nun ")

            if (player.isInvulnerable) {
                variableValue("unverwundbar.")
            } else {
                variableValue("nicht mehr unverwundbar.")
            }
        }
    }

    entitySelectorArgumentOnePlayer("player") {
        withPermission(EssentialsPermissionRegistry.GOD_COMMAND_OTHER)
        anyExecutor { executor, args ->
            val player: Player by args

            player.isInvulnerable = !player.isInvulnerable

            executor.sendText {
                appendPrefix()
                variableValue(player.name)
                success(" ist nun ")

                if (player.isInvulnerable) {
                    variableValue("unverwundbar.")
                } else {
                    variableValue("nicht mehr unverwundbar.")
                }
            }

            player.sendText {
                appendPrefix()
                success("Du bist nun ")

                if (player.isInvulnerable) {
                    variableValue("unverwundbar.")
                } else {
                    variableValue("nicht mehr unverwundbar.")
                }
            }
        }
    }
}