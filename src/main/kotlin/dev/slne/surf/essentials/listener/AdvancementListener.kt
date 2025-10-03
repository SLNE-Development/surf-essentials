package dev.slne.surf.essentials.listener

import dev.slne.surf.essentials.util.translatable
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.advancement.AdvancementDisplay
import org.bukkit.GameRule
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent

object AdvancementListener : Listener {
    @EventHandler
    fun onAdvancementDone(event: PlayerAdvancementDoneEvent) {
        val display = event.advancement.display ?: return
        val player = event.player

        if (event.message() == null) {
            return
        }

        if (player.world.getGameRuleValue(GameRule.ANNOUNCE_ADVANCEMENTS) == false) {
            return
        }

        val translationKey = when (display.frame()) {
            AdvancementDisplay.Frame.TASK -> "chat.type.advancement.task"
            AdvancementDisplay.Frame.CHALLENGE -> "chat.type.advancement.challenge"
            else -> "chat.type.advancement.goal"
        }

        event.message(buildText {
            appendPrefix()
            translatable(
                translationKey,
                player.displayName().colorIfAbsent(Colors.VARIABLE_VALUE),
                event.advancement.displayName().colorIfAbsent(Colors.YELLOW)
            ).colorIfAbsent(Colors.SPACER)
        })
    }
}