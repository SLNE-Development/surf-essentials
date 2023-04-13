package dev.slne.surf.essentials.utils.abtract;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class CraftUtil {
    public static final String NMS_CLASS = "org.bukkit.craftbukkit.v1_19_R3.CraftWorld";

    @Contract(value = "_ -> param1", pure = true)
    public static CraftPlayer toCraftPlayer(Player player){
        return (CraftPlayer) player;
    }

    public static ServerPlayer toServerPlayer(Player player){
        return toCraftPlayer(player).getHandle();
    }

    @Contract(value = "_ -> param1", pure = true)
    public static CraftServer toCraftServer(Server server){
        return (CraftServer) server;
    }

    @Contract(value = "_ -> param1", pure = true)
    public static CraftWorld toCraftWorld(World world){
        return ((CraftWorld) world);
    }

    public static ServerLevel toServerLevel(World world){
        return toCraftWorld(world).getHandle();
    }

    public static CraftBlock toCraftBlock(Block block){
        return (CraftBlock) block;
    }

    public static net.minecraft.world.level.block.Block toMinecraftBlock(Block block){
        return toCraftBlock(block).getNMS().getBlock();
    }

    public static CraftEntity toCraftEntity(Entity entity){
        return (CraftEntity) entity;
    }

    public static net.minecraft.world.entity.Entity toMinecraftEntity(Entity entity){
        return toCraftEntity(entity).getHandle();
    }

    public static net.minecraft.world.entity.player.Player toMinecraftPlayer(Player player){
        return toCraftPlayer(player).getHandle();
    }

    @SafeVarargs
    public static<T extends Player, L extends PacketListener,  P extends Packet<L>> void sendPackets(T player, P @NotNull ... packets){
        var connection = toServerPlayer(player).connection;
        for (P packet : packets) {
            connection.send(packet);
        }
    }

    public static CraftItemStack toCraftItemStack(ItemStack stack){
        return ((CraftItemStack) stack);
    }

    public static net.minecraft.world.item.ItemStack toMinecraftItemStack(ItemStack stack){
        return toCraftItemStack(stack).handle;
    }

}
