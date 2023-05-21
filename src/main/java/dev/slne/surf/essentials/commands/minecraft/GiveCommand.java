package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;


public class GiveCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"give"};
    }

    @Override
    public String usage() {
        return "/give <targets> <item> [<amount>]";
    }

    @Override
    public String description() {
        return "Gives the targets the items";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(EssentialsUtil.checkPermissions(Permissions.GIVE_PERMISSION));
        literal.then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("item", ItemArgument.item(this.commandBuildContext))
                        .executes(context -> give(context.getSource(), EntityArgument.getPlayers(context, "targets"), ItemArgument.getItem(context, "item"), 1))

                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> give(context.getSource(), EntityArgument.getPlayers(context, "targets"), ItemArgument.getItem(context, "item"),
                                        IntegerArgumentType.getInteger(context, "amount"))))));
    }

    private static int give(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked, ItemInput item, int amount) throws CommandSyntaxException {
        var targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int maxStackSize = item.getItem().getMaxStackSize();
        int maxGiveSize = maxStackSize*100;

        if (amount > maxGiveSize){
            source.sendFailure(net.minecraft.network.chat.Component.translatable("commands.give.failed.toomanyitems", maxGiveSize, item.createItemStack(amount, false).getDisplayName()));
            return 0;
        }

        int countdownAmount = amount;
        while (countdownAmount > 0) {
            for (ServerPlayer target : targets) {
                if (countdownAmount <= 0) continue;

                int min = Math.min(maxStackSize, countdownAmount);
                countdownAmount -= min;

                ItemStack itemStack = item.createItemStack(min, false);
                boolean successful = target.getInventory().add(itemStack);
                ItemEntity entityItem;

                if (successful && itemStack.isEmpty()) {
                    itemStack.setCount(1);
                    entityItem = target.drop(itemStack, false, false, false);

                    if (entityItem != null) {
                        entityItem.makeFakeItem();
                    }

                    target.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ITEM_PICKUP,
                            SoundSource.PLAYERS, 0.2F, ((target.getRandom().nextFloat() - target.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    target.containerMenu.broadcastChanges();

                } else {
                    entityItem = target.drop(itemStack, false);

                    if (entityItem != null) {
                        entityItem.setNoPickUpDelay();
                        entityItem.setTarget(target.getUUID());
                    }
                }

            }
        }

        if (source.isPlayer()){
            if (targets.size() == 1) {
                EssentialsUtil.sendSuccess(source, (targets.iterator().next().adventure$displayName.colorIfAbsent(Colors.TERTIARY))
                        .append(Component.text(" hat ", Colors.SUCCESS))
                        .append(Component.text(amount, Colors.TERTIARY))
                        .append(Component.text("x ", Colors.TERTIARY))
                        .append(item.createItemStack(amount, false).getBukkitStack().displayName())
                        .append(Component.text(" erhalten!", Colors.SUCCESS)));

            // If there are multiple targets
            } else {
                EssentialsUtil.sendSuccess(source, (Component.text(targets.size()))
                        .append(Component.text(" Spieler haben ", Colors.SUCCESS))
                        .append(Component.text(amount, Colors.TERTIARY))
                        .append(Component.text("x ", Colors.TERTIARY))
                        .append(item.createItemStack(amount, false).getBukkitStack().displayName())
                        .append(Component.text(" erhalten!", Colors.SUCCESS)));
            }
        }else {
            if (targets.size() == 1) {
                source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.give.success.single", amount,
                        item.createItemStack(amount, false).getDisplayName(), (targets.iterator().next()).getDisplayName()), false);

            } else {
                source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.give.success.single", amount,
                        item.createItemStack(amount, false).getDisplayName(), targets.size()), false);
            }
        }
        return targets.size();
    }
}
