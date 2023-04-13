package dev.slne.surf.essentials.listeners;

import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        EssentialsUtil.sendDebug("Sending commands to " + event.getPlayer().getName() + "...");
        EssentialsUtil.sendCommands(event.getPlayer());

        if (EssentialsUtil.hasGameModePermission().test(event.getPlayer())) {
            var gameModeFixer = new ClientboundEntityEventPacket(
                    EssentialsUtil.toServerPlayer(event.getPlayer()),
                    (byte) 26 // https://wiki.vg/Entity_statuses#Player
            );

            Bukkit.getScheduler().runTaskLater(SurfEssentials.getInstance(), () -> {
                EssentialsUtil.sendDebug("Fixing gameMode switcher for " + event.getPlayer().getName());
                EssentialsUtil.sendPackets(event.getPlayer(), gameModeFixer);
            }, 10L);

        }
    }
}
