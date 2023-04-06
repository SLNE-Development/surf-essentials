package dev.slne.surf.essentials.utils.abtract;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.kyori.adventure.nbt.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public abstract class OfflineUtil extends MessageUtil{
    public static final DynamicCommandExceptionType ERROR_POSITION_IN_UNLOADED_WORLD = new DynamicCommandExceptionType(gameProfile ->
            net.minecraft.network.chat.Component.literal(((GameProfile) gameProfile).getName() + " has logged out in an unloaded world."));

    public static File getPlayerFile(UUID uuid) {
        for (World world : Bukkit.getWorlds()) {
            File worldFolder = world.getWorldFolder();
            if (!worldFolder.isDirectory()) continue;
            File playerDataFolder = new File(worldFolder, "playerdata");
            if (!playerDataFolder.isDirectory()) continue;
            File playerFile = new File(playerDataFolder, uuid.toString() + ".dat");
            if (playerFile.exists()) return playerFile;
        }
        return null;
    }

    public static Location getLocation(GameProfile gameProfile) throws IOException, CommandSyntaxException {
        File dataFile = getPlayerFile(gameProfile.getId());

        if (dataFile == null) return null;
        CompoundBinaryTag tag = BinaryTagIO.unlimitedReader().read(dataFile.toPath(), BinaryTagIO.Compression.GZIP);
        ListBinaryTag posTag = tag.getList("Pos");
        ListBinaryTag rotTag = tag.getList("Rotation");

        long worldUUIDMost = tag.getLong("WorldUUIDMost");
        long worldUUIDLeast = tag.getLong("WorldUUIDLeast");

        World world = Bukkit.getWorld(new UUID(worldUUIDMost, worldUUIDLeast));

        if (world == null) throw ERROR_POSITION_IN_UNLOADED_WORLD.create(gameProfile);

        return new Location(world, posTag.getDouble(0), posTag.getDouble(1), posTag.getDouble(2), rotTag.getFloat(0), rotTag.getFloat(1));
    }

    public static void setLocation(UUID uuid, Location location) throws IOException{
        File dataFile = getPlayerFile(uuid);

        if (dataFile == null) return;
        CompoundBinaryTag rawTag = BinaryTagIO.unlimitedReader().read(dataFile.toPath(), BinaryTagIO.Compression.GZIP);
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

        long worldUUIDLeast = location.getWorld().getUID().getLeastSignificantBits();
        long worldUUIDMost = location.getWorld().getUID().getMostSignificantBits();
        builder.putLong("WorldUUIDLeast", worldUUIDLeast);
        builder.putLong("WorldUUIDMost", worldUUIDMost);

        BinaryTagIO.writer().write(builder.build(), dataFile.toPath(), BinaryTagIO.Compression.GZIP);
    }
}
