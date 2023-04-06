package dev.slne.surf.essentials.commands.general.other.troll.trolls;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DemoTroll {
    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> demo(@NotNull LiteralArgumentBuilder<CommandSourceStack> literal){
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> makeDemo(context, EntityArgument.getPlayer(context, "player")));
    }

    private static int makeDemo(@NotNull CommandContext<CommandSourceStack> context, ServerPlayer targetPlayer) throws CommandSyntaxException {
        EssentialsUtil.checkPlayerSuggestion(context.getSource(), targetPlayer);
        // Get the source of the command
        CommandSourceStack source = context.getSource();
        Player target = targetPlayer.getBukkitEntity();
        ClientboundGameEventPacket packet = new ClientboundGameEventPacket(ClientboundGameEventPacket.DEMO_EVENT, 0f);

        targetPlayer.connection.send(packet);

        // Play a sound for the target player
        target.playSound(target.getLocation(), Sound.ENTITY_ITEM_PICKUP, 2F, 1F);

        // Send a message to the source of the command
        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, target.displayName().colorIfAbsent(Colors.YELLOW)
                    .append(Component.text(" wurde die Demo gezeigt!", Colors.SUCCESS)));
        }else{
            source.sendSuccess(EntityArgument.getPlayer(context, "player").getDisplayName()
                    .copy().append(net.minecraft.network.chat.Component.literal(" was shown the demo!")
                            .withStyle(ChatFormatting.GREEN)), false);
        }
        return 1;
    }
}
