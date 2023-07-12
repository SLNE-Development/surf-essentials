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
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.IOException;

public class TeleportOffline extends EssentialsCommand {
    public TeleportOffline() {
        super("tpoff", "tpoff <player> [<destination>]", "Teleports the sender to offline players last known location or teleports the offline player to the specified location");

        withPermission(Permissions.OFFLINE_TELEPORT_PERMISSION);

        then(offlinePlayerArgument("player")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> teleportToPlayer(
                        getEntityOrException(sender),
                        args.getUnchecked("player")
                ))
                .then(locationArgument("destination")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> teleportPlayerToLocation(
                                sender,
                                args.getUnchecked("player"),
                                args.getUnchecked("destination")
                        ))
                )
        );
    }

    private int teleportToPlayer(Entity source, OfflinePlayer offlinePlayer) throws WrapperCommandSyntaxException {
        if (!offlinePlayer.hasPlayedBefore()) throw Exceptions.NO_PLAYERS_FOUND;

        val onlinePlayer = offlinePlayer.getPlayer();
        if (onlinePlayer != null) {
            source.teleportAsync(onlinePlayer.getLocation());

        } else {
            EssentialsUtil.sendInfo(source, "Spieler daten laden...");
            source.teleportAsync(EssentialsUtil.getLocation(offlinePlayer), PlayerTeleportEvent.TeleportCause.COMMAND);
        }

        EssentialsUtil.sendSuccess(source, Component.text("Du hast dich zu ", Colors.SUCCESS)
                .append(EssentialsUtil.getOfflineDisplayName(offlinePlayer))
                .append(Component.text(" teleportiert!", Colors.SUCCESS)));

        return 1;
    }

    private int teleportPlayerToLocation(CommandSender sender, OfflinePlayer offlinePlayer, Location newLocation) throws WrapperCommandSyntaxException {
        if (!offlinePlayer.hasPlayedBefore()) throw Exceptions.NO_PLAYERS_FOUND;

        val onlinePlayer = offlinePlayer.getPlayer();
        if (onlinePlayer != null) {
            onlinePlayer.teleportAsync(newLocation);

        } else {
            EssentialsUtil.sendInfo(sender, "Teleportiere Spieler...");

            try {
                EssentialsUtil.setLocation(offlinePlayer.getUniqueId(), newLocation);
            } catch (IOException e) {
                throw Exceptions.FAILED_TO_WRITE_TO_FILE_IO.create(e);
            }
        }

        EssentialsUtil.sendSuccess(sender, Component.text("Der Spieler ", Colors.SUCCESS)
                .append(EssentialsUtil.getOfflineDisplayName(offlinePlayer))
                .append(Component.text(" wurde zu ", Colors.SUCCESS))
                .append(EssentialsUtil.formatLocationWithoutSpacer(newLocation))
                .append(Component.text(" teleportiert!", Colors.SUCCESS)));

        return 1;
    }
}
