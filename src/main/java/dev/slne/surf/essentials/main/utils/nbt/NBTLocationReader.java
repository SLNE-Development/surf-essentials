package dev.slne.surf.essentials.main.utils.nbt;

import dev.slne.surf.essentials.SurfEssentials;
import net.kyori.adventure.nbt.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static net.kyori.adventure.nbt.BinaryTagIO.Compression.GZIP;

public class NBTLocationReader {
    public static void getLocationAsync(String name, NBTCallback<Location> callback, Component playerWasNeverOnlineMessage, Component failedOfflineTeleportMessage){
        Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), () -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(name);
            try {
                Location location = getLocation(player);
                if (location == null){
                    callback.onFail(playerWasNeverOnlineMessage);
                    return;
                }
                callback.onSuccess(location);
            } catch (IOException e){
                callback.onFail(failedOfflineTeleportMessage);
                e.printStackTrace();
            }
        });
    }

    public static void setLocationAsync(String name, Location newLocation, NBTCallback<Boolean> callback, Component failedOfflineTeleportMessage){
        Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), () -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(name);
            try {
                setLocation(player, newLocation);
                callback.onSuccess(true);
            }catch (IOException e){
                e.printStackTrace();
                callback.onFail(failedOfflineTeleportMessage);
            }
        });
    }


    private static Location getLocation(OfflinePlayer player) throws IOException {
        UUID uuid = player.getUniqueId();
        File dataFile = getPlayerFile(uuid);

        if (dataFile == null) return null;
        CompoundBinaryTag tag = BinaryTagIO.unlimitedReader().read(dataFile.toPath(), GZIP);
        ListBinaryTag posTag = tag.getList("Pos");
        ListBinaryTag rotTag = tag.getList("Rotation");
        long worldUUIDMost = tag.getLong("WorldUUIDMost");
        long worldUUIDLeast = tag.getLong("WorldUUIDLeast");

        World world = Bukkit.getWorld(new UUID(worldUUIDMost, worldUUIDLeast));

        return new Location(world, posTag.getDouble(0), posTag.getDouble(1), posTag.getDouble(2), rotTag.getFloat(0), rotTag.getFloat(1));
    }

    private static void setLocation(OfflinePlayer player, Location location) throws IOException{
        UUID uuid = player.getUniqueId();
        File dataFile = getPlayerFile(uuid);

        if (dataFile == null) return;
        CompoundBinaryTag rawTag = BinaryTagIO.unlimitedReader().read(dataFile.toPath(), GZIP);
        CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder().put(rawTag);

        ListBinaryTag.Builder<BinaryTag> posTag = ListBinaryTag.builder();
        posTag.add(DoubleBinaryTag.of(location.getX()));
        posTag.add(DoubleBinaryTag.of(location.getY()));
        posTag.add(DoubleBinaryTag.of(location.getZ()));

        ListBinaryTag.Builder<BinaryTag> rotTag = ListBinaryTag.builder();
        rotTag.add(FloatBinaryTag.of(location.getYaw()));
        rotTag.add(FloatBinaryTag.of(location.getPitch()));

        builder.put("Pos", posTag.build());
        builder.put("Rotation", rotTag.build());

        BinaryTagIO.writer().write(builder.build(), dataFile.toPath(), GZIP);
    }

    private static File getPlayerFile(UUID uuid) {
        for (World world : Bukkit.getWorlds()) {
            File worldFolder = world.getWorldFolder();
            if (!worldFolder.isDirectory()) continue;
            File[] children = worldFolder.listFiles();
            if (children == null) continue;
            for (File file : children) {
                if (!file.isDirectory() || !file.getName().equals("playerdata")) continue;
                return getPlayerFile(file, uuid);
            }
        }
        return null;
    }

    private static File getPlayerFile(File playerDataFolder, UUID uuid) {
        File[] files = playerDataFolder.listFiles();
        if (files == null) return null;
        for (File file : files) {
            if (file.getName().equals(uuid.toString() + ".dat")) return file;
        }
        return null;
    }

    public interface NBTCallback<D> {

        void onSuccess(D data);

        default void onFail(Component message) {}
    }
}
