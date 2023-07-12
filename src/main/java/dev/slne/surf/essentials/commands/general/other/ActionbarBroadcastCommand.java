package dev.slne.surf.essentials.commands.general.other;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ActionbarBroadcastCommand extends EssentialsCommand {
    public ActionbarBroadcastCommand() {
        super("actionbarbroadcast", "actionbarbroadcast <players> <message>", "Broadcasts a message to all specified players");

        withPermission(Permissions.ACTION_BAR_BROADCAST_PERMISSION);

        then(playersArgument("players")
                .then(greedyStringArgument("message")
                        .replaceSuggestions(EssentialsUtil.suggestColors())
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> broadcast(sender.getCallee(), args.getUnchecked("players"), args.getUnchecked("message")))));
    }

    private int broadcast(CommandSender source, Collection<Player> targetsUnchecked, String actionbar) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        val actionBarText = EssentialsUtil.deserialize(actionbar).colorIfAbsent(Colors.TERTIARY);
        int successfullyShowed = 0;

        for (Audience target : targets) {
            target.sendActionBar(actionBarText);
            successfullyShowed++;
        }

        EssentialsUtil.sendSuccess(source, Component.text("Die ", Colors.SUCCESS)
                .append(Component.text("Actionbar", Colors.TERTIARY)
                        .hoverEvent(HoverEvent.showText(Component.text("Text: ", Colors.INFO)
                                .append(actionBarText))))
                .append(Component.text(" wurde ", Colors.SUCCESS))
                .append((successfullyShowed == 1) ? EssentialsUtil.getDisplayName(targets.iterator().next()) : Component.text(successfullyShowed, Colors.TERTIARY).append(Component.text(" Spielern ", Colors.SUCCESS)))
                .append(Component.text(" gezeigt!", Colors.SUCCESS)));
        return successfullyShowed;
    }
}
