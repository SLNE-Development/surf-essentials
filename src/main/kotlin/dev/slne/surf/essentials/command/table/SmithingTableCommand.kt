@file:Suppress("DEPRECATION")

package dev.slne.surf.essentials.command.table

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun smithingTableCommand() = commandTree("smithingTable") {
    withPermission(EssentialsPermissionRegistry.SMITHING_TABLE_COMMAND)
    playerExecutor { player, _ ->
        player.openSmithingTable(null, true)
        player.sendText {
            appendPrefix()
            success("Du hast einen Schmiedetisch geöffnet.")
        }
    }
}