package dev.slne.surf.essentialsold.commands.cheat;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.brigadier.Exceptions;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class HurtCommand extends EssentialsCommand {
    public HurtCommand() {
        super("hurt", "hurt [<player>]", "Hurt other players");

        withPermission(Permissions.HURT_PERMISSION);

        then(playersArgument("players")
                .then(integerArgument("amount", 1, 255)
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> executeHurt(sender.getCallee(), args.getUnchecked("players"), args.getUnchecked("amount")))));
    }

    private int executeHurt(CommandSender source, Collection<Player> targetsUnchecked, Integer amount) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int successfulHurted = 0;

        for (Player target : targets) {
            if (target.isInvulnerable()) continue;
            target.damage(amount, ((source instanceof Player player) ? player : null));
            successfulHurted++;
        }

        if (successfulHurted == 0) throw Exceptions.ERROR_INVULNERABLE;

        if (successfulHurted == 1) {
            EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(targets.iterator().next())
                    .append(Component.text(" wurde verletzt!", Colors.SUCCESS)));
        } else {
            EssentialsUtil.sendSuccess(source, (Component.text(targets.size(), Colors.TERTIARY))
                    .append(Component.text(" Spieler wurden verletzt!", Colors.SUCCESS)));
        }
        return targets.size();
    }
}
