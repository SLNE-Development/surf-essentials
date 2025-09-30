package dev.slne.surf.essentials.utils.abtract;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import net.kyori.adventure.nbt.*;
import org.bukkit.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static net.kyori.adventure.nbt.BinaryTagIO.Compression.GZIP;

/**
 * Provides utility methods for working with offline players.
 *
 * @author twisti
 * @since 1.0.2
 */
public abstract class OfflineUtil extends MessageUtil {
    private static final String ARG_PLAYER_DATA = "playerdata";
    private static final String ARG_PLAYER_FILE_GAMEMODE = "playerGameType";

    /**
     * Returns the file associated with the specified player UUID.
     *
     * @param uuid the UUID of the player
     * @return the file associated with the player UUID, or null if the player file could not be found
     */
    public static @NotNull Optional<File> getPlayerFile(UUID uuid) {
        for (World world : Bukkit.getWorlds()) {
            final var worldFolder = world.getWorldFolder();
            if (!worldFolder.isDirectory()) continue;

            final var playerDataFolder = new File(worldFolder, ARG_PLAYER_DATA);
            if (!playerDataFolder.isDirectory()) continue;

            final var playerFile = new File(playerDataFolder, uuid.toString() + ".dat");
            if (playerFile.exists()) return Optional.of(playerFile);
        }
        return Optional.empty();
    }

    /**
     * Returns the location of the specified player's last logout location.
     *
     * @param offlinePlayer the OfflinePlayer
     * @return the location of the player's last logout location, or null if the player file could not be found
     * @throws WrapperCommandSyntaxException if a syntax error occurs
     */
    public static @NotNull Location getLocation(@NotNull OfflinePlayer offlinePlayer) throws WrapperCommandSyntaxException {
        final File dataFile = getPlayerFile(offlinePlayer.getUniqueId()).orElseThrow(() -> Exceptions.ERROR_PLAYER_FILE_NOT_FOUND);
        final CompoundBinaryTag tag;

        try {
            tag = BinaryTagIO.unlimitedReader().read(dataFile.toPath(), BinaryTagIO.Compression.GZIP);
        } catch (IOException e) {
            throw Exceptions.ERROR_WHILE_READING_TAG_IO.create(e);
        }

        final ListBinaryTag posTag = tag.getList("Pos");
        final ListBinaryTag rotTag = tag.getList("Rotation");

        final long worldUUIDMost = tag.getLong("WorldUUIDMost");
        final long worldUUIDLeast = tag.getLong("WorldUUIDLeast");

        final World world = Bukkit.getWorld(new UUID(worldUUIDMost, worldUUIDLeast));

        if (world == null) throw Exceptions.ERROR_POSITION_IN_UNLOADED_WORLD.create(offlinePlayer);

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

        if (dataFile.isEmpty()) return;
        final CompoundBinaryTag rawTag;
        try {
            rawTag = BinaryTagIO.unlimitedReader().read(dataFile.get().toPath(), BinaryTagIO.Compression.GZIP);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final var builder = CompoundBinaryTag.builder().put(rawTag);
        final var posTag = ListBinaryTag.builder();
        final var rotTag = ListBinaryTag.builder();

        posTag.add(DoubleBinaryTag.doubleBinaryTag(location.getX()));
        posTag.add(DoubleBinaryTag.doubleBinaryTag(location.getY()));
        posTag.add(DoubleBinaryTag.doubleBinaryTag(location.getZ()));

        rotTag.add(FloatBinaryTag.floatBinaryTag(location.getYaw()));
        rotTag.add(FloatBinaryTag.floatBinaryTag(location.getPitch()));

        builder.put("Pos", posTag.build());
        builder.put("Rotation", rotTag.build());

        final long worldUUIDLeast = location.getWorld().getUID().getLeastSignificantBits();
        final long worldUUIDMost = location.getWorld().getUID().getMostSignificantBits();
        builder.putLong("WorldUUIDLeast", worldUUIDLeast);
        builder.putLong("WorldUUIDMost", worldUUIDMost);

        BinaryTagIO.writer().write(builder.build(), dataFile.get().toPath(), BinaryTagIO.Compression.GZIP);
    }

    /**
     * Sets the specified player's game mode in the specified file.
     * <br>
     * <br>
     * This method is used to set the game mode of an offline player.
     * <br>
     * The specified file must be the player's data file.
     *
     * @param gameMode the game mode to set
     * @param dataFile the file to set the game mode in
     * @throws IOException if an I/O error occurs
     */
    public static void setGameModeInFile(GameMode gameMode, File dataFile) throws IOException {
        CompoundBinaryTag rawTag = BinaryTagIO.unlimitedReader().read(dataFile.toPath(), GZIP);
        CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder().put(rawTag);

        builder.put(ARG_PLAYER_FILE_GAMEMODE, IntBinaryTag.intBinaryTag(gameMode.getValue()));

        BinaryTagIO.writer().write(builder.build(), dataFile.toPath(), GZIP);
    }

    /**
     * Sets the specified player's game mode in the specified file.
     *
     * @param player   the player to set the game mode for
     * @param gameMode the game mode to set
     * @throws IOException if an I/O error occurs
     */
    public static void setOfflineGameMode(OfflinePlayer player, GameMode gameMode) throws WrapperCommandSyntaxException {
        Optional<File> playerFile = getPlayerFile(player.getUniqueId());
        if (playerFile.isEmpty()) return;
        try {
            setGameModeInFile(gameMode, playerFile.get());
        } catch (IOException e) {
            throw Exceptions.FAILED_TO_WRITE_TO_FILE_IO.create(e);
        }
    }
}
