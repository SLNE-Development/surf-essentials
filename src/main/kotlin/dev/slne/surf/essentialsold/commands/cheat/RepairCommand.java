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
import org.bukkit.inventory.meta.Damageable;

public class RepairCommand extends EssentialsCommand {
    public RepairCommand() {
        super("repair", "repair [<player>]", "Repairs the players main-hand-item");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.REPAIR_SELF_PERMISSION, Permissions.REPAIR_OTHER_PERMISSION));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> repair(sender.getCallee(), getPlayerOrException(sender)));
        then(playerArgument("player")
                .withPermission(Permissions.REPAIR_OTHER_PERMISSION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> repair(sender.getCallee(), args.getUnchecked("player"))));
    }

    public int repair(CommandSender source, Player targetUnchecked) throws WrapperCommandSyntaxException {
        val target = EssentialsUtil.checkPlayerSuggestion(source, targetUnchecked);
        val item = target.getInventory().getItemInMainHand();

        if (!item.editMeta(Damageable.class, damageable -> damageable.setDamage(0))) throw Exceptions.ERROR_NOT_DAMAGEABLE_ITEMSTACK.create(item);

        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(item)
                .append(Component.text(" von ", Colors.SUCCESS))
                .append(EssentialsUtil.getDisplayName(target))
                .append(Component.text(" wurde repariert!", Colors.SUCCESS)));

        return 1;
    }
}
