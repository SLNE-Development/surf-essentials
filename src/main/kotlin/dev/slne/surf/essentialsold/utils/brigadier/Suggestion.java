package dev.slne.surf.essentialsold.utils.brigadier;

import com.mojang.brigadier.Message;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Creates a suggestion
 *
 * @param suggestion the actual text to display
 * @param tooltip    the tooltip when the user hovers over a suggestion
 */
@SuppressWarnings("unused")
public record Suggestion(@NotNull String suggestion, @NotNull Optional<Component> tooltip) {
    /**
     * Creates a suggestion without tooltip
     *
     * @param suggestion the text to suggest
     */
    public Suggestion(@NotNull String suggestion) {
        this(suggestion, Optional.empty());
    }

    /**
     * Gets the tooltip as a vanilla Component
     *
     * @return the vanilla tooltip
     */
    @Contract(pure = true)
    public Message getMinecraftTooltip() {
        return new BrigadierMessage(tooltip.orElse(Component.empty()));
    }
}
