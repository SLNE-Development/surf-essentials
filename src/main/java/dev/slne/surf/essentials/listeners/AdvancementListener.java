package dev.slne.surf.essentials.listeners;

import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementListener implements Listener {
    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        var display = event.getAdvancement().getDisplay();
        if (event.message() == null || display == null) return;

        String translationKey;

        switch (display.frame()) {
            case TASK -> translationKey = "chat.type.advancement.task";
            case CHALLENGE -> translationKey = "chat.type.advancement.challenge";
            default -> translationKey = "chat.type.advancement.goal";
        }

        event.message(EssentialsUtil.getPrefix()
                .append(Component.translatable(translationKey, event.getPlayer().displayName().colorIfAbsent(Colors.TERTIARY),
                                event.getAdvancement().displayName().colorIfAbsent(Colors.TERTIARY))
                        .color(Colors.INFO)));
    }
}
