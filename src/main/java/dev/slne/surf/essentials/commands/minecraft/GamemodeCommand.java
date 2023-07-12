package dev.slne.surf.essentials.commands.minecraft;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class GamemodeCommand extends EssentialsCommand {

    public GamemodeCommand() {
        super("gamemode", "gamemode <mode> [offline | players]", "Change gamemodes of a players", "gm");

        withRequirement(EssentialsUtil.hasGameModePermission());

        registerGameModes(GameMode.SURVIVAL, Permissions.GAMEMODE_SURVIVAL_SELF_PERMISSION, Permissions.GAMEMODE_SURVIVAL_OTHER_PERMISSION,
                Permissions.GAMEMODE_SURVIVAL_OTHER_OFFLINE_PERMISSION);

        registerGameModes(GameMode.CREATIVE, Permissions.GAMEMODE_CREATIVE_SELF_PERMISSION, Permissions.GAMEMODE_CREATIVE_OTHER_PERMISSION,
                Permissions.GAMEMODE_CREATIVE_OTHER_OFFLINE_PERMISSION);

        registerGameModes(GameMode.SPECTATOR, Permissions.GAMEMODE_SPECTATOR_SELF_PERMISSION, Permissions.GAMEMODE_SPECTATOR_OTHER_PERMISSION,
                Permissions.GAMEMODE_SPECTATOR_OTHER_OFFLINE_PERMISSION);

        registerGameModes(GameMode.ADVENTURE, Permissions.GAMEMODE_ADVENTURE_SELF_PERMISSION, Permissions.GAMEMODE_ADVENTURE_OTHER_PERMISSION,
                Permissions.GAMEMODE_ADVENTURE_OTHER_OFFLINE_PERMISSION);
    }

    private int setMode(CommandSender source, Collection<Player> targetsUnchecked, GameMode gameMode) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int successfulChanges = 0;

        if (targets.size() == 1) {
            val target = targets.iterator().next();
            target.setGameMode(gameMode);
            EssentialsUtil.sendSuccess(target, Component.text("Dein Gamemode wurde auf ", Colors.SUCCESS)
                    .append(Component.translatable(gameMode.translationKey(), Colors.VARIABLE_VALUE))
                    .append(Component.text(" gesetzt!", Colors.SUCCESS)));
            logSingleChange(target, gameMode);

        } else {
            for (Player target : targets) {
                target.setGameMode(gameMode);
                EssentialsUtil.sendSuccess(target, Component.text("Dein Gamemode wurde auf ", Colors.SUCCESS)
                        .append(Component.translatable(gameMode.translationKey(), Colors.VARIABLE_VALUE))
                        .append(Component.text(" gesetzt!", Colors.SUCCESS)));
                ++successfulChanges;
            }
            logMultiChange(gameMode, successfulChanges);
        }
        return successfulChanges;
    }

    private int setOfflineMode(CommandSender source, GameMode gameType, OfflinePlayer offlinePlayer) throws WrapperCommandSyntaxException {
        val onlinePlayer = Optional.ofNullable(offlinePlayer.getPlayer());
        val targetUUID = offlinePlayer.getUniqueId();

        if (onlinePlayer.isPresent()) {
            setMode(source, Collections.singleton(onlinePlayer.get()), gameType);
            return 1;
        }

        val playerData = EssentialsUtil.getPlayerFile(targetUUID);
        if (playerData.isEmpty()) throw Exceptions.NO_PLAYERS_FOUND;

        EssentialsUtil.setOfflineGameMode(offlinePlayer, gameType);

        EssentialsUtil.sendSuccess(source, Component.text("Der Gamemode von ", Colors.SUCCESS)
                .append(EssentialsUtil.getOfflineDisplayName(offlinePlayer))
                .append(Component.text(" wurde auf ", Colors.SUCCESS))
                .append(Component.translatable(gameType.translationKey(), Colors.VARIABLE_VALUE))
                .append(Component.text(" gesetzt!", Colors.SUCCESS)));

        return 1;
    }

    private void logSingleChange(@NotNull Player player, @NotNull GameMode gameMode) {
        player.getServer().getOnlinePlayers().stream()
                .filter(serverPlayer -> !(serverPlayer.equals(player)) && serverPlayer.hasPermission(Permissions.GAMEMODE_ANNOUCE_PERMISSION))
                .forEach(serverPlayer -> EssentialsUtil.sendSuccess(serverPlayer, EssentialsUtil.getDisplayName(player)
                        .append(Component.text(" hat in den Gamemode ", Colors.INFO))
                        .append(Component.translatable(gameMode.translationKey(), Colors.VARIABLE_VALUE))
                        .append(Component.text(" gewechselt!", Colors.INFO))));

        SurfEssentials.logger().info(net.kyori.adventure.text.Component.text("Set ", Colors.INFO)
                .append(EssentialsUtil.getDisplayName(player))
                .append(net.kyori.adventure.text.Component.text("´s game mode to ", Colors.INFO))
                .append(Component.translatable(gameMode.translationKey(), Colors.VARIABLE_VALUE)));
    }

    private void logMultiChange(@NotNull GameMode gameMode, int amount) {
        Bukkit.broadcast(EssentialsUtil.getPrefix()
                .append(Component.text("Der Gamemode von ", Colors.INFO))
                .append(Component.text(amount, Colors.VARIABLE_VALUE))
                .append(Component.text(" Spielern wurde auf ", Colors.INFO))
                .append(Component.translatable(gameMode.translationKey(), Colors.VARIABLE_VALUE))
                .append(Component.text(" gesetzt!", Colors.INFO)), Permissions.GAMEMODE_ANNOUCE_PERMISSION);

        SurfEssentials.logger().info(Component.text("Set the game mode for ", Colors.INFO)
                .append(Component.text(amount, Colors.VARIABLE_VALUE))
                .append(Component.text(" players to ", Colors.INFO))
                .append(Component.translatable(gameMode.translationKey(), Colors.VARIABLE_VALUE)));
    }

    private void registerGameModes(GameMode gameType, String permissionSelf, String permissionOthers, String permissionOthersOffline) {

        then(literal(gameType.name().toLowerCase())
                .withRequirement(EssentialsUtil.checkPermissions(permissionSelf, permissionOthers, permissionOthersOffline))
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> setMode(
                        sender.getCallee(),
                        Collections.singleton(getPlayerOrException(sender)),
                        gameType
                ))
                .then(playersArgument("players")
                        .withRequirement(EssentialsUtil.checkPermissions(permissionOthers, permissionOthersOffline))
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> setMode(
                                sender.getCallee(),
                                args.getUnchecked("players"),
                                gameType
                        )))
                .then(literal("offline")
                        .withPermission(permissionOthersOffline)
                        .then(offlinePlayerArgument("offlinePlayer")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> setOfflineMode(
                                        sender.getCallee(),
                                        gameType,
                                        args.getUnchecked("offlinePlayer")
                                ))
                        )
                )
        );
    }
}
