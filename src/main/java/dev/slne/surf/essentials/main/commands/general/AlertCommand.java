package dev.slne.surf.essentials.main.commands.general;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AlertCommand extends EssentialsCommand {
    public AlertCommand(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //If sender is Player
        if (sender instanceof Player player) {
            //If the player has not specified an alert message
            if (args.length == 0) {
                player.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Du musst eine Alert-Nachricht angeben!", SurfColors.ERROR)));
                return true;
            }
            //Adds all args to one String
            String alert = "";
            for (int i = 0; i < args.length; i++) {
                alert += args[i] + " ";
            }
            //Sends the alert message
            Bukkit.broadcast(SurfApi.getPrefix()
                    .color(SurfColors.YELLOW)
                    .append(Component.text(ChatColor.translateAlternateColorCodes('&', alert))));

            //Plays notification sound
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                SurfApi.getUser(onlinePlayer).thenAcceptAsync((user) -> {
                    if (user == null) return;
                    user.playSound(Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1);
                });
            }

        //If sender is console
        }else if (sender instanceof ConsoleCommandSender console) {
            //gets the console logger
            ComponentLogger logger = SurfEssentials.getInstance().getComponentLogger();
            //If no alert message was specified
            if (args.length == 0) {
                logger.error(SurfApi.getPrefix()
                        .append(Component.text("You must specify an alert message!", SurfColors.ERROR)));
                return true;
            }
            //Adds all args to one String
            String alert = "";
            for (int i = 0; i < args.length; i++) {
                alert += args[i] + " ";
            }
            //Sends the alert message
            Bukkit.broadcast(SurfApi.getPrefix()
                    .color(SurfColors.YELLOW)
                    .append(Component.text(ChatColor.translateAlternateColorCodes('&', alert))));

            //Plays notification sound
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                SurfApi.getUser(onlinePlayer).thenAcceptAsync((user) -> {
                    if (user == null) return;
                    user.playSound(Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1);
                });
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
