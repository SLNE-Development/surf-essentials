package dev.slne.surf.essentials.listener.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A listener class that keeps track of the last teleport location of each player.
 */
public class TeleportListener implements Listener {
    /**
     * A map that stores the last teleport location of each player.
     */
    private static final Map<UUID, Location> PLAYER_LOCATION_MAP;

    /**
     * Logs the player teleports.
     *
     * @param event the PlayerTeleportEvent that occurred
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(@NotNull PlayerTeleportEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();

        if (event.getCause() != PlayerTeleportEvent.TeleportCause.COMMAND && event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN)
            return;

        PLAYER_LOCATION_MAP.remove(player.getUniqueId());
        PLAYER_LOCATION_MAP.put(player.getUniqueId(), event.getFrom());
    }

    /**
     * Gets the last teleport location of the specified player.
     *
     * @param player the player whose last teleport location should be retrieved
     * @return an Optional containing the player's last teleport location, or an empty Optional if the player has not teleported yet
     */
    public static Optional<Location> getLastTeleportLocation(Player player) {
        return Optional.ofNullable(PLAYER_LOCATION_MAP.get(player.getUniqueId()));
    }

    static {
        PLAYER_LOCATION_MAP = new HashMap<>();
    }
}
