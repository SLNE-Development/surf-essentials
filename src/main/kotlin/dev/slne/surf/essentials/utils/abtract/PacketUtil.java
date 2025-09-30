package dev.slne.surf.essentials.utils.abtract;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A utility class that provides helper methods for working with Bukkit's Craft classes.
 *
 * @author twisti
 * @since 1.0.2
 */
public abstract class PacketUtil {
    private static final PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();

    /**
     * Sends one or more {@link PacketWrapper<?>}s to the client of the given {@link Player}.
     *
     * @param player  the player to send the packets to
     * @param packet  the first packet to send
     * @param packets additional packets to send
     * @param <P>     the type of packet to send
     */
    @SuppressWarnings("UnusedReturnValue")
    @SafeVarargs
    public static <P extends PacketWrapper<?>> void sendPackets(Player player, @NotNull P packet, @NotNull P... packets) {
        playerManager.sendPacket(player, packet);
        for (P p : packets) {
            playerManager.sendPacket(player, p);
        }
    }

    /**
     * Sends a {@link WrapperPlayServerDestroyEntities} to remove all entities with the given ids
     *
     * @param player    the {@link Player} to send the packet to
     * @param entityIds the optionally others entity ids
     */
    public static void sendRemoveEntitiesPacket(Player player, int... entityIds) {
        sendPackets(player, new WrapperPlayServerDestroyEntities(entityIds));
    }
}
