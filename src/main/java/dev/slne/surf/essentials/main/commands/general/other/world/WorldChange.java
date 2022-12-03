package dev.slne.surf.essentials.main.commands.general.other.world;

import dev.slne.surf.essentials.SurfEssentials;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldChange {
    public static void change(Player player, String[] args){
        if (args.length == 1){//no world to change
            SurfEssentials.somethingWentWrongAsync_DE(player, "Du musst eine Welt angeben!");
            return;
        }
        if (Bukkit.getWorld(args[1]) == null){//no valid world
            SurfEssentials.somethingWentWrongAsync_DE(player, "Diese Welt existiert nicht oder ist nicht Geladen!");
            return;
        }
        ComponentLogger logger = SurfEssentials.logger();
        World worldToChange = Bukkit.getWorld(args[1]);
        if (args.length == 2){//change world for the sender
            WorldCommand.changeWorld(player, worldToChange, logger);
            return;
        }
        if (Bukkit.getPlayerExact(args[2]) == null){//check if specified player is valid
            SurfEssentials.somethingWentWrongAsync_DE(player, "Dieser Spieler existiert nicht!");
            return;
        }
        WorldCommand.changeWorld(Bukkit.getPlayerExact(args[2]), worldToChange, logger);//change world for target player
    }
}
