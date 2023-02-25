package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import dev.slne.surf.essentials.utils.brigadier.BrigadierCommand;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Collections;

public class ClearInventoryCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"clear", "clearinventory"};
    }

    @Override
    public String usage() {
        return "/clear [<targets>]";
    }

    @Override
    public String description() {
        return "Clears the inventories from the targets";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.CLEAR_SELF_PERMISSION));
        literal.executes(context -> clear(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException())));

        literal.then(Commands.argument("players", EntityArgument.players())
                .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.CLEAR_OTHER_PERMISSION))
                .executes(context -> clear(context.getSource(), EntityArgument.getPlayers(context, "players"))));
    }

    private int clear(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked) throws CommandSyntaxException{
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int successfulClears = 0;

        for (ServerPlayer target : targets) {
            target.getInventory().clearContent();
            successfulClears ++;
        }

        if (source.isPlayer()){
            if (successfulClears == 1){
                EssentialsUtil.sendSuccess(source, Component.text("Das Inventar von ", SurfColors.SUCCESS)
                        .append(targets.iterator().next().adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                        .append(Component.text(" wurde geleert.", SurfColors.SUCCESS)));
            }else {
                EssentialsUtil.sendSuccess(source, Component.text("Das Inventar von ", SurfColors.SUCCESS)
                        .append(Component.text(successfulClears, SurfColors.TERTIARY))
                        .append(Component.text(" Spielern wurde geleert.", SurfColors.SUCCESS)));
            }
        }else {
            if (successfulClears == 1){
                source.sendSuccess(net.minecraft.network.chat.Component.literal("Cleared ")
                        .withStyle(ChatFormatting.GREEN)
                        .append(targets.iterator().next().getDisplayName())
                        .append(net.minecraft.network.chat.Component.literal("´s inventory")
                                .withStyle(ChatFormatting.GREEN)), false);
            }else {
                source.sendSuccess(net.minecraft.network.chat.Component.literal("Cleared the inventory from " + successfulClears + " players")
                        .withStyle(ChatFormatting.GREEN), false);
            }
        }
        return successfulClears;
    }
}
