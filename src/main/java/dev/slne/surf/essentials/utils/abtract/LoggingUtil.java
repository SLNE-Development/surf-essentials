package dev.slne.surf.essentials.utils.abtract;

import io.papermc.paper.math.Position;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

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
     * @param sender   the source of the block change
     * @param level    the level in which the block change occurred
     * @param pos      the position of the block that was changed
     * @param state    the new state of the block
     * @param <Sender> the type of the sender
     * @param <Level>  the type of the level
     * @param <Pos>    the type of the position
     * @param <State>  the type of the state
     * @return true if the block change was logged successfully, false otherwise
     */
    @SuppressWarnings("UnusedReturnValue")
    public static <Sender extends CommandSender, Level extends World, Pos extends Position, State extends org.bukkit.block.BlockState>
    boolean logBlockChange(Sender sender, Level level, Pos pos, State state) {
        if (!isCoreProtectEnabled()) return false;

        return getCoreProtectAPI().logPlacement(
                sender.getName(),
                new Location(level, pos.blockX(), pos.blockY(), pos.blockZ()),
                state.getType(),
                state.getBlockData()
        );
    }
}
