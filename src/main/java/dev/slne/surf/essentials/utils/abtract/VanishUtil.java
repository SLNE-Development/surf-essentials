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

@SuppressWarnings("unused")
public abstract class VanishUtil extends ColorCodes {

    /**
     * Returns whether the given player is vanished or not.
     * A player is considered vanished if they have a "vanished" metadata value
     * set to `true`.
     *
     * @param player the player to check
     * @return `true` if the player is vanished, `false` otherwise
     */
    public static boolean isVanished(@NotNull Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

    public static boolean canPlayerSeePlayer(@NotNull ServerPlayer player, @NotNull ServerPlayer playerToCheck){
        if (!isVanished(playerToCheck.getBukkitEntity())) return true;
        return player.getBukkitEntity().canSee(playerToCheck.getBukkitEntity());
    }

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

    @Contract("_, _ -> param2")
    public static<T extends Collection<ServerPlayer>> T checkPlayerSuggestionWithoutException(CommandSourceStack source, T targets){
        try {
            checkEntitySuggestion(source, targets);
        } catch (CommandSyntaxException ignored) {}

        return targets;
    }

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

    public static <T extends ServerPlayer> ServerPlayer checkPlayerSuggestion(CommandSourceStack source, T player) throws CommandSyntaxException {
        Collection<ServerPlayer> players = checkPlayerSuggestion(source, Collections.singleton(player));
        return players.iterator().next();
    }
    public static <T extends Player> Player checkPlayerSuggestion(CommandSourceStack source, T player) throws CommandSyntaxException {
        Collection<ServerPlayer> players = checkPlayerSuggestion(source, Collections.singleton(CraftUtil.toServerPlayer(player)));
        return players.iterator().next().getBukkitEntity();
    }

    public static <T extends Entity> Entity checkSingleEntitySuggestion(CommandSourceStack source, T entity) throws CommandSyntaxException{
        Collection<? extends Entity> entities = checkEntitySuggestion(source, Collections.singleton(entity));
        return entities.iterator().next();
    }
}
