@file:Suppress("DEPRECATION")

package dev.slne.surf.essentials.command.table

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun cartographyTableCommand() = commandTree("cartographyTable") {
    withPermission(EssentialsPermissionRegistry.CARTOGRAPY_TABLE_COMMAND)
    playerExecutor { player, _ ->
        player.openCartographyTable(null, true)
        player.sendText {
            appendPrefix()
            success("Du hast einen Kartentisch geöffnet.")
        }
    }
}