package dev.slne.surf.essentials.commands.general;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TimeCommand extends EssentialsCommand {
    public TimeCommand(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //If sender is instance of Player
        if (sender instanceof Player player) {
            //If the player has not specify any arguments
            if (args.length == 0) {
                //The player must specify a time
                player.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Du musst eine Zeit angeben!", SurfColors.ERROR)));
                return true;
            }
            //Gets the player world
            World world = SurfEssentials.getInstance().getServer().getWorlds().get(0);
            //Day
            if (args[0].equalsIgnoreCase("day")) {
                //Sets the time to day
                world.setTime(350);
                //Success message to the player
                player.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Die Zeit in der Welt ", SurfColors.SUCCESS)
                                .append(Component.text(world.getName())
                                        .color(SurfColors.GOLD)))
                        .append(Component.text(" wurde auf ", SurfColors.SUCCESS)
                                .append(Component.text("Tag", SurfColors.GOLD)))
                        .append(Component.text(" gesetzt!", SurfColors.SUCCESS)));
                return true;
            }
            //Evening
            if (args[0].equalsIgnoreCase("evening")) {
                //Sets the time to day
                world.setTime(12350);
                //Success message to the player
                player.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Die Zeit in der Welt ", SurfColors.SUCCESS)
                                .append(Component.text(world.getName())
                                        .color(SurfColors.GOLD)))
                        .append(Component.text(" wurde auf ", SurfColors.SUCCESS)
                                .append(Component.text("Abend", SurfColors.GOLD)))
                        .append(Component.text(" gesetzt!", SurfColors.SUCCESS)));
                return true;
            }
            //night
            if (args[0].equalsIgnoreCase("night")) {
                //Sets the time to day
                world.setTime(13000);
                //Success message to the player
                player.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Die Zeit in der Welt ", SurfColors.SUCCESS)
                                .append(Component.text(world.getName())
                                        .color(SurfColors.GOLD)))
                        .append(Component.text(" wurde auf ", SurfColors.SUCCESS)
                                .append(Component.text("Nacht", SurfColors.GOLD)))
                        .append(Component.text(" gesetzt!", SurfColors.SUCCESS)));
                return true;
            }
            //Midnight
            if (args[0].equalsIgnoreCase("midnight")) {
                //Sets the time to day
                world.setTime(18000);
                //Success message to the player
                player.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Die Zeit in der Welt ", SurfColors.SUCCESS)
                                .append(Component.text(world.getName())
                                        .color(SurfColors.GOLD)))
                        .append(Component.text(" wurde auf ", SurfColors.SUCCESS)
                                .append(Component.text("Mittenacht", SurfColors.GOLD)))
                        .append(Component.text(" gesetzt!", SurfColors.SUCCESS)));
                return true;
            }
            //set time
            if (args[0].equalsIgnoreCase("set")) {
                //Check if player provided enough args
                if (args.length > 1) {
                    //Check if args[1] is an int
                    if (isInt(args[1])) {
                        //Sets the time to the specified number of ticks
                        world.setTime(Integer.parseInt(args[1]));
                        //Success message to player
                        player.sendMessage(SurfApi.getPrefix()
                                .append(Component.text("Die Zeit wurde erfolgreich auf ", SurfColors.SUCCESS))
                                .append(Component.text(args[1], SurfColors.GOLD))
                                .append(Component.text(" Ticks gesetzt!", SurfColors.SUCCESS)));
                        return true;
                        //If args[1] isn´t an int
                    } else {
                        player.sendMessage(SurfApi.getPrefix()
                                .append(Component.text("Du musst eine gültige Zahl angeben!", SurfColors.ERROR)));
                        return true;
                    }
                    //If the player has too few arguments to specify
                } else {
                    player.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Du musst eine gültige Zahl angeben!", SurfColors.ERROR)));
                    return true;
                }
            }

        } else if (sender instanceof ConsoleCommandSender console) {
            ComponentLogger logger = SurfEssentials.getInstance().getComponentLogger();
            //If the no arguments have been provided
            if (args.length == 0) {
                logger.warn(Component.text("You must specify a time!", SurfColors.ERROR));
                logger.warn(Component.text("Valid times are: ", SurfColors.DARK_GREEN)
                        .append(Component.text("day | evening | night | midnight | set <ticks>", SurfColors.SECONDARY)));
                return true;
            }
                //Gets the player world
                World world = console.getServer().getWorlds().get(0);
                //Day
                if (args[0].equalsIgnoreCase("day")) {
                    //Sets the time to day
                    world.setTime(350);
                    //Success message to console
                    logger.info(Component.text("The time in the world ", SurfColors.SUCCESS)
                            .append(Component.text(world.getName())
                                    .color(SurfColors.GOLD))
                            .append(Component.text(" was set to ", SurfColors.SUCCESS)
                                    .append(Component.text("day", SurfColors.GOLD)))
                            .append(Component.text("!", SurfColors.SUCCESS)));
                    return true;
                }
                //Evening
                if (args[0].equalsIgnoreCase("evening")) {
                    //Sets the time to day
                    world.setTime(12350);
                    //Success message to the player
                    logger.info(Component.text("The time in the world ", SurfColors.SUCCESS)
                            .append(Component.text(world.getName())
                                    .color(SurfColors.GOLD))
                            .append(Component.text(" was set to ", SurfColors.SUCCESS)
                                    .append(Component.text("evening", SurfColors.GOLD)))
                            .append(Component.text("!", SurfColors.SUCCESS)));
                    return true;
                }
                //night
                if (args[0].equalsIgnoreCase("night")) {
                    //Sets the time to day
                    world.setTime(13000);
                    //Success message to the player
                    logger.info(Component.text("The time in the world ", SurfColors.SUCCESS)
                            .append(Component.text(world.getName())
                                    .color(SurfColors.GOLD))
                            .append(Component.text(" was set to ", SurfColors.SUCCESS)
                                    .append(Component.text("night", SurfColors.GOLD)))
                            .append(Component.text("!", SurfColors.SUCCESS)));
                    return true;
                }
                //Midnight
                if (args[0].equalsIgnoreCase("midnight")) {
                    //Sets the time to day
                    world.setTime(18000);
                    //Success message to the player
                    logger.info(Component.text("The time in the world ", SurfColors.SUCCESS)
                            .append(Component.text(world.getName())
                                    .color(SurfColors.GOLD))
                            .append(Component.text(" was set to ", SurfColors.SUCCESS)
                                    .append(Component.text("midnight", SurfColors.GOLD)))
                            .append(Component.text("!", SurfColors.SUCCESS)));
                    return true;
                }
                //Set time
                if (args[0].equalsIgnoreCase("set")) {
                    //Check if player provided enough args
                    if (args.length > 1) {
                        //Check if args[1] is an int
                        if (isInt(args[1])) {
                            //Sets the time to the specified number of ticks
                            world.setTime(Integer.parseInt(args[1]));
                            //Success message to player
                            logger.info(Component.text("The time was successfully set to ", SurfColors.SUCCESS)
                                    .append(Component.text(args[1], SurfColors.GOLD))
                                    .append(Component.text(" ticks!", SurfColors.SUCCESS)));
                            return true;
                            //If args[1] isn´t an int
                        } else {
                            logger.warn(Component.text("You must enter a valid number!", SurfColors.ERROR));
                            return true;
                        }
                        //If the sender has too few arguments to specify
                    } else {
                        logger.warn(Component.text("You must enter a valid number!", SurfColors.ERROR));
                        return true;
                    }
                }
                else {
                    logger.warn(Component.text("You must specify a valid time!", SurfColors.ERROR));
                    logger.warn(Component.text("Valid times are: ", SurfColors.DARK_GREEN)
                            .append(Component.text("day | evening | night | midnight | set <ticks>", SurfColors.SECONDARY)));
                    return true;
                }
            }
            return true;
    }

        @Override
        public @Nullable List<String> onTabComplete (@NotNull CommandSender sender, @NotNull Command
        command, @NotNull String label, @NotNull String[]args){
            //Returns a list of available times
            List<String> list = new ArrayList<>();
            if (!(args.length == 1)) {
                return list;
            }
            list.add("day");
            list.add("evening");
            list.add("night");
            list.add("midnight");
            list.add("set");
            List<String> allowedTime = new ArrayList<>();
            String currentarg = args[args.length - 1];
            for (String s : list) {
                if (s.startsWith(currentarg)) {
                    allowedTime.add(s);
                }
            }
            return allowedTime;
        }

        //Check if arg is int
        public boolean isInt(String s){
            int i;
            try {
                i = Integer.parseInt(s);
                return true;
            } catch (NumberFormatException ex) {
                //string is not an integer
                return false;
            }
        }

    }
