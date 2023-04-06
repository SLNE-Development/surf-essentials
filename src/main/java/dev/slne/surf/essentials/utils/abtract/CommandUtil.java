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

public abstract class CommandUtil extends VanishUtil {
    private static final Collection<CommandNode<CommandSourceStack>> REGISTERED_COMMANDS = new ArrayList<>();

    public static CommandDispatcher<CommandSourceStack> getDispatcher(){
        return SurfEssentials.getMinecraftServer().getCommands().getDispatcher();
    }

    public static RootCommandNode<CommandSourceStack> getRoot(){
        return getDispatcher().getRoot();
    }
    public static CommandNode<CommandSourceStack> unregisterDispatcherCommand(String name){
        var command = getRoot().getChild(name);
        sendDebug("Removing command: " + name);
        getRoot().removeCommand(name);
        return command;
    }
    public static Collection<CommandNode<CommandSourceStack>> unregisterDispatcherCommand(Collection<String> names){
        Collection<CommandNode<CommandSourceStack>> unregistered = new HashSet<>();
        for (String name : names) {
            unregistered.add(unregisterDispatcherCommand(name));
        }
        return unregistered;
    }
    public static void syncCommands(){
        sendDebug("Syncing commands...");
        CraftUtil.toCraftServer(SurfEssentials.getInstance().getServer()).syncCommands();
    }
    public static <T extends CommandNode<CommandSourceStack>> void registerCommand(T command){
        getRoot().addChild(command);
        REGISTERED_COMMANDS.add(command);
    }
    @SuppressWarnings("unchecked")
    public static<T extends Collection<CommandNode<CommandSourceStack>>> T getRegisteredCommands(){
        return (T) REGISTERED_COMMANDS;
    }

    public static void sendCommands(Player player){
        SurfEssentials.getMinecraftServer().getCommands().sendCommands(CraftUtil.toServerPlayer(player));
    }

    public static Predicate<CommandSourceStack> checkPermissions(String permission){
        return commandSourceStack -> commandSourceStack.hasPermission(2, permission);
    }
    public static Predicate<CommandSourceStack> checkPermissions(int level, String permission){
        return commandSourceStack -> commandSourceStack.hasPermission(level, permission);
    }


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

    public static MinecraftServer getMinecraftServer() {
        return SurfEssentials.getMinecraftServer();
    }
}
