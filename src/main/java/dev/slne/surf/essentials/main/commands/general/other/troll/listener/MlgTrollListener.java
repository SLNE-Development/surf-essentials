package dev.slne.surf.essentials.main.commands.general.other.troll.listener;

import dev.slne.surf.essentials.main.commands.general.other.troll.MlgTroll;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MlgTrollListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        MlgTroll.restoreInventoryFromMlgTroll(event.getPlayer());
    }

}
