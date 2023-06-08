package dev.slne.surf.essentials.utils.abtract;

import dev.slne.surf.essentials.annontations.UpdateRequired;
import dev.slne.surf.essentials.utils.Validate;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.Enchantment;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R1.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A utility class that provides helper methods for working with Bukkit's Craft classes.
 *
 * @author twisti
 * @since 1.0.2
 */
@SuppressWarnings("unused")
@UpdateRequired(minVersion = "1.21", updateReason = "Update imports")
public abstract class CraftUtil {

    /**
     * The name of the NMS class used for the current version of Minecraft.
     */
    @UpdateRequired(minVersion = "1.21", updateReason = "Craft classes change with each minecraft version")
    public static final String NMS_CLASS = "org.bukkit.craftbukkit.v1_20_R1.CraftWorld";

    /**
     * Converts a {@link Player} into a {@link CraftPlayer}.
     *
     * @param player the {@link Player} to convert
     * @return the corresponding {@link CraftPlayer} object
     */
    @Contract(value = "_ -> param1", pure = true)
    public static CraftPlayer toCraftPlayer(Player player) {
        return (CraftPlayer) player;
    }

    /**
     * Converts a {@link Player} into a {@link ServerPlayer}.
     *
     * @param player the {@link Player} to convert
     * @return the corresponding {@link ServerPlayer} object
     */
    public static ServerPlayer toServerPlayer(Player player) {
        return toCraftPlayer(player).getHandle();
    }

    /**
     * Converts a {@link Server} into a {@link CraftServer}.
     *
     * @param server the {@link Server} to convert
     * @return the corresponding {@link CraftServer} object
     */
    @Contract(value = "_ -> param1", pure = true)
    public static CraftServer toCraftServer(Server server) {
        return (CraftServer) server;
    }

    /**
     * Converts a {@link World} into a {@link CraftWorld}.
     *
     * @param world the {@link World} to convert
     * @return the corresponding {@link CraftWorld} object
     */
    @Contract(value = "_ -> param1", pure = true)
    public static CraftWorld toCraftWorld(World world) {
        return ((CraftWorld) world);
    }

    /**
     * Converts a {@link World} into a {@link ServerLevel}.
     *
     * @param world the {@link World} to convert
     * @return the corresponding {@link ServerLevel} object
     */
    public static ServerLevel toServerLevel(World world) {
        return toCraftWorld(world).getHandle();
    }

    /**
     * Converts a {@link Block} into a {@link CraftBlock}.
     *
     * @param block the {@link Block} to convert
     * @return the corresponding {@link CraftBlock} object
     */
    @Contract(value = "_ -> param1", pure = true)
    public static CraftBlock toCraftBlock(Block block) {
        return (CraftBlock) block;
    }

    /**
     * Converts a {@link Block} into a {@link net.minecraft.world.level.block.Block}.
     *
     * @param block the {@link Block} to convert
     * @return the corresponding {@link net.minecraft.world.level.block.Block} object
     */
    public static net.minecraft.world.level.block.@NotNull Block toMinecraftBlock(Block block) {
        return toCraftBlock(block).getNMS().getBlock();
    }

    /**
     * Converts an {@link Entity} into a {@link CraftEntity}.
     *
     * @param entity the {@link Entity} to convert
     * @return the corresponding {@link CraftEntity} object
     */
    @Contract(value = "_ -> param1", pure = true)
    public static CraftEntity toCraftEntity(Entity entity) {
        return (CraftEntity) entity;
    }

    /**
     * Converts an {@link Entity} into an {@link net.minecraft.world.entity.Entity}.
     *
     * @param entity the {@link Entity} to convert
     * @return the corresponding {@link net.minecraft.world.entity.Entity} object
     */
    public static net.minecraft.world.entity.Entity toMinecraftEntity(Entity entity) {
        return toCraftEntity(entity).getHandle();
    }

    /**
     * Converts a {@link Player} into a {@link net.minecraft.world.entity.player.Player}.
     *
     * @param player the {@link Player} to convert
     * @return the corresponding {@link net.minecraft.world.entity.player.Player} object
     */
    public static net.minecraft.world.entity.player.Player toMinecraftPlayer(Player player) {
        return toCraftPlayer(player).getHandle();
    }

    /**
     * Converts a {@link ItemStack} into a {@link CraftItemStack}.
     *
     * @param stack the {@link ItemStack} to convert
     * @return the corresponding {@link CraftItemStack} object
     */
    @Contract(value = "_ -> param1", pure = true)
    public static CraftItemStack toCraftItemStack(ItemStack stack) {
        return ((CraftItemStack) stack);
    }

    /**
     * Converts a {@link ItemStack} into a {@link net.minecraft.world.item.ItemStack}.
     *
     * @param stack the {@link ItemStack} to convert
     * @return the corresponding {@link net.minecraft.world.item.ItemStack} object
     */
    @Contract(pure = true)
    public static net.minecraft.world.item.ItemStack toMinecraftItemStack(ItemStack stack) {
        return toCraftItemStack(stack).handle;
    }

    /**
     * Converts a {@link org.bukkit.enchantments.Enchantment} into a {@link Enchantment}.
     *
     * @param from the {@link org.bukkit.enchantments.Enchantment} to convert
     * @return the corresponding {@link Enchantment} object
     */
    @Contract("null -> null")
    public static Enchantment toMinecraftEnchant(org.bukkit.enchantments.Enchantment from) {
        return CraftEnchantment.getRaw(from);
    }


    /**
     * Sends one or more {@link Packet}s to the client of the given {@link Player}.
     *
     * @param player  the player to send the packets to
     * @param packet  the first packet to send
     * @param packets additional packets to send
     * @param <L>     the type of packet listener
     * @param <P>     the type of packet to send
     * @return the first {@code packet}
     * @see #sendPackets(ServerPlayer player, P packet, P... packets)
     */
    @SuppressWarnings("UnusedReturnValue")
    @Contract("_, _, _ -> param2")
    @SafeVarargs
    public static <L extends PacketListener, P extends Packet<L>> P sendPackets(Player player, @NotNull P packet, @NotNull P... packets) {
        return sendPackets(toServerPlayer(player), packet, packets);
    }


    /**
     * Sends one or more {@link Packet}s to the {@link ServerPlayer}'s client.
     *
     * @param player  the player to send the packets to
     * @param packet  the first packet to send
     * @param packets additional packets to send
     * @param <L>     the type of packet listener
     * @param <P>     the type of packet to send
     * @return the first {@code packet}
     */
    @SafeVarargs
    @Contract("_, _, _ -> param2")
    public static <L extends PacketListener, P extends Packet<L>> P sendPackets(@NotNull ServerPlayer player, @NotNull P packet, @NotNull P... packets) {
        Validate.notNull(player, "Player cannot be null");
        Validate.notNull(packet, "Packet cannot be null");
        Validate.notNull(packets, "Packets cannot be null");

        final var connection = player.connection;

        connection.send(packet);

        for (P p : packets) {
            connection.send(p);
        }

        return packet;
    }

    /**
     * Sends a {@link ClientboundRemoveEntitiesPacket} to remove all entities with the given ids
     *
     * @param player        the {@link Player} to send the packet to
     * @param firstEntityId the first entity ids
     * @param entityIds     the optionally others entity ids
     */
    public static void sendRemoveEntitiesPacket(Player player, int firstEntityId, int... entityIds) {
        final var list = IntArrayList.of(entityIds);
        list.add(firstEntityId);

        sendPackets(player, new ClientboundRemoveEntitiesPacket(list));
    }
}
