package dev.slne.surf.essentials.utils.brigadier;

import dev.slne.surf.essentials.utils.EssentialsUtil;

import java.util.ArrayList;
import java.util.List;

public class RecodedCommands {
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
            EssentialsUtil.unregisterDispatcherCommand(s);
        }
    }

    public List<String> getRecodedCommands() {
        return COMMANDS;
    }
}
