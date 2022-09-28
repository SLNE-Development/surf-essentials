package dev.slne.surf.essentials.commands.general;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GamemodeCommand extends EssentialsCommand {
    public GamemodeCommand(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            //If the player has not specify any arguments
            if (args.length < 1) {
                //The player must specify a gamemode
                player.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Du musst einen Gamemode angeben!", SurfColors.ERROR)));
                return true;
            }
            //If the player specifies only one gamemode
            if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "survival":
                        changeGamemode(player, GameMode.SURVIVAL);
                        break;
                    case "creative":
                        changeGamemode(player, GameMode.CREATIVE);
                        break;
                    case "adventure":
                        changeGamemode(player, GameMode.ADVENTURE);
                        break;
                    case "spectator":
                        changeGamemode(player, GameMode.SPECTATOR);
                        break;
                    default:
                        player.sendMessage(SurfApi.getPrefix()
                                .append(Component.text("Du musst einen gültigen Gamemode angeben!", SurfColors.ERROR)));
                        break;
                }
                return true;

                //If the player specifies another valid player
            } else if (args.length > 1 && (Bukkit.getPlayerExact(args[1]) != null)) {
                //targetPlayer
                Player targetPlayer = Bukkit.getPlayerExact(args[1]);

                if (args[1].equalsIgnoreCase(targetPlayer.getName())) {
                    switch (args[0].toLowerCase()) {
                        case "survival":
                            changeGamemode(targetPlayer, GameMode.SURVIVAL);
                            break;
                        case "creative":
                            changeGamemode(targetPlayer, GameMode.CREATIVE);
                            break;
                        case "adventure":
                            changeGamemode(targetPlayer, GameMode.ADVENTURE);
                            break;
                        case "spectator":
                            changeGamemode(targetPlayer, GameMode.SPECTATOR);
                            break;
                        default:
                            player.sendMessage(SurfApi.getPrefix()
                                    .append(Component.text("Du musst einen gültigen Gamemode angeben!", SurfColors.ERROR)));
                            break;
                    }
                    return true;
                    //Change gamemode for all online players
                }
            } else if (args[1].equalsIgnoreCase("@a")) {
                switch (args[0].toLowerCase()) {
                    case "survival":
                        changeAllGamemodes(GameMode.SURVIVAL);
                        break;
                    case "creative":
                        changeAllGamemodes(GameMode.CREATIVE);
                        break;
                    case "adventure":
                        changeAllGamemodes(GameMode.ADVENTURE);
                        break;
                    case "spectator":
                        changeAllGamemodes(GameMode.SPECTATOR);
                        break;
                    default:
                        player.sendMessage(SurfApi.getPrefix()
                                .append(Component.text("Du musst einen gültigen Gamemode angeben!", SurfColors.ERROR)));
                        break;
                }
                return true;
            }

            //sender instance of Console
        } else if (sender instanceof ConsoleCommandSender console) {
            //logger of the Console
            ComponentLogger logger = SurfEssentials.getInstance().getComponentLogger();

            //If sender provided too few arguments
            if (args.length < 2) {
                logger.error(Component.text("You must specify a valid player and a valid gamemode!", SurfColors.ERROR));
                logger.info(Component.text("Valid gamemodes are: ", SurfColors.DARK_GREEN)
                        .append(Component.text("adventure | creative | spectator | survival", SurfColors.TERTIARY)));
                return true;
            }
            if (Bukkit.getPlayerExact(args[1]) == null) {
                //Change the gamemode for all online players
                if (args[1].equalsIgnoreCase("@a")) {
                    switch (args[0].toLowerCase()) {
                        case "survival":
                            changeAllGamemodes(GameMode.SURVIVAL);
                            break;
                        case "creative":
                            changeAllGamemodes(GameMode.CREATIVE);
                            break;
                        case "adventure":
                            changeAllGamemodes(GameMode.ADVENTURE);
                            break;
                        case "spectator":
                            changeAllGamemodes(GameMode.SPECTATOR);
                            break;
                        default:
                            logger.error(Component.text("You must specify a valid gamemode!", SurfColors.ERROR));
                            break;
                    }
                    return true;
                //No valid player specified
                }else logger.error(Component.text("You must specify a valid player!", SurfColors.ERROR)); return true;
            }
            //target Player
            Player targetPlayer = Bukkit.getPlayerExact(args[1]);

            switch (args[0].toLowerCase()) {
                case "survival":
                    changeGamemode(targetPlayer, GameMode.SURVIVAL);
                    break;
                case "creative":
                    changeGamemode(targetPlayer, GameMode.CREATIVE);
                    break;
                case "adventure":
                    changeGamemode(targetPlayer, GameMode.ADVENTURE);
                    break;
                case "spectator":
                    changeGamemode(targetPlayer, GameMode.SPECTATOR);
                    break;
                default:
                    logger.error(Component.text("You must specify a valid gamemode!", SurfColors.ERROR));
                    break;
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("survival");
            list.add("creative");
            list.add("spectator");
            list.add("adventure");
            List<String> availableGamemodes = new ArrayList<>();
            String currentarg = args[args.length - 1];
            for (String s : list) {
                if (s.startsWith(currentarg)) {
                    availableGamemodes.add(s);
                }
            }
            return availableGamemodes;
        }else if (args.length == 2){
            List<String> list = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                list.add(onlinePlayer.getName());
            }
            list.add("@a");
            List<String> onlinePlayer = new ArrayList<>();
            String currentarg = args[args.length - 1];
            for (String s : list) {
                if (s.startsWith(currentarg)) {
                    onlinePlayer.add(s);
                }
            }
            return onlinePlayer;
        }
        return null;
    }

    /**
     *
     * Change the gamemode of one Player
     *
     * @param player  Player from which the gamemode is changed
     * @param gameMode  the new gamemode of the player
     */
    public void changeGamemode(Player player, GameMode gameMode){
        player.setGameMode(gameMode);
        //Succes message to player
        player.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Deine Gamemode wurde auf ", SurfColors.SUCCESS))
                .append(Component.text(player.getGameMode().toString(), SurfColors.AQUA))
                .append(Component.text(" gesetzt!", SurfColors.SUCCESS)));

        //message for moderators
        final Component gamemodeChange = SurfApi.getPrefix()
                .append(Component.text(player.getName(), SurfColors.AQUA))
                .append(Component.text(" hat in den Gamemode ", SurfColors.DARK_AQUA))
                .append(Component.text(player.getGameMode().toString(), SurfColors.AQUA))
                .append(Component.text(" gewechselt!", SurfColors.DARK_AQUA));

        Bukkit.broadcast(gamemodeChange, "surf.gamemode.announce");
        //log gamemode change
        SurfEssentials.getInstance().getComponentLogger().info(Component.text("Set ", SurfColors.SUCCESS)
                .append(Component.text(player.getName(), SurfColors.GOLD)
                        .append(Component.text("´s ",SurfColors.GOLD)))
                .append(Component.text("game mode to ", SurfColors.SUCCESS))
                .append(Component.text(gameMode.toString().toLowerCase(), SurfColors.GOLD))
                .append(Component.text("!", SurfColors.SUCCESS)));
    }

    /**
     *
     * Change the gamemode of all online players
     *
     * @param gameMode  the new gamemode of the online players
     */
    public void changeAllGamemodes(GameMode gameMode) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setGameMode(gameMode);
            //succes message
            onlinePlayer.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Deine Gamemode wurde auf ", SurfColors.SUCCESS))
                    .append(Component.text(gameMode.toString(), SurfColors.AQUA))
                    .append(Component.text(" gesetzt!", SurfColors.SUCCESS)));
        }

        //message to moderators in german
        final Component gamemodeChangeAll_de = SurfApi.getPrefix()
                .append(Component.text("Der Gamemode von allen Spielern wurde auf ", SurfColors.AQUA))
                .append(Component.text(gameMode.toString(), SurfColors.DARK_AQUA))
                .append(Component.text(" gesetzt!", SurfColors.AQUA));

        //log message in english
        final Component gamemodeChangeAll_en = Component.text("The gamemode of all players was set to ", SurfColors.AQUA)
                .append(Component.text(gameMode.toString().toLowerCase(), SurfColors.DARK_AQUA))
                .append(Component.text("!", SurfColors.AQUA));

        Bukkit.broadcast(gamemodeChangeAll_de, "surf.gamemode.announce");

        SurfEssentials.getInstance().getComponentLogger().info(gamemodeChangeAll_en);
    }

}
