package dev.slne.surf.essentials.utils.abtract;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class ColorCodes extends OfflineUtil{
    private static final Map<String, Component> COLOR_CODES = EssentialsUtil.make(new HashMap<>(), map -> {
        map.put("&0", Component.literal("Black").withStyle(ChatFormatting.BLACK));
        map.put("&1", Component.literal("Dark Blue").withStyle(ChatFormatting.DARK_BLUE));
        map.put("&2", Component.literal("Dark Green").withStyle(ChatFormatting.DARK_GREEN));
        map.put("&3", Component.literal("Dark Aqua").withStyle(ChatFormatting.DARK_AQUA));
        map.put("&4", Component.literal("Dark Red").withStyle(ChatFormatting.DARK_RED));
        map.put("&5", Component.literal("Dark Purple").withStyle(ChatFormatting.DARK_PURPLE));
        map.put("&6", Component.literal("Gold").withStyle(ChatFormatting.GOLD));
        map.put("&7", Component.literal("Gray").withStyle(ChatFormatting.GRAY));
        map.put("&8", Component.literal("Dark Gray").withStyle(ChatFormatting.DARK_GRAY));
        map.put("&9", Component.literal("Blue").withStyle(ChatFormatting.BLUE));
        map.put("&a", Component.literal("Green").withStyle(ChatFormatting.GREEN));
        map.put("&b", Component.literal("Aqua").withStyle(ChatFormatting.AQUA));
        map.put("&c", Component.literal("Red").withStyle(ChatFormatting.RED));
        map.put("&d", Component.literal("Light Purple").withStyle(ChatFormatting.LIGHT_PURPLE));
        map.put("&e", Component.literal("Yellow").withStyle(ChatFormatting.YELLOW));
        map.put("&f", Component.literal("White").withStyle(ChatFormatting.WHITE));

        map.put("&k", Component.literal("Obfuscated").withStyle(ChatFormatting.OBFUSCATED));
        map.put("&l", Component.literal("Bold").withStyle(ChatFormatting.BOLD));
        map.put("&m", Component.literal("Strikethrough").withStyle(ChatFormatting.STRIKETHROUGH));
        map.put("&n", Component.literal("Underline").withStyle(ChatFormatting.UNDERLINE));
        map.put("&o", Component.literal("Italic").withStyle(ChatFormatting.ITALIC));
        map.put("&r", Component.literal("Reset").withStyle(ChatFormatting.RESET));
    });


    /**
     * Suggests all possible color codes to the given {@link SuggestionsBuilder}.
     *
     * @param builder the {@link SuggestionsBuilder} to which the color codes will be added
     */
    public static void suggestAllColorCodes(@NotNull SuggestionsBuilder builder) {
        COLOR_CODES.forEach(builder::suggest);
        builder.buildFuture();
    }

    /**
     * Suggests all possible color codes to the given {@link SuggestionsBuilder} with the {@link StringArgumentType} input.
     *
     * @param builder the {@link SuggestionsBuilder} to which the color codes will be added
     * @param context the {@link CommandContext<CommandSourceStack>}
     */
    public static void suggestAllColorCodes(@NotNull SuggestionsBuilder builder, @NotNull CommandContext<CommandSourceStack> context) {
        builder = builder.createOffset(builder.getStart() + context.getInput().trim().lastIndexOf(' '));
        COLOR_CODES.forEach(builder::suggest);
        builder.buildFuture();
    }
}