@file:Suppress("DEPRECATION")

package dev.slne.surf.essentials.command.table

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry

fun workbenchCommand() = commandTree("workbench") {
    withAliases("wb")
    withPermission(EssentialsPermissionRegistry.WORKBENCH_COMMAND)
    playerExecutor { player, _ ->
        player.openWorkbench(null, true)
        player.sendText {
            appendSuccessPrefix()
            success("Du hast eine Werkbank geöffnet.")
        }
    }
}