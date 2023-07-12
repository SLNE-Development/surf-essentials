package dev.slne.surf.essentials.commands.tp;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class TeleportToTopCommand extends EssentialsCommand {
    public TeleportToTopCommand() {
        super("tptop", "tptop [<player>]", "Teleports the player to the top of the world");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.TELEPORT_TOP_SELF_PERMISSION, Permissions.TELEPORT_TOP_OTHER_PERMISSION));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> tptop(sender.getCallee(), getPlayerOrException(sender)));
        then(offlinePlayerArgument("player") // TODO test
                .withPermission(Permissions.TELEPORT_TOP_OTHER_PERMISSION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> tptop(sender.getCallee(), args.getUnchecked("player"))));
    }

    private int tptop(CommandSender source, OfflinePlayer offlinePlayer) throws WrapperCommandSyntaxException {
        val uuid = offlinePlayer.getUniqueId();
        @Nullable val onlinePlayer = offlinePlayer.getPlayer();
        final Location location;

        if (onlinePlayer != null) {
            location = onlinePlayer.getLocation().clone();
            location.setY((location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ())) + 2);
            location.setX(location.getBlockX() + 0.5);
            location.setZ(location.getBlockZ() + 0.5);

            onlinePlayer.teleportAsync(location);
        } else {

            if (!offlinePlayer.hasPlayedBefore()) {
                throw Exceptions.NO_PLAYERS_FOUND;
            }

            EssentialsUtil.sendInfo(source, "Teleportiere Spieler...");


            location = EssentialsUtil.getLocation(offlinePlayer);

            location.setY((location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ())) + 2);
            location.setX(location.getBlockX() + 0.5);
            location.setZ(location.getBlockZ() + 0.5);

            try {
                EssentialsUtil.setLocation(uuid, location);
            } catch (IOException e) {
                throw Exceptions.FAILED_TO_WRITE_TO_FILE_IO.create(e);
            }
        }

        EssentialsUtil.sendSuccess(source, EssentialsUtil.getOfflineDisplayName(offlinePlayer)
                .append(Component.text(" wurde zum höchsten Block teleportiert.", Colors.SUCCESS)
                        .hoverEvent(HoverEvent.showText(EssentialsUtil.formatLocation(Colors.INFO, location, true)))));
        return 1;
    }
}
