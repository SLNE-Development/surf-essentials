@file:Suppress("DEPRECATION")

package dev.slne.surf.essentials.command.table

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun workbenchCommand() = commandTree("workbench") {
    withAliases("wb")
    withPermission(EssentialsPermissionRegistry.WORKBENCH_COMMAND)
    playerExecutor { player, _ ->
        player.openWorkbench(null, true)
        player.sendText {
            appendPrefix()
            success("Du hast eine Werkbank geöffnet.")
        }
    }
}