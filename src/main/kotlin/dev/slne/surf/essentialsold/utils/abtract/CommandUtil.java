package dev.slne.surf.essentialsold.utils.abtract;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import dev.jorel.commandapi.Brigadier;
import dev.jorel.commandapi.CommandAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * A utility class for handling commands in Minecraft.
 *
 * @author twisti
 * @since 1.0.2
 */
public abstract class CommandUtil extends VanishUtil {
    private static final Collection<CommandNode<?>> REGISTERED_COMMANDS = new ArrayList<>();

    /**
     * Returns the {@link CommandDispatcher}
     *
     * @return the command dispatcher
     */
    public static CommandDispatcher<?> getDispatcher() {
        return Brigadier.getCommandDispatcher();
    }

    /**
     * Returns the {@link RootCommandNode<?>} of the {@link CommandDispatcher<?>}.
     *
     * @return the root command node
     */
    public static RootCommandNode<?> getRoot() {
        return getDispatcher().getRoot();
    }

    /**
     * Sends the current commands to the given player.
     *
     * @param player the player to send the commands to
     */
    public static void sendCommands(Player player) {
        CommandAPI.updateRequirements(player);
    }

    /**
     * Returns a predicate that checks whether the command source has the given permission.
     *
     * @param permission the permission to check
     * @return the permission predicate
     */
    public static Predicate<CommandSender> checkPermissions(String permission) {
        return sender -> sender.hasPermission(permission);
    }

    /**
     * Returns a predicate that checks whether the command source has any of the given permissions.
     *
     * @param permissions the permissions to check
     * @return the permission predicate
     */
    public static Predicate<CommandSender> checkPermissions(String... permissions) {
        return sender -> {
            for (String permission : permissions) {
                if (sender.hasPermission(permission)) {
                    return true;
                }
            }
            return false;
        };
    }
}
