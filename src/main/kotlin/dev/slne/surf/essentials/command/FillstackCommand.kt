package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun fillStackCommand() = commandTree("fillstack") {
    withPermission(EssentialsPermissionRegistry.FILLSTACK_COMMAND)
    playerExecutor { player, _ ->
        val itemInHand = player.inventory.itemInMainHand

        if (itemInHand.isEmpty) {
            player.sendText {
                appendPrefix()
                error("Du musst ein Item in der Hand halten.")
            }
            return@playerExecutor
        }

        itemInHand.amount = itemInHand.maxStackSize

        player.sendText {
            appendPrefix()
            success("Der Item wurde aufgefüllt.")
        }
    }
}