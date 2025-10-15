package dev.slne.surf.essentialsold.commands.general;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetPosCommand extends EssentialsCommand {
    public GetPosCommand() {
        super("getpos", "getpos [<player>]", "Get the position of the player", "position");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.GET_POS_SELF_PERMISSION, Permissions.GET_POS_OTHER_PERMISSION));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> getPos(sender.getCallee(), getPlayerOrException(sender)));
        then(playerArgument("player")
                .withPermission(Permissions.GET_POS_OTHER_PERMISSION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> getPos(sender.getCallee(), args.getUnchecked("player"))));
    }

    private int getPos(CommandSender source, Player playerUnchecked) throws WrapperCommandSyntaxException {
        val player = EssentialsUtil.checkPlayerSuggestion(source, playerUnchecked);
        val location = player.getLocation();

        EssentialsUtil.sendSuccess(source, Component.text("Die Position von ", Colors.INFO)
                .append(EssentialsUtil.getDisplayName(player))
                .append(Component.text(" ist: ", Colors.INFO))
                .append(EssentialsUtil.formatLocation(Colors.INFO, location, true)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Kopieren", Colors.INFO)))
                        .clickEvent(ClickEvent.copyToClipboard("%s %s %s".formatted(location.getBlockX(), location.getBlockY(), location.getBlockZ())))));

        return 1;
    }
}
