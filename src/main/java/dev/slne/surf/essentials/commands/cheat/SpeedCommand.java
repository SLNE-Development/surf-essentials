package dev.slne.surf.essentials.commands.cheat;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpeedCommand extends EssentialsCommand {
    public SpeedCommand(PluginCommand command) {
        super(command);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                SurfApi.getUser(player).thenAcceptAsync(user -> {
                    user.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Dein Fly/Walk speed ist: ", SurfColors.SUCCESS))
                            .append(Component.text(player.getWalkSpeed(), SurfColors.GOLD)));
                });
                return true;
            }
            if (isFloat(args[0])) {
                player.setFlySpeed(Float.parseFloat(args[0]));
                player.setWalkSpeed(Float.parseFloat(args[0]));
                SurfApi.getUser(player).thenAcceptAsync(user -> {
                    user.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Dein fly/walk speed wurde geändert zu: ", SurfColors.SUCCESS))
                            .append(Component.text(player.getWalkSpeed(), SurfColors.GOLD)));
                });
            } else if (args[0].equalsIgnoreCase("default")) {
                player.setFlySpeed((float) 0.2);
                player.setWalkSpeed((float) 0.2);
                SurfApi.getUser(player).thenAcceptAsync(user -> {
                    user.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Dein fly/walk speed wurde zurückgesetzt!", SurfColors.SUCCESS)));
                });
                return true;
            } else {
                SurfApi.getUser(player).thenAcceptAsync(user -> {
                    user.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Du musst eine gültige Zahl angeben!", SurfColors.ERROR)));
                });
                return true;
            }

        } else if (sender instanceof ConsoleCommandSender console) {
            ComponentLogger logger = SurfEssentials.logger();

            logger.warn(Component.text("You must be a player to execute this command!", SurfColors.ERROR));
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final String[] COMMON_VALUES = {
                "default",
                "0",
                "0.2",
                "0.4",
                "0.6",
                "0.8",
                "1"
        };
        //create new array
        final List < String > completions = new ArrayList< >();
        //copy matches of arguments from list
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], List.of(COMMON_VALUES), completions);
        }
        return completions;
    }

    public boolean isFloat(String input) {
        try {
            Float.parseFloat(input);
            float fullInput = Float.parseFloat(input);
            return !(fullInput > 1);
        }catch (NumberFormatException e){
            return false;
        }
    }

}
