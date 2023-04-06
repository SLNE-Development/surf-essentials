package dev.slne.surf.essentials.utils.nms.brigadier;

import com.mojang.brigadier.tree.CommandNode;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import net.minecraft.commands.CommandSourceStack;

import java.util.ArrayList;
import java.util.List;

public class RecodedCommands {
    List<CommandNode<CommandSourceStack>> REMOVED_COMMAND_NODE = new ArrayList<>();
    private static final List<String> COMMANDS = EssentialsUtil.make(new ArrayList<>(), strings -> {
        strings.add("bossbar");
        strings.add("clear");
        strings.add("damage");
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
        strings.add("gamerule");
        strings.add("give");
        strings.add("help");
        strings.add("kill");
        strings.add("list");
        strings.add("op");
        strings.add("particle");
        strings.add("ride");
        strings.add("seed");
        strings.add("setblock");
        strings.add("setworldspawn");
        strings.add("spectate");
        strings.add("summon");
        strings.add("time");
        strings.add("weather");
        strings.add("teleport");
        strings.add("tp");
    }).stream().map(o -> o.toString().toLowerCase()).toList();

    public RecodedCommands(){
    }

    public synchronized void unregisterVanillaCommands(){
        for (String s : COMMANDS) {
            REMOVED_COMMAND_NODE.add(EssentialsUtil.unregisterDispatcherCommand(s));
        }
    }

    public synchronized void addVanillaCommands(){
        var copy = REMOVED_COMMAND_NODE;
        for (CommandNode<CommandSourceStack> commandNode : copy) {
            EssentialsUtil.registerCommand(commandNode);
        }
        REMOVED_COMMAND_NODE = new ArrayList<>();
    }

    @SuppressWarnings("unused")
    public List<String> getRecodedCommands() {
        return COMMANDS;
    }
}
