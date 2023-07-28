package dev.slne.surf.essentials.utils.abtract;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

/**
 * This abstract class provides utility methods related to player vanishing and entity visibility.
 *
 * @author twisti
 * @since 1.0.2
 */
public abstract class VanishUtil extends SuggestionUtil {

    /**
     * Returns whether the given player is vanished or not.
     * A player is considered vanished if they have a "vanished" metadata value
     * set to `true`.
     *
     * @param player the player to check
     * @return `true` if the player is vanished, `false` otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isVanished(@NotNull Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

    /**
     * Checks whether a {@link Player} can see another {@link Player}.
     *
     * @param player        the {@link Player} who is checking visibility
     * @param playerToCheck the {@link Player} to check visibility against
     * @return `true` if `player` can see `playerToCheck`, `false` otherwise
     */
    public static boolean canPlayerSeePlayer(@NotNull Player player, @NotNull Player playerToCheck) {
        if (!isVanished(playerToCheck)) return true;
        return player.canSee(playerToCheck);
    }


    /**
     * Checks whether a command source can see an {@link org.bukkit.entity.Entity}.
     *
     * @param source the command source to check
     * @param entity the entity to check
     * @return `true` if `source` can see `entity`, `false` otherwise
     */
    @SuppressWarnings("UnstableApiUsage")
    public static boolean canSourceSeeEntity(@NotNull CommandSender source, @NotNull org.bukkit.entity.Entity entity) {
        if (source instanceof Player player) {
            if (entity instanceof Player other) {
                return canPlayerSeePlayer(player, other);
            } else {
                return player.canSee(entity);
            }
        }
        return true;
    }

    /**
     * Checks whether the {@link Player}s in the provided collection are visible to the source who executed the command.
     *
     * @param source  the command source
     * @param targets the collection of {@link Player}s to check
     * @param <T>     a subtype of Collection that extends {@link Player}
     * @return the collection of {@link Player}s that are visible to the command source
     * @throws WrapperCommandSyntaxException if no {@link Player}s were found that are visible to the command source
     */
    @Contract("_, _ -> param2")
    public static <T extends Collection<Player>> T checkPlayerSuggestion(CommandSender source, T targets) throws WrapperCommandSyntaxException {
        if (!(source instanceof Player player)) return targets;

        for (Player target : targets) {
            if (canPlayerSeePlayer(player, target)) continue;
            targets.remove(target);
        }
        if (targets.size() == 0) {
            throw Exceptions.NO_PLAYERS_FOUND;
        }
        return targets;
    }

    /**
     * Checks whether the {@link Player}s in the provided collection are visible to the source who executed the command.
     *
     * @param source  the command source
     * @param targets the collection of {@link Player}s to check
     * @param <T>     a subtype of Collection that extends {@link Player}
     * @return the collection of {@link Player} that are visible to the command source
     */
    @Contract("_, _ -> param2")
    public static <T extends Collection<Player>> T checkPlayerSuggestionWithoutException(CommandSender source, T targets) {
        try {
            checkEntitySuggestion(source, targets);
        } catch (WrapperCommandSyntaxException ignored) {
        }

        return targets;
    }

    /**
     * Checks whether the {@link org.bukkit.entity.Entity}´s in the provided collection are visible to the player who executed the command.
     *
     * @param source  the command source
     * @param targets the collection of {@link org.bukkit.entity.Entity}´s to check
     * @param <E>     a subtype of Entity
     * @param <T>     a subtype of Collection that extends E
     * @return the collection of {@link org.bukkit.entity.Entity}´s that are visible to the command source
     * @throws WrapperCommandSyntaxException if no {@link org.bukkit.entity.Entity}´s were found that are visible to the command source
     */
    @Contract("_, _ -> param2")
    public static <E extends org.bukkit.entity.Entity, T extends Collection<E>> T checkEntitySuggestion(CommandSender source, T targets) throws WrapperCommandSyntaxException {
        if (!(source instanceof Player player)) return targets;

        for (E target : targets) {
            if (target instanceof Player check) {
                if (canPlayerSeePlayer(player, check)) continue;
                targets.remove(target);
            }
        }
        if (targets.size() == 0) {
            throw Exceptions.NO_ENTITIES_FOUND;
        }

        return targets;
    }


    /**
     * Checks whether the provided {@link org.bukkit.entity.Entity} is visible to the source who executed the command.
     *
     * @param sender the {@link CommandSender}
     * @param target the {@link org.bukkit.entity.Entity} to check
     * @param <E>    a subtype of Entity
     * @return the {@link org.bukkit.entity.Entity} that is visible to the command source
     * @throws WrapperCommandSyntaxException if the player is not visible to the command source
     */
    @Contract("_, null -> null")
    public static <E extends org.bukkit.entity.Entity> E checkEntitySuggestion(CommandSender sender, E target) throws WrapperCommandSyntaxException {
        if (target == null) return null;
        return checkEntitySuggestion(sender, Collections.singleton(target)).iterator().next();
    }

    /**
     * Checks whether the provided {@link Player} is visible to the source who executed the command.
     *
     * @param source the command source
     * @param player the {@link Player} to check
     * @return the {@link Player} that is visible to the command source
     * @throws WrapperCommandSyntaxException if the player is not visible to the command source
     */
    @Contract("_, null -> null")
    public static Player checkPlayerSuggestion(CommandSender source, Player player) throws WrapperCommandSyntaxException {
        if (player == null) return null;
        return checkPlayerSuggestion(source, Collections.singleton(player)).iterator().next();
    }

    /**
     * Checks whether the provided entity is visible to the source who executed the command.
     *
     * @param source the command source
     * @param entity the entity to check
     * @param <T>    the type of entity
     * @return the entity that is visible to the command source
     * @throws WrapperCommandSyntaxException if the entity is not visible to the command source
     */
    @SuppressWarnings("UnusedReturnValue")
    public static <T extends org.bukkit.entity.Entity> T checkSingleEntitySuggestion(CommandSender source, T entity) throws WrapperCommandSyntaxException {
        return checkEntitySuggestion(source, Collections.singleton(entity)).iterator().next();
    }
}
