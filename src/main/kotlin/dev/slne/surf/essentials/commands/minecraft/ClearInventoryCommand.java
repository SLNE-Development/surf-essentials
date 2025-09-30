package dev.slne.surf.essentials.commands.minecraft;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

public class ClearInventoryCommand extends EssentialsCommand {
    public ClearInventoryCommand() {
        super("clear", "clear [<targets>]", "Clears the inventories from the targets", "clearinventory");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.CLEAR_SELF_PERMISSION, Permissions.CLEAR_OTHER_PERMISSION));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> clear(sender.getCallee(), Collections.singleton(getPlayerOrException(sender))));
        then(playersArgument("players")
                .withPermission(Permissions.CLEAR_OTHER_PERMISSION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> clear(sender.getCallee(), args.getUnchecked("players"))));
    }

    private int clear(CommandSender source, Collection<Player> targetsUnchecked) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int successfulClears = 0;

        for (HumanEntity target : targets) {
            target.getInventory().clear();
            successfulClears++;
        }


        if (successfulClears == 1) {
            EssentialsUtil.sendSuccess(source, Component.text("Das Inventar von ", Colors.SUCCESS)
                    .append(EssentialsUtil.getDisplayName(targets.iterator().next()))
                    .append(Component.text(" wurde geleert.", Colors.SUCCESS)));
        } else {
            EssentialsUtil.sendSuccess(source, Component.text("Das Inventar von ", Colors.SUCCESS)
                    .append(Component.text(successfulClears, Colors.TERTIARY))
                    .append(Component.text(" Spielern wurde geleert.", Colors.SUCCESS)));
        }

        return successfulClears;
    }
}
