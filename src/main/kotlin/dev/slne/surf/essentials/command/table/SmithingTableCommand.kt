@file:Suppress("DEPRECATION")

package dev.slne.surf.essentials.command.table

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry

fun smithingTableCommand() = commandTree("smithingtable") {
    withPermission(EssentialsPermissionRegistry.SMITHING_TABLE_COMMAND)
    playerExecutor { player, _ ->
        player.openSmithingTable(null, true)
        player.sendText {
            appendSuccessPrefix()
            success("Du hast einen Schmiedetisch geöffnet.")
        }
    }
}