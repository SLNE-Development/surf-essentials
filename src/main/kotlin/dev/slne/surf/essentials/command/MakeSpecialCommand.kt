package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.service.specialItemService
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun makeSpecialCommand() = commandTree("makespecial") {
    withPermission(EssentialsPermissionRegistry.MAKE_SPECIAL_COMMAND)
    playerExecutor { player, _ ->
        val itemInHand = player.inventory.itemInMainHand

        if (itemInHand.type.isAir) {
            player.sendText {
                appendPrefix()
                error("Du musst ein Item in der Hand halten.")
            }
            return@playerExecutor
        }

        specialItemService.makeSpecial(itemInHand)
        specialItemService.unMarkAsAnnounced(itemInHand)

        player.sendText {
            appendPrefix()
            success("Das Item wurde als Spezialitem markiert.")
        }
    }
}