package dev.slne.surf.essentials.main.listeners;

import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {
    //TODO: fire the event in the tp command
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (EssentialsUtil.isVanished(player)) return;

        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        Vec3 vec3 = serverPlayer.position();

        serverPlayer.getLevel().sendParticles(serverPlayer, ParticleTypes.AMBIENT_ENTITY_EFFECT, false, vec3.x(), vec3.y(), vec3.z(), 4000, 0.5, 1, 0.5, 1);
    }
}
