package dev.slne.surf.essentials.commands.general;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class ChatClearCommand extends EssentialsCommand {
    private static final int CLEAR_LINES = 100;

    public ChatClearCommand() {
        super("chatclear", "chatclear [<players>]", "Clears the chat from the targets except they have the bypass permission", "cc");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.CHAT_CLEAR_SELF_PERMISSION, Permissions.CHAT_CLEAR_OTHER_PERMISSION));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> clearChat(sender.getCallee(), List.of(getPlayerOrException(sender))));

        then(playersArgument("players")
                .withPermission(Permissions.CHAT_CLEAR_OTHER_PERMISSION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> clearChat(sender.getCallee(), args.getUnchecked("players"))));
    }

    private int clearChat(CommandSender source, Collection<Player> targetsUnchecked) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int successfulClears = 0;

        for (CommandSender target : targets) {
            if (target.hasPermission(Permissions.CHAT_CLEAR_BYPASS_PERMISSION)) continue;

            for (int i = 0; i < CLEAR_LINES; i++) {
                target.sendMessage(Component.empty());
            }
            successfulClears++;

            EssentialsUtil.sendSuccess(target, "Dein Chat wurde gelöscht!");
        }

        if (successfulClears == 1) {
            if (!(source instanceof Player player && player.equals(targets.iterator().next()))) {
                EssentialsUtil.sendSuccess(source, Component.text("Der Chat von ", Colors.SUCCESS)
                        .append(EssentialsUtil.getDisplayName(targets.iterator().next()))
                        .append(Component.text(" wurde gelöscht.", Colors.SUCCESS)));
            }

        } else {
            EssentialsUtil.sendSuccess(source, Component.text("Der Chat von ", Colors.SUCCESS)
                    .append(Component.text(successfulClears, Colors.TERTIARY))
                    .append(Component.text(" Spielern wurde gelöscht.", Colors.SUCCESS)));

        }
        return successfulClears;
    }
}
