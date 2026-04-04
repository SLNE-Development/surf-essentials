package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.util.coloredPing
import org.bukkit.entity.Player

fun pingCommand() = commandTree("ping") {
    withPermission(EssentialsPermissionRegistry.PING_COMMAND)
    playerExecutor { player, _ ->
        val ping = player.ping.toLong()

        player.sendText {
            appendInfoPrefix()
            info("Du hast einen Ping von ")
            coloredPing(ping)
            info(".")
        }
    }

    entitySelectorArgumentOnePlayer("target") {
        withPermission(EssentialsPermissionRegistry.PING_COMMAND_OTHER)
        anyExecutor { executor, args ->
            val target: Player by args

            val ping = target.ping.toLong()

            executor.sendText {
                appendInfoPrefix()
                variableValue(target.name)
                info(" hat einen Ping von ")
                coloredPing(ping)
                info(".")
            }
        }
    }
}