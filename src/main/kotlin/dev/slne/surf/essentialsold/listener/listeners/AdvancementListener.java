package dev.slne.surf.essentialsold.listener.listeners;

import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.GameRule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

/**
 * A {@link Listener} for player advancement events, which sends a chat message to players when they complete an advancement.
 */
public class AdvancementListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        val display = event.getAdvancement().getDisplay();
        val player = event.getPlayer();
        if (event.message() == null || display == null) return;

        if (Boolean.FALSE.equals(player.getWorld().getGameRuleValue(GameRule.ANNOUNCE_ADVANCEMENTS))) {
            return;
        }

        final String translationKey;

        switch (display.frame()) {
            case TASK -> translationKey = "chat.type.advancement.task";
            case CHALLENGE -> translationKey = "chat.type.advancement.challenge";
            default -> translationKey = "chat.type.advancement.goal";
        }

        event.message(EssentialsUtil.getPrefix()
                .append(Component.translatable(translationKey, EssentialsUtil.getDisplayName(player),
                                event.getAdvancement().displayName().colorIfAbsent(Colors.TERTIARY))
                        .color(Colors.GRAY)));
    }
}
