@file:Suppress("DEPRECATION")

package dev.slne.surf.essentials.command.table

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun anvilCommand() = commandTree("anvil") {
    withPermission(EssentialsPermissionRegistry.ANVIL_COMMAND)
    playerExecutor { player, _ ->
        player.openAnvil(null, true)
        player.sendText {
            appendPrefix()
            success("Du hast einen Amboss geöffnet.")
        }
    }
}