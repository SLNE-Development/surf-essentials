package dev.slne.surf.essentials.utils.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import io.papermc.paper.entity.LookAnchor;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * An argument for a {@link LookAnchor}.
 */
public class LookAnchorArgument extends CustomArgument<LookAnchor, String> {

    /**
     * Constructs a new look anchor argument.
     *
     * @param nodeName The name of the node for this argument
     */
    public LookAnchorArgument(String nodeName) {
        super(new StringArgument(nodeName), info -> {
            try {
                return LookAnchor.valueOf(info.currentInput().toUpperCase()); // try to parse the input as a LookAnchor
            } catch (IllegalArgumentException e) {
                throw CustomArgumentException.fromAdventureComponent(Component.text("Unknown anchor: " + info.currentInput(), Colors.ERROR)); // throw an error if the input is not a LookAnchor
            }
        });
        replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.supplyAsync(() -> Arrays.stream(LookAnchor.values()) // suggest all LookAnchors
                .map(anchor -> anchor.name().toLowerCase())
                .toList())));
    }

    /**
     * Represents an anchor point on an entity.
     */
    public enum Anchor {
        FEET("feet", (pos, entity) -> pos),
        EYES("eyes", (pos, entity) -> pos.add(0, pos.y() + ((entity instanceof LivingEntity livingEntity) ? livingEntity.getEyeHeight() : 0), 0));

        /**
         * A map of all anchors by their id.
         */
        private static final Map<String, Anchor> BY_NAME = EssentialsUtil.make(new HashMap<>(), (map) -> {
            for (Anchor anchor : values()) {
                map.put(anchor.id, anchor);
            }
        });

        private final String id; // The id of this anchor
        private final BiFunction<Location, Entity, Location> transform; // The function to transform a location to this anchor

        /**
         * Constructs a new anchor.
         *
         * @param id     The id of this anchor
         * @param offset The function to transform a location to this anchor
         */
        Anchor(String id, BiFunction<Location, Entity, Location> offset) {
            this.id = id;
            this.transform = offset;
        }

        /**
         * Transforms a location to this anchor.
         *
         * @param entity The entity to transform the location for
         * @return The transformed location
         */
        public Location apply(Entity entity) {
            return this.transform.apply(entity.getLocation(), entity);
        }

        /**
         * Transforms a location to this anchor.
         *
         * @param source The command sender to transform the location for
         * @return The transformed location
         */
        public Location apply(CommandSender source) {
            return !(source instanceof Entity entity) ? EssentialsUtil.getSenderLocation(source) : this.transform.apply(entity.getLocation(), entity);
        }

        /**
         * Gets the id of this anchor.
         *
         * @return The id of this anchor
         */
        public String getId() {
            return id;
        }

        /**
         * Gets the anchor by its id.
         *
         * @param id The id of the anchor
         * @return The optional anchor
         */
        @NotNull
        public static Optional<Anchor> getByName(String id) {
            return Optional.ofNullable(BY_NAME.get(id));
        }
    }
}
