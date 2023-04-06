package dev.slne.surf.essentials.commands.general.other.troll.listener;

import dev.slne.surf.essentials.commands.general.other.troll.trolls.CageTroll;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class CageTrollListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!CageTroll.playersInCage.contains(event.getPlayer().getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (!CageTroll.playersInCage.contains(event.getPlayer().getUniqueId())) return;
        event.setCancelled(true);
    }
}
