package dev.slne.surf.essentials.commands.cheat.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class AnvilCommand {
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("anvil", AnvilCommand::literal).setUsage("/anvil [<targets>]")
                .setDescription("Opens the anvil gui for the targets");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.ANVIL_SELF_PERMISSION));
        literal.executes(context -> open(context.getSource(), ImmutableList.of(context.getSource().getPlayerOrException())));
        literal.then(Commands.argument("targets", EntityArgument.players())
                .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.ANVIL_OTHER_PERMISSION))
                .executes(context -> open(context.getSource(), EntityArgument.getPlayers(context, "targets"))));
    }

    private static int open(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        for (Player target : targets) {
            target.getBukkitEntity().openAnvil(target.getBukkitEntity().getLocation(), true);
        }
        if (source.isPlayer()){
            if (targets.size() == 1){
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Der Amboss wurde für ", SurfColors.SUCCESS))
                        .append(targets.iterator().next().adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                        .append(Component.text(" geöffnet", SurfColors.SUCCESS))));
            }else {
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Der Amboss wurde für ", SurfColors.SUCCESS))
                        .append(Component.text(targets.size(), SurfColors.TERTIARY))
                        .append(Component.text(" Spieler geöffnet", SurfColors.SUCCESS))));
            }
        }else {
            if (targets.size() == 1){
                source.sendSuccess(net.minecraft.network.chat.Component.literal("Opened anvil for ")
                        .withStyle(ChatFormatting.WHITE)
                        .append(targets.iterator().next().getDisplayName()), false);
            }else{
                source.sendSuccess(net.minecraft.network.chat.Component.literal("Opened anvil for ")
                        .withStyle(ChatFormatting.WHITE)
                        .append(net.minecraft.network.chat.Component.literal(String.valueOf(targets.size()))
                                .withStyle(ChatFormatting.GOLD))
                        .append(net.minecraft.network.chat.Component.literal(" players")
                                .withStyle(ChatFormatting.WHITE)), false);
            }
        }
        return 1;
    }
}
