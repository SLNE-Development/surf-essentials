package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

fun hurtCommand() = commandTree("hurt") {
    withAliases("damage")
    withPermission(EssentialsPermissionRegistry.HURT_COMMAND)
    entitySelectorArgumentOnePlayer("player") {
        doubleArgument("damage") {
            anyExecutor { executor, args ->
                val player: Player by args
                val damage: Double by args

                player.damage(damage)

                executor.sendText {
                    appendPrefix()
                    success("Du hast ")
                    variableValue(player.name)
                    success(" ")
                    variableValue("${damage}x")
                    success(" geschlagen.")
                }
            }
        }
    }
}