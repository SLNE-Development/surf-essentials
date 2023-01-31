package dev.slne.surf.essentials.main.listeners;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;

public class TeleportListener implements Listener {
    private static final Map<Player, Location> playerTeleportLocationMap = new HashMap<>();

    //TODO: fire the event in the tp command
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (EssentialsUtil.isVanished(player)) return;
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.COMMAND && event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) return;

        playerTeleportLocationMap.remove(player);
        playerTeleportLocationMap.put(player, event.getFrom());

        if (player.getGameMode() == GameMode.SPECTATOR) return;

        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        Vec3 vec3 = new Vec3(event.getTo().getX(), event.getTo().getY(), event.getTo().getZ());

        serverPlayer.getLevel().sendParticles(serverPlayer, ParticleTypes.END_ROD, true, vec3.x(), vec3.y(), vec3.z(), 700, 0.5, 1, 0.5, 0.1);
        SurfApi.getUser(player).thenAcceptAsync(user -> user.playSound(Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1f, 1f));
    }

    public static Location getLastTeleportLocationOrNull(Player player){
        return playerTeleportLocationMap.get(player);
    }
}
