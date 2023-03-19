package dev.slne.surf.essentials.commands.general.other.troll.listener;

import dev.slne.surf.essentials.commands.general.other.troll.trolls.MlgTroll;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MlgTrollListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        MlgTroll.restoreInventoryFromMlgTroll(event.getPlayer());
    }

}
