package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry

fun fillStackCommand() = commandTree("fillstack") {
    withPermission(EssentialsPermissionRegistry.FILLSTACK_COMMAND)
    playerExecutor { player, _ ->
        val itemInHand = player.inventory.itemInMainHand

        if (itemInHand.isEmpty) {
            player.sendText {
                appendErrorPrefix()
                error("Du musst ein Item in der Hand halten.")
            }
            return@playerExecutor
        }

        itemInHand.amount = itemInHand.maxStackSize

        player.sendText {
            appendSuccessPrefix()
            success("Der Item wurde aufgefüllt.")
        }
    }
}