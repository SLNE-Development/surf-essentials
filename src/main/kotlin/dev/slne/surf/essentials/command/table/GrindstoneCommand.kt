@file:Suppress("DEPRECATION")

package dev.slne.surf.essentials.command.table

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun grindstoneCommand() = commandTree("grindstone") {
    withPermission(EssentialsPermissionRegistry.ANVIL_COMMAND)
    playerExecutor { player, _ ->
        player.openGrindstone(null, true)
        player.sendText {
            appendPrefix()
            success("Du hast einen Schleifstein geöffnet.")
        }
    }
}