package dev.slne.surf.essentials.utils.color;

import dev.slne.surf.essentials.annontations.UpdateRequired;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.boss.BarColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A collection of predefined text colors for use in styling text.
 */
@SuppressWarnings("unused")
public interface Colors {
    /**
     * The primary text color.
     */
    TextColor PRIMARY = TextColor.fromHexString("#3b92d1");

    /**
     * The secondary text color.
     */
    TextColor SECONDARY = TextColor.fromHexString("#5b5b5b");

    /**
     * The tertiary text color.
     */
    TextColor TERTIARY = TextColor.fromHexString("#f2c94c");


    /**
     * The text color for variable keys.
     */
    TextColor VARIABLE_KEY = TextColor.fromHexString("#3b92d1");

    /**
     * The text color for variable values.
     */
    TextColor VARIABLE_VALUE = TextColor.fromHexString("#f9c353");


    /**
     * The spacer text color.
     */
    TextColor SPACER = NamedTextColor.GRAY;

    /**
     * The dark spacer text color.
     */
    TextColor DARK_SPACER = NamedTextColor.DARK_GRAY;


    /**
     * The error text color.
     */
    TextColor ERROR = TextColor.fromHexString("#ee3d51");

    /**
     * The success text color.
     */
    TextColor SUCCESS = TextColor.fromHexString("#65ff64");

    /**
     * The warning text color.
     */
    TextColor WARNING = TextColor.fromHexString("#f9c353");

    /**
     * The info text color.
     */
    TextColor INFO = TextColor.fromHexString("#40d1db");

    /**
     * The debug text color.
     */
    TextColor DEBUG = TextColor.fromHexString("#a6c7e6");


    /**
     * The white text color.
     */
    TextColor WHITE = TextColor.fromHexString("#f4f4f4");

    /**
     * The black text color.
     */
    TextColor BLACK = TextColor.fromHexString("#000000");

    /**
     * The dark gray text color.
     */
    TextColor DARK_GRAY = NamedTextColor.DARK_GRAY;

    /**
     * The gray text color.
     */
    TextColor GRAY = NamedTextColor.GRAY;

    /**
     * The dark red text color.
     */
    TextColor DARK_RED = NamedTextColor.DARK_RED;

    /**
     * The red text color.
     */
    TextColor RED = NamedTextColor.RED;

    /**
     * The dark green text color.
     */
    TextColor DARK_GREEN = NamedTextColor.DARK_GREEN;

    /**
     * The green text color.
     */
    TextColor GREEN = NamedTextColor.GREEN;

    /**
     * The dark aqua text color.
     */
    TextColor DARK_AQUA = NamedTextColor.DARK_AQUA;

    /**
     * The aqua text color.
     */
    TextColor AQUA = NamedTextColor.AQUA;

    /**
     * The dark blue text color.
     */
    TextColor DARK_BLUE = NamedTextColor.DARK_BLUE;

    /**
     * The blue text color.
     */
    TextColor BLUE = NamedTextColor.BLUE;

    /**
     * The dark purple text color.
     */
    TextColor DARK_PURPLE = NamedTextColor.DARK_PURPLE;

    /**
     * The light purple text color.
     */
    TextColor LIGHT_PURPLE = NamedTextColor.LIGHT_PURPLE;

    /**
     * The gold text color.
     */
    TextColor GOLD = NamedTextColor.GOLD;

    /**
     * The yellow text color.
     */
    TextColor YELLOW = NamedTextColor.YELLOW;

    /**
     * Gets the {@link NamedTextColor} from the {@link BarColor}.
     *
     * @param barColor The {@link BarColor} to convert.
     * @return The {@link NamedTextColor} from the {@link BarColor}.
     */
    @Contract(pure = true)
    @UpdateRequired(updateReason = "Maybe more colors will be added in the future")
    static NamedTextColor convertBossBarColor(@NotNull BarColor barColor) {
        return switch (barColor) {
            case BLUE -> NamedTextColor.BLUE;
            case GREEN -> NamedTextColor.GREEN;
            case PINK -> NamedTextColor.LIGHT_PURPLE;
            case PURPLE -> NamedTextColor.DARK_PURPLE;
            case RED -> NamedTextColor.RED;
            case YELLOW -> NamedTextColor.YELLOW;
            default -> NamedTextColor.WHITE;
        };
    }
}
