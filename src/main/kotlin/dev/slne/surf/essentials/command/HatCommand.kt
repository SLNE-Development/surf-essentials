package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentOnePlayer
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

fun hatCommand() = commandTree("hat") {
    withPermission(EssentialsPermissionRegistry.HAT_COMMAND)
    playerExecutor { player, _ ->
        val itemInHand = player.inventory.itemInMainHand
        player.inventory.helmet = itemInHand

        if (itemInHand.type.isAir) {
            player.sendText {
                appendPrefix()
                success("Du hast deinen Hut entfernt.")
            }
        } else {
            player.sendText {
                appendPrefix()
                success("Du hast deinen Hut gesetzt.")
            }
        }
    }
    entitySelectorArgumentOnePlayer("player") {
        withPermission(EssentialsPermissionRegistry.HAT_COMMAND_OTHERS)
        playerExecutor { executor, args ->
            val player: Player by args
            val itemInHand = executor.inventory.itemInMainHand
            player.inventory.helmet = itemInHand

            if (itemInHand.type.isAir) {
                executor.sendText {
                    appendPrefix()
                    variableValue(player.name)
                    success("s Hut wurde entfernt.")
                }
                player.sendText {
                    appendPrefix()
                    success("Dir wurde der Hut entfernt.")
                }
            } else {
                executor.sendText {
                    appendPrefix()
                    variableValue(player.name)
                    success("s Hut wurde gesetzt.")
                }
                player.sendText {
                    appendPrefix()
                    success("Dir wurde der Hut gesetzt.")
                }
            }
        }
    }
}