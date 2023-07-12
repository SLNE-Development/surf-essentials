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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class ClearItemCommand extends EssentialsCommand {
    public ClearItemCommand() {
        super("clearitem", "clearitem <item> [<targets>]", "removes a specific item from the targets' inventories");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.CLEAR_ITEM_SELF_PERMISSION, Permissions.CLEAR_ITEM_OTHER_PERMISSION));

        then(itemStackPredicateArgument("item")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> clearItem(sender.getCallee(), args.getUnchecked("item"), List.of(getPlayerOrException(sender))))
                .then(playersArgument("targets")
                        .withPermission(Permissions.CLEAR_ITEM_OTHER_PERMISSION)
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> clearItem(sender.getCallee(), args.getUnchecked("item"), args.getUnchecked("targets")))));
    }

    private int clearItem(CommandSender source, Predicate<ItemStack> itemStackPredicate, Collection<Player> targetsUnchecked) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int successfullyRemoved = 0;

        for (HumanEntity target : targets) {
            val inventory = target.getInventory();

            for (ItemStack stack : inventory) {
                if (itemStackPredicate.test(stack)) inventory.remove(stack);
            }

            successfullyRemoved++;
        }

        if (successfullyRemoved == 1) {
            EssentialsUtil.sendSuccess(source, Component.text("Die Items ", Colors.SUCCESS)
                    .append(Component.text(" wurde erfolgreich aus dem Inventar von ", Colors.SUCCESS))
                    .append(EssentialsUtil.getDisplayName(targets.iterator().next()))
                    .append(Component.text(" entfernt!", Colors.SUCCESS)));
        } else {
            EssentialsUtil.sendSuccess(source, Component.text("Die Items ", Colors.SUCCESS)
                    .append(Component.text(" wurde erfolgreich aus ", Colors.SUCCESS))
                    .append(Component.text(successfullyRemoved, Colors.TERTIARY))
                    .append(Component.text(" Inventaren entfernt!", Colors.SUCCESS)));

        }
        return successfullyRemoved;
    }
}
