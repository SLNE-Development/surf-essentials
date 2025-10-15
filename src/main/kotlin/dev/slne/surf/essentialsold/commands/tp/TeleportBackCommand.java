package dev.slne.surf.essentialsold.commands.tp;

import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.listener.listeners.TeleportListener;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportBackCommand extends EssentialsCommand {

    public TeleportBackCommand() {
        super("back", "back", "Teleports you back to your last location", "tpback");

        withPermission(Permissions.TELEPORT_BACK_PERMISSION);
        executesNative((sender, args) -> {
            val player = getPlayerOrException(sender);
            TeleportListener.getLastTeleportLocation(player).ifPresentOrElse(
                    location -> player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(__ ->
                            EssentialsUtil.sendSuccess(sender, "Du wurdest zurück Teleportiert!")),
                    () -> EssentialsUtil.sendError(sender, "Du hast dich noch nicht Teleportiert!")
            );
            return 1;
        });
    }
}
