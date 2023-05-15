package dev.slne.surf.essentials.utils.abtract;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * This abstract class provides utility methods related to player vanishing and entity visibility.
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
     * Checks whether a {@link ServerPlayer} can see another {@link ServerPlayer}.
     *
     * @param player       the {@link ServerPlayer} who is checking visibility
     * @param playerToCheck the {@link ServerPlayer} to check visibility against
     * @return `true` if `player` can see `playerToCheck`, `false` otherwise
     */
    public static boolean canPlayerSeePlayer(@NotNull ServerPlayer player, @NotNull ServerPlayer playerToCheck){
        return canPlayerSeePlayer(player.getBukkitEntity(), playerToCheck.getBukkitEntity());
    }

    /**
     * Checks whether a {@link Player} can see another {@link Player}.
     *
     * @param player the {@link Player} who is checking visibility
     * @param playerToCheck the {@link Player} to check visibility against
     * @return `true` if `player` can see `playerToCheck`, `false` otherwise
     */
    public static boolean canPlayerSeePlayer(@NotNull Player player, @NotNull Player playerToCheck){
        if (!isVanished(playerToCheck)) return true;
        return player.canSee(playerToCheck);
    }

    /**
     * Checks whether a command source can see an {@link Entity}.
     *
     * @param source the command source to check
     * @param entity the entity to check
     * @return `true` if `source` can see `entity`, `false` otherwise
     */
    public static boolean canSourceSeeEntity(@NotNull CommandSourceStack source, @NotNull Entity entity){
        return canSourceSeeEntity(source, entity.getBukkitEntity());
    }

    /**
     * Checks whether a command source can see an {@link org.bukkit.entity.Entity}.
     *
     * @param source the command source to check
     * @param entity the entity to check
     * @return `true` if `source` can see `entity`, `false` otherwise
     */
    public static boolean canSourceSeeEntity(@NotNull CommandSourceStack source, @NotNull org.bukkit.entity.Entity entity){
        if (source.isPlayer()){
            final var player = Objects.requireNonNull(source.getPlayer()).getBukkitEntity();
            if (entity instanceof Player other){
                return canPlayerSeePlayer(player, other);
            }else  {
                return player.canSee(entity);
            }
        }
        return true;
    }

    /**
     * Checks whether the {@link ServerPlayer}s in the provided collection are visible to the source who executed the command.
     *
     * @param source the command source
     * @param targets the collection of {@link ServerPlayer}s to check
     * @param <T> a subtype of Collection that extends {@link ServerPlayer}
     * @return the collection of {@link ServerPlayer}s that are visible to the command source
     * @throws CommandSyntaxException if no {@link ServerPlayer}s were found that are visible to the command source
     */
    @Contract("_, _ -> param2")
    public static<T extends Collection<ServerPlayer>> T checkPlayerSuggestion(CommandSourceStack source, T targets) throws CommandSyntaxException {
        if (!source.isPlayer()) return targets;

        for (ServerPlayer target : targets) {
            if (canPlayerSeePlayer(source.getPlayerOrException(), target)) continue;
            targets.remove(target);
        }
        if (targets.size() == 0){
            throw EntityArgument.NO_PLAYERS_FOUND.create();
        }
        return targets;
    }

    /**
     * Checks whether the {@link ServerPlayer}s in the provided collection are visible to the source who executed the command.
     *
     * @param source the command source
     * @param targets the collection of {@link ServerPlayer}s to check
     * @param <T> a subtype of Collection that extends {@link ServerPlayer}
     * @return the collection of {@link ServerPlayer} that are visible to the command source
     */
    @Contract("_, _ -> param2")
    public static<T extends Collection<ServerPlayer>> T checkPlayerSuggestionWithoutException(CommandSourceStack source, T targets){
        try {
            checkEntitySuggestion(source, targets);
        } catch (CommandSyntaxException ignored) {}

        return targets;
    }

    /**
     * Checks whether the {@link Entity}´s in the provided collection are visible to the player who executed the command.
     *
     * @param source the command source
     * @param targets the collection of {@link Entity}´s to check
     * @param <E> a subtype of Entity
     * @param <T> a subtype of Collection that extends E
     * @return the collection of {@link Entity}´s that are visible to the command source
     * @throws CommandSyntaxException if no {@link Entity}´s were found that are visible to the command source
     */
    @Contract("_, _ -> param2")
    public static<E extends Entity, T extends Collection<? extends E>> T checkEntitySuggestion(CommandSourceStack source, T targets) throws CommandSyntaxException {
        if (!source.isPlayer()) return targets;

        for (E target : targets) {
            if (target instanceof ServerPlayer serverPlayer) {
                if (canPlayerSeePlayer(source.getPlayerOrException(), serverPlayer)) continue;
                targets.remove(target);
            }
        }
        if (targets.size() == 0) {
            throw EntityArgument.NO_ENTITIES_FOUND.create();
        }

        return targets;
    }

    /**
     * Checks whether the provided {@link ServerPlayer} is visible to the source who executed the command.
     *
     * @param source the command source
     * @param player the {@link ServerPlayer} to check
     * @return the {@link ServerPlayer} that is visible to the command source
     * @throws CommandSyntaxException if the player is not visible to the command source
     */
    public static ServerPlayer checkPlayerSuggestion(CommandSourceStack source, ServerPlayer player) throws CommandSyntaxException {
        return checkPlayerSuggestion(source, Collections.singleton(player)).iterator().next();
    }

    /**
     * Checks whether the provided {@link Player} is visible to the source who executed the command.
     *
     * @param source the command source
     * @param player the {@link Player} to check
     * @return the {@link Player} that is visible to the command source
     * @throws CommandSyntaxException if the player is not visible to the command source
     */
    public static @NotNull Player checkPlayerSuggestion(CommandSourceStack source, Player player) throws CommandSyntaxException {
        return checkPlayerSuggestion(source, Collections.singleton(CraftUtil.toServerPlayer(player))).iterator().next().getBukkitEntity();
    }

    /**
     * Checks whether the provided entity is visible to the source who executed the command.
     *
     * @param source the command source
     * @param entity the entity to check
     * @param <T> the type of entity
     * @return the entity that is visible to the command source
     * @throws CommandSyntaxException if the entity is not visible to the command source
     */
    @SuppressWarnings("UnusedReturnValue")
    public static <T extends Entity> T checkSingleEntitySuggestion(CommandSourceStack source, T entity) throws CommandSyntaxException{
        return checkEntitySuggestion(source, Collections.singleton(entity)).iterator().next();
    }
}
