package dev.slne.surf.essentials.main.commands.general.other.world;

import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class WorldChange {
    public static void change(Player player, String[] args){
        if (args.length == 1){//no world to change
            EssentialsUtil.somethingWentWrongAsync_DE(player, "Du musst eine Welt angeben!");
            return;
        }
        World worldToChange = Bukkit.getWorld(args[1]);
        if (worldToChange == null){//no valid world
            EssentialsUtil.somethingWentWrongAsync_DE(player, "Diese Welt existiert nicht oder ist nicht Geladen!");
            return;
        }
        ComponentLogger logger = SurfEssentials.logger();
        if (args.length == 2){//change world for the sender
            WorldCommand.changeWorld(player, worldToChange, logger);
            return;
        }
        Player playerToChange = Bukkit.getPlayerExact(args[2]);
        if (playerToChange == null || !EssentialsUtil.canPlayerSeePlayer(((CraftPlayer) player).getHandle(), ((CraftPlayer) player).getHandle())){//check if specified player is valid
            EssentialsUtil.somethingWentWrongAsync_DE(player, "Dieser Spieler existiert nicht!");
            return;
        }
        WorldCommand.changeWorld(playerToChange, worldToChange, logger);//change world for target player
    }
}
