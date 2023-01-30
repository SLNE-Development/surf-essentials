package dev.slne.surf.essentials.main.commands.tp;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.main.listeners.TeleportListener;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import dev.slne.surf.essentials.main.utils.Permissions;
import dev.slne.surf.essentials.main.utils.brigadier.BrigadierCommand;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

@PermissionTag(name = Permissions.TELEPORT_BACK_PERMISSION, desc = "Allows you to teleport")
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
        Player player = source.getPlayerOrException().getBukkitEntity();
        Location location = TeleportListener.getLastTeleportLocationOrNull(player);

        if (location == null){
            EssentialsUtil.sendError(source, "Du hast dich noch nicht Teleportiert!");
            return 0;
        }

        player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN);

        EssentialsUtil.sendSuccess(source, "Du wirst zurück Teleportiert!");
        return 1;
    }
}
