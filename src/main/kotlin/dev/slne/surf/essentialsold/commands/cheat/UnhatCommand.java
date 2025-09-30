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
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UnhatCommand extends EssentialsCommand {
    public UnhatCommand() {
        super("unhat", "unhat", "Puts the item on your head in your inventory");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.UNHAT_SELF_PERMISSION, Permissions.UNHAT_OTHER_PERMISSION));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> unHat(sender.getCallee(), getPlayerOrException(sender)));
        then(playerArgument("player")
                .withPermission(Permissions.UNHAT_OTHER_PERMISSION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> unHat(sender.getCallee(), args.getUnchecked("player"))));
    }

    private int unHat(CommandSender source, Player playerUnchecked) throws WrapperCommandSyntaxException {
        val player = EssentialsUtil.checkPlayerSuggestion(source, playerUnchecked);
        val playerInventory = player.getInventory();
        val itemStackOnHead = playerInventory.getHelmet();
        val freeSlot = playerInventory.firstEmpty();

        if (itemStackOnHead == null || freeSlot == -1) throw Exceptions.NO_SPACE_IN_INVENTORY.create(player);

        playerInventory.setItem(freeSlot, itemStackOnHead);
        playerInventory.setHelmet(new ItemStack(Material.AIR));

        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(player)
                .append(Component.text(" hat das Item ", Colors.SUCCESS)
                        .append(EssentialsUtil.getDisplayName(itemStackOnHead))
                        .append(Component.text(" abgesetzt.", Colors.SUCCESS))));
        return 1;
    }
}
