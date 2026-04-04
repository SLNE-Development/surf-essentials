package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentOnePlayer
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import org.bukkit.entity.Player

fun hatCommand() = commandTree("hat") {
    withPermission(EssentialsPermissionRegistry.HAT_COMMAND)
    playerExecutor { player, _ ->
        val itemInHand = player.inventory.itemInMainHand
        player.inventory.helmet = itemInHand

        if (itemInHand.type.isAir) {
            player.sendText {
                appendSuccessPrefix()
                success("Du hast deinen Hut entfernt.")
            }
        } else {
            player.sendText {
                appendSuccessPrefix()
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
                    appendSuccessPrefix()
                    variableValue(player.name)
                    success("s Hut wurde entfernt.")
                }
                player.sendText {
                    appendSuccessPrefix()
                    success("Dir wurde der Hut entfernt.")
                }
            } else {
                executor.sendText {
                    appendSuccessPrefix()
                    variableValue(player.name)
                    success("s Hut wurde gesetzt.")
                }
                player.sendText {
                    appendSuccessPrefix()
                    success("Dir wurde der Hut gesetzt.")
                }
            }
        }
    }
}