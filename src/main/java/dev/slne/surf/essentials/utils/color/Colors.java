package dev.slne.surf.essentials.utils.color;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public interface Colors {
    TextColor PRIMARY = TextColor.fromHexString("#3b92d1");
    TextColor SECONDARY = TextColor.fromHexString("#5b5b5b");
    TextColor TERTIARY = TextColor.fromHexString("#f2c94c");
    TextColor VARIABLE_KEY = TextColor.fromHexString("#3b92d1");
    TextColor VARIABLE_VALUE = TextColor.fromHexString("#f9c353");
    TextColor SPACER = NamedTextColor.GRAY;
    TextColor DARK_SPACER = NamedTextColor.DARK_GRAY;

    TextColor ERROR = TextColor.fromHexString("#ee3d51");
    TextColor SUCCESS = TextColor.fromHexString("#65ff64");
    TextColor WARNING = TextColor.fromHexString("#f9c353");
    TextColor INFO = TextColor.fromHexString("#40d1db");
    TextColor DEBUG = TextColor.fromHexString("#a6c7e6");

    TextColor WHITE = TextColor.fromHexString("#f4f4f4");
    TextColor BLACK = TextColor.fromHexString("#000000");
    TextColor DARK_GRAY = NamedTextColor.DARK_GRAY;
    TextColor GRAY = NamedTextColor.GRAY;
    TextColor DARK_RED = NamedTextColor.DARK_RED;
    TextColor RED = NamedTextColor.RED;
    TextColor DARK_GREEN = NamedTextColor.DARK_GREEN;
    TextColor GREEN = NamedTextColor.GREEN;
    TextColor DARK_AQUA = NamedTextColor.DARK_AQUA;
    TextColor AQUA = NamedTextColor.AQUA;
    TextColor DARK_BLUE = NamedTextColor.DARK_BLUE;
    TextColor BLUE = NamedTextColor.BLUE;
    TextColor DARK_PURPLE = NamedTextColor.DARK_PURPLE;
    TextColor LIGHT_PURPLE = NamedTextColor.LIGHT_PURPLE;
    TextColor GOLD = NamedTextColor.GOLD;
    TextColor YELLOW = NamedTextColor.YELLOW;
}
