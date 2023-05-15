package dev.slne.surf.essentials.utils.abtract;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.nms.brigadier.Suggestion;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * A utility class for handling suggestions.
 * @author twisti
 * @since 1.0.2
 */
public abstract class SuggestionUtil extends OfflineUtil{
    /**
     * A {@link Map} that contains all valid color and formatting codes for Minecraft chat messages.
     */
    private static final Map<String, Component> COLOR_CODES;

    /**
     * Creates a builder offset to display the suggestion at the very end
     * <p></p>
     * See here for an example:
     * <p></p><img src="https://commandapi.jorel.dev/9.0.1/images/emojimsg.gif" alt="Following Suggestions">
     * @param builder the {@link SuggestionsBuilder} to modify
     * @return the modified {@link SuggestionsBuilder}
     */

    public static SuggestionsBuilder followingSuggestionBuilder(@NotNull SuggestionsBuilder builder){
        return builder.createOffset(builder.getStart() + builder.getRemaining().length());
    }

    /**
     * Suggests all possible color codes to the given {@link SuggestionsBuilder} with the {@link StringArgumentType} input.
     *
     * @param builder the {@link SuggestionsBuilder} to which the color codes will be added
     */
    public static CompletableFuture<Suggestions> suggestAllColorCodes(@NotNull SuggestionsBuilder builder) {
        builder = followingSuggestionBuilder(builder);
        COLOR_CODES.forEach(builder::suggest);
        return builder.buildFuture();
    }

    /**
     * Creates the actual following suggestion using the {@link #followingSuggestionBuilder(SuggestionsBuilder)}
     * @param builder the {@link SuggestionsBuilder}
     * @param suggestions an array of {@link Suggestion}s to display
     * @return a {@link CompletableFuture<Suggestions>}
     */
    @SuppressWarnings("unused")
    public static CompletableFuture<Suggestions> followingSuggestions(SuggestionsBuilder builder, Suggestion... suggestions){
        builder = followingSuggestionBuilder(builder);
        for (Suggestion suggestion : suggestions) {
            builder.suggest(suggestion.suggestion(), suggestion.getMinecraftTooltip());
        }
        return builder.buildFuture();
    }




    static {
        // noinspection StaticInitializerReferencesSubClass
        COLOR_CODES = EssentialsUtil.make(new HashMap<>(), map -> {
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
    }
}