package dev.slne.surf.essentials.listeners;

import dev.slne.surf.essentials.utils.EssentialsUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;

public class TeleportListener implements Listener {
    private static final Map<Player, Location> playerTeleportLocationMap = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();

        if (EssentialsUtil.isVanished(player)) return;
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.COMMAND && event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) return;

        playerTeleportLocationMap.remove(player);
        playerTeleportLocationMap.put(player, event.getFrom());
    }

    public static Location getLastTeleportLocationOrNull(Player player){
        return playerTeleportLocationMap.get(player);
    }
}
