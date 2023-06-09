package dev.slne.surf.essentials.listener.listeners;

import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Listens for player death events and modifies the death message to make it more beautiful.
 */
public class DeathListener implements Listener {

    /**
     * Handles a player death event.
     *
     * @param event the player death event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        var message = event.deathMessage();
        if (message == null) return;

        message = message.replaceText(b -> b.matchLiteral(event.getPlayer().getName())
                        .replacement(EssentialsUtil.getDisplayName(event.getPlayer())))
                .colorIfAbsent(Colors.GRAY);

        event.deathMessage(EssentialsUtil.getPrefix()
                .append(message));
    }
}
