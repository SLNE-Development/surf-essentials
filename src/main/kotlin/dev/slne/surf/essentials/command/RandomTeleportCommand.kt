package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit

fun teleportRandomCommand() = commandTree("teleportrandom") {
    withAliases("tpr")
    withPermission(EssentialsPermissionRegistry.TELEPORT_RANDOM_COMMAND)

    playerExecutor { player, _ ->
        val selected = Bukkit.getOnlinePlayers()
            .filter { !it.hasPermission(EssentialsPermissionRegistry.TELEPORT_RANDOM_BYPASS) }
            .randomOrNull() ?: run {
            player.sendText {
                appendPrefix()
                error("Es wurde kein Spieler gefunden, zu dem du teleportiert werden kannst.")
            }
            return@playerExecutor
        }

        player.teleportAsync(selected.location)
        player.sendText {
            appendPrefix()
            success("Du wurdest zu ")
            variableValue(selected.name)
            success(" teleportiert.")
        }
    }
}