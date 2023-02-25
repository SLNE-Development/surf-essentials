package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import dev.slne.surf.essentials.utils.brigadier.BrigadierCommand;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Collections;

public class ClearItemCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"clearitem", "itemclear"};
    }

    @Override
    public String usage() {
        return "/clearitem <item> [<targets]";
    }

    @Override
    public String description() {
        return "removes a specific item from the targets' inventories";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.CLEAR_ITEM_SELF_PERMISSION));

        literal.then(Commands.argument("item", ItemArgument.item(EssentialsUtil.buildContext()))
                .executes(context -> clearItem(context.getSource(), ItemArgument.getItem(context, "item"), Collections.singleton(context.getSource().getPlayerOrException())))
                .then(Commands.argument("players", EntityArgument.players())
                        .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.CLEAR_ITEM_OTHER_PERMISSION))
                        .executes(context -> clearItem(context.getSource(), ItemArgument.getItem(context, "item"), EntityArgument.getPlayers(context, "players")))));
    }

    private int clearItem(CommandSourceStack source, ItemInput itemInput, Collection<ServerPlayer> targets) throws CommandSyntaxException {
        Collection<ServerPlayer> targetsChecked = EssentialsUtil.checkPlayerSuggestion(source, targets);

        int successfullyRemoved = 0;
        for (ServerPlayer target : targetsChecked) {
            for (ItemStack content : target.getInventory().getContents()) {
                if (content.is(itemInput.getItem())) content.setCount(0);
            }
            successfullyRemoved ++;
        }

        if (successfullyRemoved == 1){
            if (source.isPlayer()) {
                EssentialsUtil.sendSuccess(source, Component.text("Das Item ", SurfColors.SUCCESS)
                        .append(PaperAdventure.asAdventure(itemInput.getItem().getDefaultInstance().getDisplayName()))
                        .append(Component.text(" wurde erfolgreich aus dem Inventar von ", SurfColors.SUCCESS))
                        .append(targetsChecked.iterator().next().adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                        .append(Component.text(" entfernt!", SurfColors.SUCCESS)));
            }else {
                source.sendSuccess(itemInput.getItem().getDefaultInstance().getDisplayName()
                        .copy().append(" was successful removed from ")
                        .withStyle(ChatFormatting.GREEN)
                        .append(targetsChecked.iterator().next().getDisplayName())
                        .append("´s inventory")
                        .withStyle(ChatFormatting.GREEN), false);
            }
        }else {
            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, Component.text("Das Item ", SurfColors.SUCCESS)
                        .append(PaperAdventure.asAdventure(itemInput.getItem().getDefaultInstance().getDisplayName()))
                        .append(Component.text(" wurde erfolgreich aus ", SurfColors.SUCCESS))
                        .append(Component.text(successfullyRemoved, SurfColors.TERTIARY))
                        .append(Component.text(" Inventaren entfernt!", SurfColors.SUCCESS)));
            }else {
                source.sendSuccess(itemInput.getItem().getDefaultInstance().getDisplayName()
                        .plainCopy().append(net.minecraft.network.chat.Component.literal(" was successful removed from " + successfullyRemoved + " inventories")
                        .withStyle(ChatFormatting.GREEN)), false);
            }
        }
        return successfullyRemoved;
    }
}
