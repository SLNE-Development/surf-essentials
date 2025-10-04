package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.service.lastLocationService
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun backCommand() = commandTree("back") {
    withPermission(EssentialsPermissionRegistry.BACK_COMMAND)
    playerExecutor { player, _ ->
        val lastLocation = lastLocationService.getLatestLocation(player.uniqueId) ?: run {
            player.sendText {
                appendPrefix()
                error("Du besitzt keinen letzten Standort.")
            }
            return@playerExecutor
        }

        player.sendText {
            appendPrefix()
            info("Du wirst zu deinem letzten Standort teleportiert...")
        }

        player.teleportAsync(lastLocation).thenRun {
            player.sendText {
                appendPrefix()
                success("Du wurdest zu deinem letzten Standort teleportiert.")
            }
        }
    }
}