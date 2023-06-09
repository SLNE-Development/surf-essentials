package dev.slne.surf.essentials.listener.listeners;

import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

/**
 * A {@link Listener} for player advancement events, which sends a chat message to players when they complete an advancement.
 */
public class AdvancementListener implements Listener {
    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        final var display = event.getAdvancement().getDisplay();
        if (event.message() == null || display == null) return;

        final String translationKey;

        switch (display.frame()) {
            case TASK -> translationKey = "chat.type.advancement.task";
            case CHALLENGE -> translationKey = "chat.type.advancement.challenge";
            default -> translationKey = "chat.type.advancement.goal";
        }

        event.message(EssentialsUtil.getPrefix()
                .append(Component.translatable(translationKey, EssentialsUtil.getDisplayName(event.getPlayer()),
                                event.getAdvancement().displayName().colorIfAbsent(Colors.TERTIARY))
                        .color(Colors.GRAY)));
    }
}
