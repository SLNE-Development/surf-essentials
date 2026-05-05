package dev.slne.surf.essentials.listener

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.essentials.command.minecraft.isRestartJoinBlocked
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

object RestartListener : Listener {
    @EventHandler
    fun onAsyncPlayerPreLogin(event: AsyncPlayerPreLoginEvent) {
        if (!isRestartJoinBlocked()) {
            return
        }

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, buildText {
            error("Der Server wird neugestartet...")
        })
    }
}
