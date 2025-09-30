package dev.slne.surf.essentialsold.commands.minecraft;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.SurfEssentials;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.brigadier.Exceptions;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Objects;

public class GiveCommand extends EssentialsCommand {
    public GiveCommand() {
        super("give", "give <targets> <item> [amount]", "Give items to players");

        withPermission(Permissions.GIVE_PERMISSION);

        then(playersArgument("targets")
                .then(itemStackArgument("item")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> give(
                                sender.getCallee(),
                                args.getUnchecked("targets"),
                                Objects.requireNonNull(args.getUnchecked("item")),
                                1
                        ))
                        .then(integerArgument("amount", 1)
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> give(
                                        sender.getCallee(),
                                        args.getUnchecked("targets"),
                                        Objects.requireNonNull(args.getUnchecked("item")),
                                        args.getUnchecked("amount")
                                ))
                        )
                )
        );
    }

    private int give(CommandSender sender, Collection<Player> targetsUnchecked, org.bukkit.inventory.ItemStack item, Integer amount) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(sender, targetsUnchecked);
        int maxStackSize = item.getMaxStackSize();
        int maxAllowedAmount = maxStackSize * 100;

        if (amount > maxAllowedAmount) throw Exceptions.ERROR_GIVE_TOO_MANY_ITEMS.create(maxAllowedAmount, item);

        for (Player target : targets) {
            val world = target.getWorld();
            int remainingAmount = amount;

            while (remainingAmount > 0) {
                int stackSize = Math.min(maxStackSize, remainingAmount);
                val stack = item.clone();
                remainingAmount -= stackSize;
                stack.setAmount(stackSize);

                target.getInventory().addItem(stack).forEach((integer, remaining) -> world.dropItem(target.getLocation(), remaining, item1 -> item1.setPickupDelay(0)));
                stack.setAmount(1);

                world.dropItem(target.getLocation(), stack, item1 -> {
                    item1.setCanPlayerPickup(false);
                    item1.setCanMobPickup(false);
                    Bukkit.getScheduler().runTaskLater(SurfEssentials.getInstance(), () -> item1.setHealth(-1), 2L);
                });

                world.playSound(target, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 0.2f, ((EssentialsUtil.random().nextFloat() - EssentialsUtil.random().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
        }

        if (targets.size() == 1) {
            val target = targets.iterator().next();
            EssentialsUtil.sendSuccess(sender, EssentialsUtil.getDisplayName(target)
                    .append(Component.text(" hat ", Colors.SUCCESS))
                    .append(Component.text(amount, Colors.VARIABLE_VALUE))
                    .append(Component.text("x ", Colors.VARIABLE_VALUE))
                    .append(EssentialsUtil.getDisplayName(item))
                    .append(Component.text(" erhalten!", Colors.SUCCESS)));
        } else {
            EssentialsUtil.sendSuccess(sender, (Component.text(targets.size()))
                    .append(Component.text(" Spieler haben ", Colors.SUCCESS))
                    .append(Component.text(amount, Colors.VARIABLE_VALUE))
                    .append(Component.text("x ", Colors.VARIABLE_VALUE))
                    .append(EssentialsUtil.getDisplayName(item))
                    .append(Component.text(" erhalten!", Colors.SUCCESS)));
        }

        return targets.size();
    }
}
