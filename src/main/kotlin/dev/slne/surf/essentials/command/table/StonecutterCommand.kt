@file:Suppress("DEPRECATION")

package dev.slne.surf.essentials.command.table

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry

fun stoneCutterCommand() = commandTree("stonecutter") {
    withPermission(EssentialsPermissionRegistry.STONE_CUTTER_COMMAND)
    playerExecutor { player, _ ->
        player.openStonecutter(null, true)
        player.sendText {
            appendSuccessPrefix()
            success("Du hast eine Steinschneidemaschine geöffnet.")
        }
    }
}