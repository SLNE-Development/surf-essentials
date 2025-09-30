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

public class HatCommand extends EssentialsCommand {
    public HatCommand() {
        super("hat", "hat <player>", "Puts the item in the players main hand on the players hat");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.HAT_SELF_PERMISSION, Permissions.HAT_OTHER_PERMISSION));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> hat(sender.getCallee(), getPlayerOrException(sender)));

        then(playerArgument("player")
                .withPermission(Permissions.HAT_OTHER_PERMISSION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> hat(sender.getCallee(), args.getUnchecked("player"))));
    }


    private int hat(CommandSender source, Player playerUnchecked) throws WrapperCommandSyntaxException {
        val player = EssentialsUtil.checkPlayerSuggestion(source, playerUnchecked);
        val playerInventory = player.getInventory();
        val itemStackInMainHand = playerInventory.getItemInMainHand();
        val itemStackOnHead = playerInventory.getHelmet();

        if (itemStackInMainHand.getType().isEmpty()) throw Exceptions.ERROR_HOLDS_NOTHING_IN_HAND.create(player);

        playerInventory.setItemInMainHand(itemStackOnHead);
        playerInventory.setHelmet(itemStackInMainHand);

        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(player)
                .append(Component.text(" hat das Item ", Colors.SUCCESS)
                        .append(EssentialsUtil.getDisplayName(itemStackInMainHand))
                        .append(Component.text(" aufgesetzt bekommen.", Colors.SUCCESS))));

        return 1;
    }
}
