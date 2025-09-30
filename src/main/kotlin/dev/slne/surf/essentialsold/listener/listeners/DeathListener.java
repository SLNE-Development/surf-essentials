package dev.slne.surf.essentialsold.listener.listeners;

import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import lombok.val;
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
        val message = event.deathMessage();
        if (message == null) return;

        event.deathMessage(EssentialsUtil.getPrefix()
                .append(EssentialsUtil.replaceEntityName(message, event.getEntity(), Colors.GRAY)));
    }
}
