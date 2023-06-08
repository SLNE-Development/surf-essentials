package dev.slne.surf.essentials.utils.abtract;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;

/**
 * A utility class for logging with {@link net.coreprotect.CoreProtect}.
 *
 * @author twisti
 * @since 1.0.2
 */
public abstract class LoggingUtil extends PluginUtil {
    /**
     * Logs a block change in {@link net.coreprotect.CoreProtect} if the plugin is enabled.
     *
     * @param source the source of the block change
     * @param level  the level in which the block change occurred
     * @param pos    the position of the block that was changed
     * @param state  the new state of the block
     * @return true if the block change was logged successfully, false otherwise
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean logBlockChange(CommandSourceStack source, ServerLevel level, BlockPos pos, BlockState state) {
        if (!isCoreProtectEnabled()) return false;

        return getCoreProtectAPI().logPlacement(
                source.getTextName(),
                new Location(level.getWorld(), pos.getX(), pos.getY(), pos.getZ()),
                state.getBukkitMaterial(),
                state.createCraftBlockData()
        );
    }
}
