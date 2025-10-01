package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

fun healCommand() = commandTree("heal") {
    withPermission(EssentialsPermissionRegistry.HEAL_COMMAND)
    playerExecutor { player, _ ->
        player.health = player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
        player.fireTicks = 0
        player.foodLevel = 20
        player.sendText {
            appendPrefix()
            success("Du hast dich geheilt.")
        }
    }

    entitySelectorArgumentOnePlayer("player") {
        withPermission(EssentialsPermissionRegistry.HEAL_COMMAND_OTHERS)
        anyExecutor { executor, args ->
            val player: Player by args

            player.health = player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0

            executor.sendText {
                appendPrefix()
                variableValue(player.name)
                success(" wurde geheilt.")
            }

            player.sendText {
                appendPrefix()
                success("Du wurdest geheilt.")
            }
        }
    }
}