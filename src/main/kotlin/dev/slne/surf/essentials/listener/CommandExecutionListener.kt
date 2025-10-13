package dev.slne.surf.essentials.listener

import dev.slne.surf.surfapi.core.api.messages.adventure.clickRunsCommand
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object CommandExecutionListener : Listener {
    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        val command = event.message

        when (val commandName = command.split(" ").first().lowercase().replace("/", "")) {
            "kick", "ban", "pardon", "ban-ip", "pardon-ip", "fill", "fillbiome", "setblock", "clone" -> {
                event.player.sendText {
                    appendPrefix()
                    error("Dieser Befehl wurde deaktiviert. Möchtest du ihn wirklich ausführen? ")
                    spacer("[")
                    success("Ja")
                    spacer("]")
                    clickRunsCommand("/minecraft:$commandName ${command.removePrefix(commandName)}")
                }

                event.isCancelled = true
            }
        }
    }
}