package dev.slne.surf.essentials.main.utils;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class EssentialsUtil {
    /**
     *
     * Check if arg is int.
     *
     * @param s  the string to be checked for an int
     */
    public static boolean isInt(@NotNull String s) {
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
     * Simple "No permission" message.
     *
     */
    public static Component NO_PERMISSION(){
        return SurfApi.getPrefix()
                .append(Component.text("You do not have permission to execute this command!", SurfColors.ERROR));
    }

    /**
     *
     * Converts the color from the input string to a gradient.
     *
     * @param input  the string to convert the color from
     * @param firstHex  the first hex color
     * @param secondHex  the second hex color
     */
    public static Component gradientify(@NotNull String input, @NotNull String firstHex, @NotNull String secondHex) {

        TextColor gradientFirst = TextColor.fromHexString(firstHex);
        TextColor gradientSecond = TextColor.fromHexString(secondHex);

        if (gradientFirst == null || gradientSecond == null) {
            return Component.text(input);
        }

        TextComponent.Builder builder = Component.text();
        float step = 1.0f / (input.length() - 1);
        float current = 0.0f;
        for (char c : input.toCharArray()) {
            builder.append(Component.text(c, TextColor.lerp(current, gradientFirst, gradientSecond)));
            current += step;
        }

        return builder.build();
    }

    /**
     *
     * converts ticks in a time format.
     *
     * @param ticks  the ticks to convert
     * @return Time format in string
     */
    public static String ticksToString(int ticks){
        int totalSeconds = ticks / 20;
        int hours, minutes, seconds;
        hours = totalSeconds / 3600;
        minutes = (totalSeconds % 3600) / 60;
        seconds = totalSeconds % 60;
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }

    /**
     *
     * Sends an error message to the sender.
     *
     * @param sender  the sender
     * @param error  the error
     */
    public static void somethingWentWrongAsync_DE(@NotNull Player sender, @NotNull String error){
        SurfApi.getUser(sender).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(gradientify("Es ist ein Fehler aufgetreten:", "#eb3349", "#f45c43"))
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                .append(gradientify(error, "#EA98DA", "#5B6CF9"))));
    }

    /**
     *
     * sorts the tab completion suggestion
     *
     * @param list  the list
     * @param currentarg  the currentarg
     * @param completions  the completions
     */
    public static void sortedSuggestions(@NotNull List<String> list, @NotNull String currentarg, @NotNull List<String> completions){
        for (String s : list) {
            if (s.toLowerCase().startsWith(currentarg)) {
                completions.add(s);
            }
        }
    }

    /**
     * Returns whether the given player is vanished or not.
     * A player is considered vanished if they have a "vanished" metadata value
     * set to `true`.
     *
     * @param player the player to check
     * @return `true` if the player is vanished, `false` otherwise
     */
    public static boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }
}
