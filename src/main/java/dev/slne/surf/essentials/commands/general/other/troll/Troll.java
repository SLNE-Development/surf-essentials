package dev.slne.surf.essentials.commands.general.other.troll;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

public abstract class Troll implements Listener {
    protected final Set<UUID> PLAYER_IN_TROLL = new HashSet<>();
    protected final Map<UUID, Integer> TASK_IDS = new HashMap<>();

    public Troll(){
        SurfEssentials.getInstance().getServer().getPluginManager().registerEvents(this, SurfEssentials.getInstance());
    }

    public abstract String name();
    public abstract String permission();
    protected abstract ArgumentBuilder<CommandSourceStack, ?> troll();

    public LiteralArgumentBuilder<CommandSourceStack> build() {
        final var trollLiteral = Commands.literal(name())
                .requires(EssentialsUtil.checkPermissions(Permissions.TROLL_ALL_PERMISSION, permission()));

        trollLiteral.then(troll());
        return trollLiteral;
    }

    public boolean isInTroll(Player player) {
        return PLAYER_IN_TROLL.contains(player.getUniqueId());
    }

    public boolean getAndToggleTroll(Player player){
        boolean is = isInTroll(player);

        if (is) {
            PLAYER_IN_TROLL.remove(player.getUniqueId());
        } else {
            PLAYER_IN_TROLL.add(player.getUniqueId());
        }

        return is;
    }

    public void stopTroll(Player player){
        final var taskId = TASK_IDS.get(player.getUniqueId());
        if (taskId == null) return;

        Bukkit.getScheduler().cancelTask(taskId);
        TASK_IDS.remove(player.getUniqueId());
    }

    @SuppressWarnings("unused")
    public void stopAllTrolls() {
        TASK_IDS.forEach((uuid, integer) -> {
            Bukkit.getScheduler().cancelTask(integer);
            TASK_IDS.remove(uuid, integer);
        });
    }

}
