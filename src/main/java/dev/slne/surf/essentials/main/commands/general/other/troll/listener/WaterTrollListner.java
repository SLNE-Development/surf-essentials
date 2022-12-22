package dev.slne.surf.essentials.main.commands.general.other.troll.listener;

import dev.slne.surf.essentials.main.commands.general.other.troll.WaterTroll;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WaterTrollListner implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!WaterTroll.playersInTroll.contains(player)) return;
        if (player.getLocation().getBlock().getType() != Material.WATER) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*4, 1, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 20, 1, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20, 15, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20, 15, false, false, false));

    }
}
