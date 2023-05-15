package dev.slne.surf.essentials.utils.abtract;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import dev.slne.surf.essentials.SurfEssentials;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

/**
 * A utility class for handling commands in Minecraft.
 * @author twisti
 * @since 1.0.2
 */
public abstract class CommandUtil extends VanishUtil {
    private static final Collection<CommandNode<CommandSourceStack>> REGISTERED_COMMANDS = new ArrayList<>();

    /**
     * Returns the {@link CommandDispatcher<CommandSourceStack>} used by the {@link MinecraftServer}.
     *
     * @return the command dispatcher
     */
    public static CommandDispatcher<CommandSourceStack> getDispatcher(){
        return getMinecraftServer().vanillaCommandDispatcher.getDispatcher();
    }

    /**
     * Returns the {@link RootCommandNode<CommandSourceStack>} of the {@link CommandDispatcher<CommandSourceStack>}.
     *
     * @return the root command node
     */
    public static RootCommandNode<CommandSourceStack> getRoot(){
        return getDispatcher().getRoot();
    }

    /**
     * Unregisters a command with the given name from the {@link CommandDispatcher<CommandSourceStack>}.
     *
     * @param name the name of the command to unregister
     * @return the removed command node
     */
    public static CommandNode<CommandSourceStack> unregisterDispatcherCommand(String name){
        var command = getRoot().getChild(name);
        sendDebug("Removing command: " + name);
        getRoot().removeCommand(name);
        return command;
    }

    /**
     * Unregisters a collection of commands with the given names from the {@link CommandDispatcher<CommandSourceStack>}.
     *
     * @param names the names of the commands to unregister
     * @return the removed command nodes
     */
    @SuppressWarnings("UnusedReturnValue")
    public static Collection<CommandNode<CommandSourceStack>> unregisterDispatcherCommand(Collection<String> names){
        Collection<CommandNode<CommandSourceStack>> unregistered = new HashSet<>();
        for (String name : names) {
            unregistered.add(unregisterDispatcherCommand(name));
        }
        return unregistered;
    }

    /**
     * Syncs the commands registered with the {@link CommandDispatcher<CommandSourceStack>}.
     */
    public static void syncCommands(){
        sendDebug("Syncing commands...");
        toCraftServer(SurfEssentials.getInstance().getServer()).syncCommands();
    }

    /**
     * Registers a command with the {@link CommandDispatcher<CommandSourceStack>}.
     *
     * @param command the command node to register
     * @param <T> the type of the command node
     */
    public static <T extends CommandNode<CommandSourceStack>> void registerCommand(T command){
        getRoot().addChild(command);
        REGISTERED_COMMANDS.add(command);
    }

    /**
     * Returns a collection of all registered commands by {@link SurfEssentials}.
     *
     * @param <T> the type of the command node collection
     * @return the collection of registered commands
     */
    @SuppressWarnings("unchecked")
    public static<T extends Collection<CommandNode<CommandSourceStack>>> T getRegisteredCommands(){
        return (T) REGISTERED_COMMANDS;
    }

    /**
     * Sends the current commands to the given player.
     *
     * @param player the player to send the commands to
     */
    public static void sendCommands(Player player){
        SurfEssentials.getMinecraftServer().getCommands().sendCommands(CraftUtil.toServerPlayer(player));
    }

    /**
     * Returns a predicate that checks whether the command source has the given permission.
     *
     * @param permission the permission to check
     * @return the permission predicate
     */
    public static Predicate<CommandSourceStack> checkPermissions(String permission){
        return commandSourceStack -> commandSourceStack.hasPermission(2, permission);
    }

    /**
     * Returns a predicate that checks whether the command source has the given permission <b>or</b> the specified level.
     *
     * @param level the permission level  to check
     * @param permission the permission to check
     * @return the permission predicate
     */
    public static Predicate<CommandSourceStack> checkPermissions(int level, String permission){
        return commandSourceStack -> commandSourceStack.hasPermission(level, permission);
    }

    /**
     * Returns a predicate that checks whether the command source has any of the given permissions.
     *
     * @param permissions the permissions to check
     * @return the permission predicate
     */
    public static Predicate<CommandSourceStack> checkPermissions(String... permissions){
        return commandSourceStack -> {
            for (String permission : permissions) {
                if (commandSourceStack.hasPermission(2, permission)){
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * Returns a predicate that checks whether the command source has any of the given permissions <b>or</b> the specified level.
     *
     * @param level the permission level to check
     * @param permissions the permissions to check
     * @return the permission predicate
     */
    public static Predicate<CommandSourceStack> checkPermissions(int level, String... permissions){
        return commandSourceStack -> {
            for (String permission : permissions) {
                if (commandSourceStack.hasPermission(level, permission)){
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * Returns a predicate that checks whether the command source has <b>all</b> of the given permissions.
     *
     * @param permissions the permissions to check
     * @return the permission predicate
     */
    public static Predicate<CommandSourceStack> checkRequiredPermissions(String... permissions){
        return commandSourceStack -> {
            for (String permission : permissions) {
                if (!commandSourceStack.hasPermission(2, permission)){
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * Returns a predicate that checks whether the command source has all of the given permissions <b>or</b> the specified level.
     *
     * @param level the permission level to check
     * @param permissions the permissions to check
     * @return the permission predicate
     */
    @SuppressWarnings("unused")
    public static Predicate<CommandSourceStack> checkRequiredPermissions(int level, String... permissions){
        return commandSourceStack -> {
            for (String permission : permissions) {
                if (!commandSourceStack.hasPermission(level, permission)){
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * Returns the {@link MinecraftServer} instance.
     *
     * @return the Minecraft server instance
     */
    public static MinecraftServer getMinecraftServer() {
        return SurfEssentials.getMinecraftServer();
    }
}
