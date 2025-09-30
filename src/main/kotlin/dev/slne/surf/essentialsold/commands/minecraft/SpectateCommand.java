package dev.slne.surf.essentialsold.commands.minecraft;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.brigadier.Exceptions;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class SpectateCommand extends EssentialsCommand {
    public SpectateCommand() {
        super("spectate", "spectate [<target>] [<player>]", "Spectates the given player");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.SPECTATE_SELF_PERMISSION, Permissions.SPECTATE_SELF_PERMISSION, Permissions.SPECTATE_OTHER_PERMISSION));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> spectate(
                sender.getCallee(),
                null,
                getPlayerOrException(sender)
        ));
        then(entityArgument("target")
                .withRequirement(EssentialsUtil.checkPermissions(Permissions.SPECTATE_SELF_PERMISSION, Permissions.SPECTATE_OTHER_PERMISSION))
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> spectate(
                        sender.getCallee(),
                        args.getUnchecked("target"),
                        getPlayerOrException(sender)
                ))
                .then(playerArgument("player")
                        .withPermission(Permissions.SPECTATE_OTHER_PERMISSION)
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> spectate(
                                sender.getCallee(),
                                args.getUnchecked("target"),
                                args.getUnchecked("player")
                        ))
                )
        );
    }

    private int spectate(CommandSender source, @Nullable Entity entityUnchecked, Player playerUnchecked) throws WrapperCommandSyntaxException {
        val entity = EssentialsUtil.checkEntitySuggestion(source, entityUnchecked);
        val player = EssentialsUtil.checkPlayerSuggestion(source, playerUnchecked);

        if (entity != null && entity.equals(player)) throw Exceptions.ERROR_CANNOT_SPECTATE_SELF;

        if (player.getGameMode() != GameMode.SPECTATOR && entity != null) {
            player.setGameMode(GameMode.SPECTATOR);
        }
        player.setSpectatorTarget(entity);

        if (entity != null) {
            EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(player)
                    .append(Component.text(" beobachtet nun ", Colors.SUCCESS))
                    .append(EssentialsUtil.getDisplayName(entity))
                    .append(Component.text(".", Colors.SUCCESS)));
        } else {
            EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(player)
                    .append(Component.text(" beobachtet nun niemanden mehr.", Colors.SUCCESS)));
        }
        return 1;
    }
}
