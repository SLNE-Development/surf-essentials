package dev.slne.surf.essentialsold.commands.minecraft;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.brigadier.Exceptions;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class OpCommand extends EssentialsCommand {
    public OpCommand() {
        super("op", "op <player>", "Gives a player operator permissions");

        withPermission(Permissions.OP_PERMISSION);

        then(offlinePlayerArgument("player")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> op(sender.getCallee(), Objects.requireNonNull(args.getUnchecked("player")))));
    }

    private static int op(CommandSender source, OfflinePlayer player) throws WrapperCommandSyntaxException {
        if (player.isOp()) throw Exceptions.ERROR_ALREADY_OP;
        player.setOp(true);

        Bukkit.broadcast(EssentialsUtil.getPrefix()
                .append(EssentialsUtil.getOfflineDisplayName(player))
                .append(Component.text(" wurde durch ", Colors.INFO))
                .append(EssentialsUtil.getDisplayName(source))
                .append(Component.text(" zum Operator", Colors.INFO)), Permissions.OP_ANNOUCE_PERMISSION);
        return 1;
    }
}
