package dev.slne.surf.essentials.main.commands.tp;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.main.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TeleportToTop extends EssentialsCommand {
    public TeleportToTop(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Check if sender is a player
        if (!(sender instanceof Player player)) {sender.sendMessage(SurfColors.ERROR + "You must be a player to execute this command!"); return true;}
        //if player provided to many args
        if (args.length > 2){player.sendMessage(SurfApi.getPrefix().append(Component.text("Du darfst nur einen Spieler angeben!" )).color(SurfColors.ERROR)); return true;}
        if (args.length == 0) {
            //Teleport Player to the highest Block
            teleportTopBlock(player);
            return true;
        }else {
            //Check if target is not null
            if (Bukkit.getPlayerExact(args[0]) == null) {player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Der Spieler existiert nicht!", SurfColors.ERROR))); return true;}
            //Get Player of arg
            Player targetPlayer = Bukkit.getPlayerExact(args[0]);
            //Teleport target player to top
            teleportTopBlock(targetPlayer);
            // get location and location fragments
            Location loc = targetPlayer.getLocation();

            double x = loc.getX();
            double y = loc.getY();
            double z = loc.getZ();
            // convert to int (information loss)
            int xInt = (int)x;
            int yInt = (int)y;
            int zInt = (int)z;
            //Success message to player
            player.sendMessage(SurfApi.getPrefix()
                    .append(targetPlayer.displayName())
                    .append(Component.text(" wurde zum höchsten Block Teleportiert!", SurfColors.SUCCESS))
                    .append(Component.newline())
                    .append(SurfApi.getPrefix())
                    .append(targetPlayer.displayName().color(SurfColors.BLUE))
                    .append(Component.text(" befindet sich nun bei: " , SurfColors.INFO))
                    .append(Component.text(xInt + " " + yInt + " " + zInt, SurfColors.BLUE))
                    .hoverEvent(Component.text("Klicke um dich zu diesem Spieler zu teleportieren!", SurfColors.INFO))
                    .clickEvent(ClickEvent.suggestCommand("/tp " + player.getName() + " " + targetPlayer.getName())));

            //Info Message to target player
            targetPlayer.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du wurdest zu dem höchsten Block an deiner Position Teleportiert!", SurfColors.INFO)));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Returns a list of online players
        List<String> list = new ArrayList<>();
        if (!(args.length == 1)){return list;}
        for (Player player : Bukkit.getOnlinePlayers()) {
            list.add(player.getName());
        }
        List<String> onlinePlayers = new ArrayList<>();
        String currentarg = args[args.length-1];
        for (String s : list){
            if (s.startsWith(currentarg)){
                onlinePlayers.add(s);
            }
        }
        return onlinePlayers;
    }

    //Get the highest Block & teleport Player to it.
    public static void teleportTopBlock (Player player) {
        Location playerLocation = player.getLocation();
        int topX = playerLocation.getBlockX();
        int topZ = playerLocation.getBlockZ();
        int topY = playerLocation.getWorld().getHighestBlockYAt(topX, topZ);
        player.teleport(new Location(player.getWorld(), topX + 0.5, topY + 1.5, topZ + 0.5, playerLocation.getYaw(), playerLocation.getPitch()));
    }
}

