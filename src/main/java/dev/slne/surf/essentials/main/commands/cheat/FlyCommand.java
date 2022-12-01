package dev.slne.surf.essentials.main.commands.cheat;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.main.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

        //Set fly mode
        boolean allowFlight = player.getAllowFlight();
        player.setAllowFlight(!allowFlight);

        //messages
        if (allowFlight){
            player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du hast den Fly mode", SurfColors.SUCCESS))
                    .append(Component.text(" verlassen!", SurfColors.GOLD)));
        }else{
            player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du hast den Fly mode", SurfColors.SUCCESS))
                    .append(Component.text(" betreten!", SurfColors.GOLD)));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
