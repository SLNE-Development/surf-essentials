package dev.slne.surf.essentials.commands.time;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DayCommand extends EssentialsCommand {
    public DayCommand(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Check if sender is a player
        if (!(sender instanceof Player player)) {sender.sendMessage(SurfColors.ERROR + "You must be a player to execute this command!"); return true;}
        //if player provided to many args
        if (args.length > 0){player.sendMessage(SurfApi.getPrefix().append(Component.text("Du darfst keine Argumente angeben!" )).color(SurfColors.ERROR)); return true;}
        Bukkit.getWorld("world").setTime(350);
        player.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Die Zeit wurde auf ", SurfColors.SUCCESS))
                .append(Component.text("Tag", SurfColors.AQUA))
                .append(Component.text(" gesetzt!", SurfColors.SUCCESS)));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
