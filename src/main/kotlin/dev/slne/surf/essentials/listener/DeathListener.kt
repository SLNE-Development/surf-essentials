package dev.slne.surf.essentials.listener

import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

object DeathListener : Listener {
    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val message = event.deathMessage() ?: return

        event.deathMessage(buildText {
            appendPrefix()
            append(message.replaceText {
                it.matchLiteral(
                    LegacyComponentSerializer
                        .legacyAmpersand()
                        .serialize(event.entity.teamDisplayName())
                        .replace("§", "")
                )
                    .once()
                    .replacement(event.entity.displayName())
            }.colorIfAbsent(Colors.GRAY))
        })
    }
}