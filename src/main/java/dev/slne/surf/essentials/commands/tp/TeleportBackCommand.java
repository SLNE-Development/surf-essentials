package dev.slne.surf.essentials.commands.tp;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.listener.listeners.TeleportListener;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportBackCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"back", "tpback"};
    }

    @Override
    public String usage() {
        return "/back";
    }

    @Override
    public String description() {
        return "Teleports you back to your last location";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.TELEPORT_BACK_PERMISSION));

        literal.executes(context -> back(context.getSource()));
    }

    private int back(CommandSourceStack source) throws CommandSyntaxException {
        final var player = source.getPlayerOrException().getBukkitEntity();

        TeleportListener.getLastTeleportLocation(player).ifPresentOrElse(
                location -> player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(__ ->
                        EssentialsUtil.sendSuccess(player, "Du wurdest zurück Teleportiert!")),

                () -> EssentialsUtil.sendError(player, "Du hast dich noch nicht Teleportiert!")
        );
        return 1;
    }
}
