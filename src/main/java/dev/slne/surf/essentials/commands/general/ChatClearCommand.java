package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Collections;

public class ChatClearCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"chatclear", "cc"};
    }

    @Override
    public String usage() {
        return "/chatclear [<players>]";
    }

    @Override
    public String description() {
        return "Clears the chat from the targets except they have the bypass permission";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.CHAT_CLEAR_SELF_PERMISSION));
        literal.executes(context -> clearChat(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException())));

        literal.then(Commands.argument("players", EntityArgument.players())
                .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.CHAT_CLEAR_OTHER_PERMISSION))
                .executes(context -> clearChat(context.getSource(), EntityArgument.getPlayers(context, "players"))));
    }

    private int clearChat(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked) throws CommandSyntaxException{
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int successfulClears = 0;
        int countEmptyLines = 100;
        ClientboundSystemChatPacket emptyChatPacket = new ClientboundSystemChatPacket(net.minecraft.network.chat.Component.empty(), false);

        for (ServerPlayer target : targets) {
            if (target.getBukkitEntity().hasPermission(Permissions.CHAT_CLEAR_BYPASS_PERMISSION)) continue;

            for (int i = 0; i < countEmptyLines; i++) {
                target.connection.send(emptyChatPacket);
            }
            successfulClears++;

            EssentialsUtil.sendSuccess(target, "Dein Chat wurde gelöscht!");
        }

        if (successfulClears == 1){
            if (source.isPlayer()){
                if (targets.iterator().next() != source.getPlayerOrException()){
                    EssentialsUtil.sendSuccess(source, Component.text("Der Chat von ", Colors.SUCCESS)
                            .append(targets.iterator().next().adventure$displayName.colorIfAbsent(Colors.TERTIARY))
                            .append(Component.text(" wurde gelöscht.", Colors.SUCCESS)));
                }
            }else {
                source.sendSuccess(net.minecraft.network.chat.Component.literal("Cleared the chat from ")
                        .withStyle(ChatFormatting.GREEN)
                        .append(targets.iterator().next().getDisplayName()), false);
            }
        }else {
            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, Component.text("Der Chat von ", Colors.SUCCESS)
                        .append(Component.text(successfulClears, Colors.TERTIARY))
                        .append(Component.text(" Spielern wurde gelöscht.", Colors.SUCCESS)));
            }else {
                source.sendSuccess(net.minecraft.network.chat.Component.literal("Cleared the chat from " + successfulClears + " players")
                        .withStyle(ChatFormatting.GREEN), false);
            }
        }
        return successfulClears;
    }
}
