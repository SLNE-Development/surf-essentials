package dev.slne.surf.essentials.commands.cheat;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FillStackCommand extends EssentialsCommand {
    public FillStackCommand() {
        super("fillstack", "fillstack [<player>]", "Fills the item in the player's hand to the maximum stack size.", "more");

        withPermission(Permissions.FILL_STACK_PERMISSION);

        executesNative((NativeResultingCommandExecutor) (sender, args) -> more(sender.getCallee(), getPlayerOrException(sender)));

        then(playerArgument("player"))
                .executesNative((NativeResultingCommandExecutor) (sender, args) ->  more(sender.getCallee(), args.getUnchecked("player")));
    }

    private int more(CommandSender source, Player targetUnchecked) throws WrapperCommandSyntaxException {
        val player = EssentialsUtil.checkPlayerSuggestion(source, targetUnchecked);
        val item = player.getInventory().getItemInMainHand();

        if (item.getType().isEmpty()) throw Exceptions.ERROR_HOLDS_NOTHING_IN_HAND.create(player);
        if (item.getMaxStackSize() == 1) throw Exceptions.ERROR_CANNOT_STACK_ITEMSTACK.create(item);

        item.setAmount(item.getMaxStackSize());

        EssentialsUtil.sendSuccess(source, Component.text("Das Item ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(item))
                .append(Component.text(" wurde ", Colors.SUCCESS))
                .append(Component.text("%dx".formatted(item.getMaxStackSize()), Colors.TERTIARY))
                .append(Component.text(" für ", Colors.SUCCESS))
                .append(EssentialsUtil.getDisplayName(player))
                .append(Component.text(" gestackt!", Colors.SUCCESS)));
        return item.getMaxStackSize();
    }
}
