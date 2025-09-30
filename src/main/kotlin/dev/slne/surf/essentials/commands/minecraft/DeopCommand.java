package dev.slne.surf.essentials.commands.minecraft;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class DeopCommand extends EssentialsCommand {
    public DeopCommand() {
        super("deop", "deop <player>", "Deop a player");

        withPermission(Permissions.DEOP_PERMISSION);

        then(offlinePlayerArgument("player")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> deop(sender.getCallee(), Objects.requireNonNull(args.getUnchecked("player")))));
    }

    private static int deop(CommandSender source, OfflinePlayer player) throws WrapperCommandSyntaxException {
        if (!player.isOp()) throw Exceptions.ERROR_PLAYER_WAS_NEVER_OP;

        if (player.isOp()) {
            player.setOp(false);

            Bukkit.broadcast(EssentialsUtil.getPrefix()
                    .append(EssentialsUtil.getOfflineDisplayName(player))
                    .append(Component.text(" ist durch ", Colors.INFO))
                    .append(EssentialsUtil.getDisplayName(source))
                    .append(Component.text(" kein Operator mehr!", Colors.INFO)), Permissions.OP_ANNOUCE_PERMISSION);

        }
        return 1;
    }
}
