package dev.slne.surf.essentials.commands.general;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.annontations.UpdateRequired;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class RuleCommand extends EssentialsCommand {
    @UpdateRequired(updateReason = "The rules URL will changed")
    private static final String RULES_URL = "https://castcrafter.de/subserver";

    public RuleCommand() {
        super("rules", "rules [<players>]", "Displays the rules of the server", "rule");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.RULE_SELF_PERMISSION, Permissions.RULE_OTHER_PERMISSION));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> sendRules(sender.getCallee(), List.of(getPlayerOrException(sender))));
        then(playersArgument("players")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> sendRules(sender.getCallee(), args.getUnchecked("players"))));
    }

    private int sendRules(CommandSender source, Collection<Player> targetsUnchecked) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int successfulSends = 0;

        for (Audience player : targets) {
            EssentialsUtil.sendSuccess(player, Component.text("Alle ", Colors.SUCCESS)
                    .append(Component.text("Regeln", Colors.GOLD))
                    .append(Component.text(" und", Colors.SUCCESS))
                    .append(Component.text(" Informationen", Colors.GOLD))
                    .append(Component.text(" findest du ", Colors.SUCCESS))
                    .append(Component.text("Hier", Colors.RED)
                            .decorate(TextDecoration.BOLD)
                            .hoverEvent(Component.text("Klicke um zu der Website zu kommen", Colors.GRAY))
                            .clickEvent(ClickEvent.openUrl(RULES_URL))));
            successfulSends++;
        }

        boolean isSelf = source instanceof Player player && targets.size() == 1 && targets.iterator().next().equals(player);
        if (successfulSends == 1 && !isSelf) {
            EssentialsUtil.sendSuccess(source, Component.text("Die Regeln wurden an ", Colors.SUCCESS)
                    .append(EssentialsUtil.getDisplayName(targets.iterator().next()))
                    .append(Component.text(" gesendet!", Colors.SUCCESS)));
        } else if (!isSelf) {
            EssentialsUtil.sendSuccess(source, Component.text("Die Regeln wurden an ", Colors.SUCCESS)
                    .append(Component.text(successfulSends, Colors.TERTIARY))
                    .append(Component.text(" Spieler gesendet!", Colors.SUCCESS)));
        }

        return 1;
    }
}
