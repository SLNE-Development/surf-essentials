package dev.slne.surf.essentials.commands.cheat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
public class HatCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"hat"};
    }

    @Override
    public String usage() {
        return "/hat";
    }

    @Override
    public String description() {
        return "Puts the item in your main hand on your head";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.HAT_SELF_PERMISSION));
        literal.executes(context -> hat(context.getSource(), context.getSource().getPlayerOrException()));

        literal.then(Commands.argument("player", EntityArgument.player())
                .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.HAT_OTHER_PERMISSION))
                .executes(context -> hat(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private int hat(CommandSourceStack source, ServerPlayer playerUnchecked) throws CommandSyntaxException {
        final var player = EssentialsUtil.checkPlayerSuggestion(source, playerUnchecked);
        final var playerInventory = player.getInventory();
        final var itemStackInMainHand = player.getMainHandItem();
        final var itemStackOnHead = playerInventory.getArmor(EquipmentSlot.HEAD.getIndex());

        if(itemStackInMainHand.is(Items.AIR)) throw ERROR_NO_ITEM.create(player.getName().getString());

        playerInventory.setItem(playerInventory.selected, itemStackOnHead);
        playerInventory.setItem(playerInventory.getContainerSize() - 2, itemStackInMainHand);

        if (source.isPlayer()) {
            EssentialsUtil.sendSuccess(source, player.adventure$displayName.colorIfAbsent(Colors.TERTIARY)
                    .append(Component.text(" hat das Item ", Colors.SUCCESS)
                            .append(PaperAdventure.asAdventure(itemStackInMainHand.getDisplayName()).colorIfAbsent(Colors.TERTIARY))
                            .append(Component.text(" aufgesetzt bekommen.", Colors.SUCCESS))));
        }else {
            source.sendSuccess(player.getDisplayName()
                    .copy().append(net.minecraft.network.chat.Component.literal(" has put on the item ")
                            .withStyle(ChatFormatting.GREEN)
                            .append(itemStackInMainHand.getDisplayName())), false);
        }

        return 1;
    }

    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType((entityName) ->
            net.minecraft.network.chat.Component.translatable("commands.enchant.failed.itemless", entityName));
}
