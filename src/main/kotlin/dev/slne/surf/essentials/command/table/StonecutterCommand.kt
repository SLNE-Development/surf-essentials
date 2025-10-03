@file:Suppress("DEPRECATION")

package dev.slne.surf.essentials.command.table

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun stoneCutterCommand() = commandTree("stonecutter") {
    withPermission(EssentialsPermissionRegistry.STONE_CUTTER_COMMAND)
    playerExecutor { player, _ ->
        player.openStonecutter(null, true)
        player.sendText {
            appendPrefix()
            success("Du hast eine Steinschneidemaschine geöffnet.")
        }
    }
}