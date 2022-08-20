package dev.slne.surf.essentials.commands.cheat;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlyCommand extends EssentialsCommand {
    public FlyCommand(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Check if sender is a player
        if (!(sender instanceof Player)) {sender.sendMessage(SurfColors.ERROR + "You must be a player to execute this command!"); return true;}
        //Player declaration
        Player player = (Player) sender;
        //if player provided to many args
        if (args.length > 0){player.sendMessage(SurfApi.getPrefix().append(Component.text("Du darfst keine Argumente angeben!" )).color(SurfColors.ERROR)); return true;}
        //Check if player is in fly
        if (players_in_fly_mode.contains(player.getUniqueId())){
            //Disable fly mode
            player.setAllowFlight(false);
            //Remove player from list
            players_in_fly_mode.remove(player.getUniqueId());
            //Send Success Message
            player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du hast den Fly mode" ))
                    .color(SurfColors.SUCCESS)
                    .append(Component.text(" verlassen!"))
                    .color(SurfColors.GOLD));
        //Set player to fly mode
        }else if (!(players_in_fly_mode.contains(player.getUniqueId()))){
            //Allow Flight
            player.setAllowFlight(true);
            //Add player to list
            players_in_fly_mode.add(player.getUniqueId());
            //Send Success Message
            player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du hast den Fly mode" ))
                    .color(SurfColors.SUCCESS)
                    .append(Component.text(" betreten!"))
                    .color(SurfColors.GOLD));

            return true;
        }
        return true;
    } private List<UUID> players_in_fly_mode = new ArrayList<>();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
