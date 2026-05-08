@file:Suppress("DEPRECATION")

package dev.slne.surf.essentials.command.table

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry

fun cartographyTableCommand() = commandTree("cartographytable") {
    withPermission(EssentialsPermissionRegistry.CARTOGRAPY_TABLE_COMMAND)
    playerExecutor { player, _ ->
        player.openCartographyTable(null, true)
        player.sendText {
            appendSuccessPrefix()
            success("Du hast einen Kartentisch geöffnet.")
        }
    }
}