package dev.slne.surf.essentials.commands.general.other.troll;

import dev.jorel.commandapi.CommandTree;
import dev.slne.surf.essentials.SurfEssentials;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.UUID;

public abstract class TrollSubcommand extends CommandTree implements Listener {

    private final String trollName;
    private final Object2IntMap<UUID> trollTaskMap = Object2IntMaps.synchronize(new Object2IntOpenHashMap<>());

    public TrollSubcommand(String trollName) {
        super(trollName);
        this.trollName = trollName;

        Bukkit.getPluginManager().registerEvents(this, SurfEssentials.getInstance());
    }
}
