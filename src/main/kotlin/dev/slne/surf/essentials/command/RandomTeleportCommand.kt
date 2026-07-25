package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.util.random
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import org.bukkit.Bukkit

fun teleportRandomCommand() = commandTree("teleportrandom") {
    withAliases("tpr")
    withPermission(EssentialsPermissionRegistry.TELEPORT_RANDOM_COMMAND)

    playerExecutor { player, _ ->
        val selected = Bukkit.getOnlinePlayers()
            .filter { !it.hasPermission(EssentialsPermissionRegistry.TELEPORT_RANDOM_BYPASS) }
            .secureRandomOrNull() ?: run {
            player.sendText {
                appendErrorPrefix()
                error("Es wurde kein Spieler gefunden, zu dem du teleportiert werden kannst.")
            }
            return@playerExecutor
        }

        player.teleportAsync(selected.location)
        player.sendText {
            appendSuccessPrefix()
            success("Du wurdest zu ")
            variableValue(selected.name)
            success(" teleportiert.")
        }
    }
}


private fun <T> List<T>.secureRandomOrNull(): T? =
    if (isEmpty()) null else this[random.nextInt(size)]