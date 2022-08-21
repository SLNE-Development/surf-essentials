package dev.slne.surf.essentials.commands.gamemode;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CreativeCommand extends EssentialsCommand {
    public CreativeCommand(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Check if sender is player
        if (!(sender instanceof Player player)){sender.sendMessage(SurfApi.getPrefix()
                .append(Component.text("You must be a player to execute this command!", SurfColors.ERROR))); return true;}
        //Check args length
        if (args.length > 1){player.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Du darfst maximal einen Spieler angeben!", SurfColors.ERROR))); return true;}
        //Check if player change gamemode for themselves
        if (args.length == 0){
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du hast in den Spielmodus ", SurfColors.SUCCESS))
                    .append(Component.text(player.getGameMode().toString().toLowerCase().translateEscapes(), SurfColors.GOLD))
                    .append(Component.text(" gewechselt!", SurfColors.SUCCESS)));

        }else {
            //Check if target is not null
            if (Bukkit.getPlayerExact(args[0]) == null) {player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Der Spieler existiert nicht!", SurfColors.ERROR))); return true;}
            //Get Player of arg
            Player targetPlayer = Bukkit.getPlayerExact(args[0]);
            //Sets Gamode for player
            targetPlayer.setGameMode(GameMode.CREATIVE);
            //Send scucces message to player
            player.sendMessage(SurfApi.getPrefix()
                    .append(targetPlayer.displayName().color(SurfColors.AQUA))
                    .append(Component.text(" wurde in den Spielmodus ", SurfColors.SUCCESS))
                    .append(Component.text(targetPlayer.getGameMode().toString().toLowerCase(), SurfColors.GOLD))
                    .append(Component.text(" versetzt!", SurfColors.SUCCESS)));
            //Send info message to target player
            targetPlayer.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Dein Spielmodus wurde zu ", SurfColors.INFO))
                    .append(Component.text(targetPlayer.getGameMode().toString().toLowerCase(), SurfColors.GOLD))
                    .append(Component.text(" geändert!", SurfColors.INFO)));
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
}
