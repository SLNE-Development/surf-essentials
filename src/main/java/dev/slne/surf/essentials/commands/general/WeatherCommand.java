package dev.slne.surf.essentials.commands.general;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.user.SurfUser;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class WeatherCommand extends EssentialsCommand {
    public WeatherCommand(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        World world = Bukkit.getWorlds().get(0);
        if (sender instanceof Player player) {
            //Check if the player provided a weather
            if (args.length == 0) {
                //current weather
                SurfApi.getUser(player).thenAcceptAsync(user -> {
                    if (world.isClearWeather()){
                        currentWeather_DE("Klar", world.getClearWeatherDuration(), user);
                    }else if (world.hasStorm() && !world.isThundering()){
                        currentWeather_DE("Regen", world.getWeatherDuration(), user);
                    }else {
                        currentWeather_DE("Gewitter", world.getWeatherDuration(), user);
                    }
                });
                return true;
            }
            //if the player has specified only one weather
            if (args.length == 1) {
                setWeather(args[0], player, true, null);

                //If the player has specified a duration
            } else {
                //Check if the player provided an integer
                if (!isInt(args[1])) {
                    //not an integer
                    SurfApi.getUser(player).thenAcceptAsync(user -> {
                            user.sendMessage(SurfApi.getPrefix()
                                    .append(Component.text("Du musst eine gültige Zeit in Sekunden angeben!", SurfColors.ERROR)));
                    });
                    return true;
                }
                setWeather(args[0], player, true, Integer.parseInt(args[1]));
            }

        } else if (sender instanceof ConsoleCommandSender console) {
            ComponentLogger logger = SurfEssentials.getInstance().getComponentLogger();
            //Displays the current weather and duration
            if (args.length == 0) {
                if (world.isClearWeather()){
                    currentWeather_EN("clear", world.getClearWeatherDuration(), logger);
                }else if (world.hasStorm() && !world.isThundering()){
                    currentWeather_EN("rain", world.getWeatherDuration(), logger);
                }else {
                    currentWeather_EN("thunder", world.getWeatherDuration(), logger);
                }
                return true;
            }
            //If the sender only specified a weather
            if (args.length == 1) {
                setWeather(args[0], console, false, null);
                return true;
                //If the sender specified a weather duration
            } else {
                //Check if duration is a valid integer
                if (!isInt(args[1])) {
                    logger.warn(Component.text("You must specify a duration in seconds!"));
                    return true;
                }
                setWeather(args[0], console, false, Integer.parseInt(args[1]));
            }
            return true;
        }
        return true;
    }


    @Override
    public @Nullable List < String > onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final String[] WEATHER = {
                "clear",
                "rain",
                "thunder"
        };
        final String[] TIME = {
                "150",
                "300",
                "600",
                "900",
                "1200",
                "1800",
        };
        //create new array
        final List < String > completions = new ArrayList < > ();
        //copy matches of arguments from list
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], List.of(WEATHER), completions);
        } else if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], List.of(TIME), completions);
        }
        //sort the list
        Collections.sort(completions);
        return completions;
    }


    /**
     *
     * Changes the weather with the given parameters.
     *
     * @param weather  the specified weather
     * @param sender  the command sender
     * @param isPlayer  if the sender is a player
     * @param duration  optional duration of the weather
     */
    public void setWeather(String weather, CommandSender sender, Boolean isPlayer, Integer duration) {
        World world = Bukkit.getWorlds().get(0);
        ComponentLogger logger = SurfEssentials.getInstance().getComponentLogger();
        Optional < Integer > dur = Optional.ofNullable(duration);
        int optionalduration = dur.orElse(300);
        optionalduration = optionalduration * 20;

        switch (weather.toLowerCase()) {
            case "clear" -> {
                    world.setClearWeatherDuration(optionalduration);
                if (isPlayer) {
                    weatherSuccess_DE("Klar", sender);
                }
                weatherSuccess_EN("clear", logger);
            }
            case "rain" -> {
                    world.setStorm(true);
                world.setThundering(false);
                world.setWeatherDuration(optionalduration);
                if (isPlayer) {
                    weatherSuccess_DE("Regen", sender);
                }
                weatherSuccess_EN("rain", logger);
            }
            case "thunder" -> {
                    world.setStorm(true);
                world.setThundering(true);
                world.setThunderDuration(optionalduration);
                if (isPlayer) {
                    weatherSuccess_DE("Gewitter", sender);
                }
                weatherSuccess_EN("thunder", logger);
            }
            default -> {
                if (isPlayer) {
                    sender.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Du musst ein gültiges Wetter angeben!", SurfColors.ERROR)));
                    return;
                }
                logger.warn(Component.text("You have to specify a valid weather!", SurfColors.ERROR));
                logger.info(Component.text("Valid weather are: ", SurfColors.DARK_GREEN)
                        .append(Component.text("clear | rain | thunder", SurfColors.GOLD)));

            }
        }
    }

    /**
     *
     * Check if arg is int.
     *
     * @param s  the string to be checked for an int
     */
    public boolean isInt(String s) {
        int i;
        try {
            i = Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            //string is not an integer
            return false;
        }
    }

    /**
     *
     * Success weather change message in german.
     *
     * @param weather  the new weather
     * @param sender  the command sender
     */
    public void weatherSuccess_DE(String weather, CommandSender sender) {
        SurfApi.getUser(sender.getName()).thenAcceptAsync(user -> {
                user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Das Wetter wurde auf ", SurfColors.SUCCESS))
                        .append(Component.text(weather, SurfColors.GOLD))
                        .append(Component.text(" gesetzt!", SurfColors.SUCCESS)));
        });
    }


    /**
     *
     * Success weather change message in english.
     *
     * @param weather  the new weather
     * @param logger the logger
     */
    public void weatherSuccess_EN(String weather, ComponentLogger logger) {
        logger.info(Component.text("The weather was set to ", SurfColors.SUCCESS)
                .append(Component.text(weather, SurfColors.GOLD))
                .append(Component.text("!", SurfColors.SUCCESS)));
    }

    /**
     *
     * Shows what weather and how long is currently in german.
     *
     * @param weather  the current weather
     * @param duration  the duration
     * @param user  the user
     */
    public void currentWeather_DE(String weather, int duration, SurfUser user) {
        user.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Das Wetter ist ", SurfColors.SUCCESS)
                .append(Component.text(weather, SurfColors.GOLD))
                .append(Component.text(" für noch ", SurfColors.SUCCESS))
                .append(Component.text(secondsInFullTime(duration), SurfColors.GOLD))));
    }

    /**
     *
     * Shows what weather and how long is currently in english.
     *
     * @param weather  the current weather
     * @param duration  the duration
     * @param logger  the logger
     */
    public void currentWeather_EN(String weather, int duration, ComponentLogger logger) {
        logger.info(Component.text("The weather is ", SurfColors.SUCCESS)
                .append(Component.text(weather, SurfColors.GOLD))
                .append(Component.text(" for "))
                .append(Component.text(secondsInFullTime(duration), SurfColors.GOLD)));
    }

    /**
     *
     * converts ticks in a time format.
     *
     * @param ticks  the ticks to convert
     * @return Time format in string
     */
    public String secondsInFullTime(int ticks){
        int totalSeconds = ticks/20;
        int hours, minutes, seconds;
        hours = totalSeconds / 3600;
        minutes = (totalSeconds % 3600) / 60;
        seconds = totalSeconds % 60;
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }
}