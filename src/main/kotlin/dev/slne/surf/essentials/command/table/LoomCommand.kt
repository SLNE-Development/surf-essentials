@file:Suppress("DEPRECATION")

package dev.slne.surf.essentials.command.table

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun loomCommand() = commandTree("loom") {
    withPermission(EssentialsPermissionRegistry.LOOM_COMMAND)
    playerExecutor { player, _ ->
        player.openLoom(null, true)
        player.sendText {
            appendPrefix()
            success("Du hast einen Webstuhl geöffnet.")
        }
    }
}