package dev.slne.surf.essentialsold.utils.brigadier;

import com.mojang.brigadier.tree.CommandNode;
import dev.jorel.commandapi.Brigadier;
import dev.jorel.commandapi.CommandAPI;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for unregistering and re-registering vanilla commands in order to recode them.
 */
public class RecodedCommands {
    private final List<CommandNode<?>> REMOVED_COMMAND_NODE = new ArrayList<>();
    private static final List<String> COMMANDS = EssentialsUtil.make(new ArrayList<>(), strings -> {
        strings.add("clear");
        strings.add("defaultgamemode");
        strings.add("deop");
        strings.add("difficulty");
        strings.add("effect");
        strings.add("enchant");
        strings.add("experience");
        strings.add("xp");
        strings.add("fill");
        strings.add("forceload");
        strings.add("gamemode");
        strings.add("give");
        strings.add("kill");
        strings.add("list");
        strings.add("op");
        strings.add("particle");
        strings.add("seed");
        strings.add("setblock");
        strings.add("setworldspawn");
        strings.add("spectate");
        strings.add("summon");
        strings.add("time");
        strings.add("weather");
        strings.add("teleport");
        strings.add("tp");
        strings.add("whitelist");
        strings.add("reload");
        strings.add("function");
        strings.add("setidletimeout");
        strings.add("advancement");
    }).stream().map(o -> o.toString().toLowerCase()).toList();

    /**
     * Constructs a new {@link RecodedCommands} instance.
     *
     */
    public RecodedCommands(){
    }

    /**
     * Unregisters all vanilla commands defined in {@link RecodedCommands#COMMANDS}.
     */
    public void unregisterVanillaCommands(){

        for (String s : COMMANDS) {
            REMOVED_COMMAND_NODE.add(Brigadier.getRootNode().getChild(s));
            CommandAPI.unregister(s, true);
        }
    }

    /**
     * Re-registers all previously removed vanilla commands.
     *
     */
    @SuppressWarnings("unused")
    public synchronized void addVanillaCommands(){

        final var copy = new ArrayList<>(REMOVED_COMMAND_NODE);
        for (CommandNode<?> commandNode : copy) {
            Brigadier.getRootNode().addChild(commandNode);
        }
        REMOVED_COMMAND_NODE.clear();
    }

    /**
     * Gets a list of all commands defined in {@link RecodedCommands#COMMANDS}.
     *
     * @return a list of all recoded commands
     */
    @SuppressWarnings("unused")
    public List<String> getRecodedCommands() {
        return COMMANDS;
    }
}
