package dev.slne.surf.essentials.listeners;

import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        var message = event.deathMessage();
        if (message == null) return;
        message = message.replaceText(b -> b.matchLiteral(event.getPlayer().getName())
                .replacement(event.getPlayer().displayName()
                        .colorIfAbsent(Colors.TERTIARY)))
                .colorIfAbsent(Colors.INFO);

        event.deathMessage(EssentialsUtil.getPrefix()
                .append(message));
    }
}
