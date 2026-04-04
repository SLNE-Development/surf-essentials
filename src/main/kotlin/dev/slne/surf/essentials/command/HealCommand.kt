package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

fun healCommand() = commandTree("heal") {
    withPermission(EssentialsPermissionRegistry.HEAL_COMMAND)
    playerExecutor { player, _ ->
        player.health = player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
        player.fireTicks = 0
        player.foodLevel = 20
        player.sendText {
            appendSuccessPrefix()
            success("Du hast dich geheilt.")
        }
    }

    entitySelectorArgumentOnePlayer("player") {
        withPermission(EssentialsPermissionRegistry.HEAL_COMMAND_OTHERS)
        anyExecutor { executor, args ->
            val player: Player by args

            player.health = player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0

            executor.sendText {
                appendSuccessPrefix()
                variableValue(player.name)
                success(" wurde geheilt.")
            }

            player.sendText {
                appendSuccessPrefix()
                success("Du wurdest geheilt.")
            }
        }
    }
}