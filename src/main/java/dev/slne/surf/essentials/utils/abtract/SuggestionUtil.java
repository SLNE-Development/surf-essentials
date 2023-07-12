package dev.slne.surf.essentials.utils.abtract;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.brigadier.BrigadierMessage;
import dev.slne.surf.essentials.utils.brigadier.Suggestion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * A utility class for handling suggestions.
 *
 * @author twisti
 * @since 1.0.2
 */
public abstract class SuggestionUtil extends OfflineUtil {
    /**
     * A {@link Map} that contains all valid color and formatting codes for Minecraft chat messages.
     */
    private static final Map<String, Component> COLOR_CODES;

    /**
     * Creates a builder offset to display the suggestion at the very end
     * <p></p>
     * See here for an example:
     * <p></p><img src="https://commandapi.jorel.dev/9.0.3/images/emojimsg.gif" alt="Following Suggestions">
     *
     * @param builder the {@link SuggestionsBuilder} to modify
     * @return the modified {@link SuggestionsBuilder}
     */

    public static SuggestionsBuilder followingSuggestionBuilder(@NotNull SuggestionsBuilder builder) {
        return builder.createOffset(builder.getStart() + builder.getRemaining().length());
    }

    /**
     * Suggests all possible color codes to the given {@link SuggestionsBuilder} with the {@link StringArgumentType} input.
     *
     * @param builder the {@link SuggestionsBuilder} to which the color codes will be added
     */
    public static CompletableFuture<Suggestions> suggestAllColorCodes(@NotNull SuggestionsBuilder builder) {
        builder = followingSuggestionBuilder(builder);
        @NotNull SuggestionsBuilder finalBuilder = builder;
        COLOR_CODES.forEach((s, component) -> finalBuilder.suggest(s, new BrigadierMessage(component)));
        return finalBuilder.buildFuture();
    }

    /**
     * Suggests all possible color codes.
     *
     * @return all possible color codes
     */
    public static ArgumentSuggestions<CommandSender> suggestColors() {
        return (info, builder) -> {
            builder = builder.createOffset(builder.getStart() + info.currentArg().length());
            for (Map.Entry<String, Component> entry : COLOR_CODES.entrySet()) {
                builder.suggest(entry.getKey(), new BrigadierMessage(entry.getValue()));
            }
            return builder.buildFuture();
        };
    }

    /**
     * Creates the actual following suggestion using the {@link #followingSuggestionBuilder(SuggestionsBuilder)}
     *
     * @param builder     the {@link SuggestionsBuilder}
     * @param suggestions an array of {@link Suggestion}s to display
     * @return a {@link CompletableFuture<Suggestions>}
     */
    @SuppressWarnings("unused")
    public static CompletableFuture<Suggestions> followingSuggestions(SuggestionsBuilder builder, Suggestion... suggestions) {
        builder = followingSuggestionBuilder(builder);
        for (Suggestion suggestion : suggestions) {
            builder.suggest(suggestion.suggestion(), suggestion.getMinecraftTooltip());
        }
        return builder.buildFuture();
    }


    static {
        // noinspection StaticInitializerReferencesSubClass
        COLOR_CODES = EssentialsUtil.make(new HashMap<>(), map -> {
            map.put("&0", Component.text("Black", Colors.BLACK));
            map.put("&1", Component.text("Dark Blue", Colors.DARK_BLUE));
            map.put("&2", Component.text("Dark Green", Colors.DARK_GREEN));
            map.put("&3", Component.text("Dark Aqua", Colors.DARK_AQUA));
            map.put("&4", Component.text("Dark Red", Colors.DARK_RED));
            map.put("&5", Component.text("Dark Purple", Colors.DARK_PURPLE));
            map.put("&6", Component.text("Gold", Colors.GOLD));
            map.put("&7", Component.text("Gray", Colors.GRAY));
            map.put("&8", Component.text("Dark Gray", Colors.DARK_GRAY));
            map.put("&9", Component.text("Blue", Colors.BLUE));
            map.put("&a", Component.text("Green", Colors.GREEN));
            map.put("&b", Component.text("Aqua", Colors.AQUA));
            map.put("&c", Component.text("Red", Colors.RED));
            map.put("&d", Component.text("Light Purple", Colors.LIGHT_PURPLE));
            map.put("&e", Component.text("Yellow", Colors.YELLOW));
            map.put("&f", Component.text("White", Colors.WHITE));

            map.put("&k", Component.text("Obfuscated", Style.style(TextDecoration.OBFUSCATED)));
            map.put("&l", Component.text("Bold", Style.style(TextDecoration.BOLD)));
            map.put("&m", Component.text("Strikethrough", Style.style(TextDecoration.STRIKETHROUGH)));
            map.put("&n", Component.text("Underline", Style.style(TextDecoration.UNDERLINED)));
            map.put("&o", Component.text("Italic", Style.style(TextDecoration.ITALIC)));
            map.put("&r", Component.text("Reset"));
        });
    }
}