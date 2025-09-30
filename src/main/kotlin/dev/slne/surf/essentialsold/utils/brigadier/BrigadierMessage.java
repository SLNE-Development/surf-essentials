package dev.slne.surf.essentialsold.utils.brigadier;

import com.mojang.brigadier.Message;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper class for Brigadier's Message class.
 */
@RequiredArgsConstructor
public final class BrigadierMessage implements Message, ComponentLike {

    @NonNull
    private final Component component; // message

    /**
     * Creates a new BrigadierMessage from a {@link String}.
     *
     * @param string the string to create the message from
     */
    public BrigadierMessage(String string) {
        this.component = LegacyComponentSerializer.legacy('§').deserialize(string);
    }

    public BrigadierMessage(Message message) {
        this.component = LegacyComponentSerializer.builder()
                .extractUrls()
                .hexColors()
                .build().deserialize(message.getString());
    }


    /**
     * Gets the legacy string representation of this message.
     *
     * @return the legacy string representation of this message
     */
    @Override
    public @NotNull String getString() {
        return LegacyComponentSerializer.legacy('§').serialize(component);
    }

    /**
     * {@inheritDoc}
     */
    @Contract(pure = true)
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
}
