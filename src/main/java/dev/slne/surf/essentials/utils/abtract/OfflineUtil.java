package dev.slne.surf.essentials.utils.abtract;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.kyori.adventure.nbt.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Provides utility methods for working with offline players.
 *
 * @author twisti
 * @since 1.0.2
 */
public abstract class OfflineUtil extends MessageUtil {
    /**
     * Represents an error that occurs when a player's position is in an unloaded world.
     */
    public static final DynamicCommandExceptionType ERROR_POSITION_IN_UNLOADED_WORLD;

    /**
     * Returns the file associated with the specified player UUID.
     *
     * @param uuid the UUID of the player
     * @return the file associated with the player UUID, or null if the player file could not be found
     */
    public static @Nullable File getPlayerFile(UUID uuid) {
        for (World world : Bukkit.getWorlds()) {
            final var worldFolder = world.getWorldFolder();
            if (!worldFolder.isDirectory()) continue;

            final var playerDataFolder = new File(worldFolder, "playerdata");
            if (!playerDataFolder.isDirectory()) continue;

            final var playerFile = new File(playerDataFolder, uuid.toString() + ".dat");
            if (playerFile.exists()) return playerFile;
        }
        return null;
    }

    /**
     * Returns the location of the specified player's last logout location.
     *
     * @param gameProfile the GameProfile of the player
     * @return the location of the player's last logout location, or null if the player file could not be found
     * @throws IOException            if an I/O error occurs
     * @throws CommandSyntaxException if a syntax error occurs
     */
    public static @Nullable Location getLocation(@NotNull GameProfile gameProfile) throws IOException, CommandSyntaxException {
        final var dataFile = getPlayerFile(gameProfile.getId());

        if (dataFile == null) return null;
        final var tag = BinaryTagIO.unlimitedReader().read(dataFile.toPath(), BinaryTagIO.Compression.GZIP);
        final var posTag = tag.getList("Pos");
        final var rotTag = tag.getList("Rotation");

        final long worldUUIDMost = tag.getLong("WorldUUIDMost");
        final long worldUUIDLeast = tag.getLong("WorldUUIDLeast");

        final var world = Bukkit.getWorld(new UUID(worldUUIDMost, worldUUIDLeast));

        if (world == null) throw ERROR_POSITION_IN_UNLOADED_WORLD.create(gameProfile);

        return new Location(world, posTag.getDouble(0), posTag.getDouble(1), posTag.getDouble(2), rotTag.getFloat(0), rotTag.getFloat(1));
    }

    /**
     * Sets the specified player's last logout location to the specified location.
     *
     * @param uuid     the UUID of the player
     * @param location the location to set as the player's last logout location
     * @throws IOException if an I/O error occurs
     */
    public static void setLocation(UUID uuid, Location location) throws IOException {
        final var dataFile = getPlayerFile(uuid);

        if (dataFile == null) return;
        final var rawTag = BinaryTagIO.unlimitedReader().read(dataFile.toPath(), BinaryTagIO.Compression.GZIP);
        final var builder = CompoundBinaryTag.builder().put(rawTag);
        final var posTag = ListBinaryTag.builder();
        final var rotTag = ListBinaryTag.builder();

        posTag.add(DoubleBinaryTag.of(location.getX()));
        posTag.add(DoubleBinaryTag.of(location.getY()));
        posTag.add(DoubleBinaryTag.of(location.getZ()));

        rotTag.add(FloatBinaryTag.of(location.getYaw()));
        rotTag.add(FloatBinaryTag.of(location.getPitch()));

        builder.put("Pos", posTag.build());
        builder.put("Rotation", rotTag.build());

        final long worldUUIDLeast = location.getWorld().getUID().getLeastSignificantBits();
        final long worldUUIDMost = location.getWorld().getUID().getMostSignificantBits();
        builder.putLong("WorldUUIDLeast", worldUUIDLeast);
        builder.putLong("WorldUUIDMost", worldUUIDMost);

        BinaryTagIO.writer().write(builder.build(), dataFile.toPath(), BinaryTagIO.Compression.GZIP);
    }

    static {
        ERROR_POSITION_IN_UNLOADED_WORLD = new DynamicCommandExceptionType(gameProfile ->
                net.minecraft.network.chat.Component.literal(((GameProfile) gameProfile).getName() + " has logged out in an unloaded world."));
    }
}
