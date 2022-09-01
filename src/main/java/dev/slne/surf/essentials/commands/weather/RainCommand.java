package dev.slne.surf.essentials.commands.weather;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RainCommand extends EssentialsCommand {
    public RainCommand(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args){
        //Check if sender is instance of player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("You must be a player to execute this command!")));
            return true;
            //Check if player provided too many args
        }else if (args.length > 0) {
            player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du darfst keine Argumente angeben!")));
            return true;
        }
        //Gets the overworld
        World world = Bukkit.getWorld("world");
        //Change weather to rain
        world.setStorm(true);
        //send success message
        player.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Das Wetter wurde auf ", SurfColors.SUCCESS))
                .append(Component.text("Regen", SurfColors.AQUA))
                .append(Component.text(" gesetzt!", SurfColors.SUCCESS)));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
