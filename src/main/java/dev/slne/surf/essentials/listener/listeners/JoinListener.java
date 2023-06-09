package dev.slne.surf.essentials.listener.listeners;

import dev.slne.surf.essentials.utils.EssentialsUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * A listener that handles actions when a player joins the server.
 */
public class JoinListener implements Listener {

    /**
     * Handles the {@link PlayerJoinEvent} and sends commands to the player.
     * Also, if the player has game mode permission, fixes the game mode switcher.
     *
     * @param event the PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final var player = event.getPlayer();
        EssentialsUtil.sendDebug("Sending commands to " + player.getName() + "...");
        EssentialsUtil.sendCommands(player);

        if (EssentialsUtil.hasGameModePermission().test(player)) {
            EssentialsUtil.sendDebug("Fixing game mode switcher for " + player.getName());
            player.sendOpLevel((byte) 2);
        }
    }
}
