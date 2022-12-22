package dev.slne.surf.essentials.main.commands.general.other.troll;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class DemoTroll {
    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> demo(@NotNull LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(stack -> stack.getBukkitSender().hasPermission("surf.essentials.commands.troll.demo"));
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> makeDemo(context, EntityArgument.getPlayer(context, "player").getBukkitEntity().getPlayer()));
    }

    private static int makeDemo(@NotNull CommandContext<CommandSourceStack> context, Player target) throws CommandSyntaxException {
        // Get the source of the command
        CommandSourceStack source = context.getSource();
        // Get the ProtocolManager instance
        ProtocolManager manager = SurfEssentials.manager();
        // Create a PacketContainer object
        PacketContainer packet = manager.createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);

        // Set the game state ID to 5 and the value to 0
        packet.getGameStateIDs().write(0, 5);
        packet.getFloat().write(0, 0F);

        // Play a sound for the target player
        SurfApi.getUser(target).thenAcceptAsync(user -> user.playSound(Sound.ENTITY_ITEM_PICKUP, 2F, 1F));
        // Send the packet to the target player
        try {
            manager.sendServerPacket(target, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // Send a message to the source of the command
        if (source.isPlayer()){
            SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(target.displayName().colorIfAbsent(SurfColors.YELLOW))
                    .append(Component.text(" wurde die Demo gezeigt!", SurfColors.SUCCESS))));
        }else{
            source.sendSuccess(EntityArgument.getPlayer(context, "player").getDisplayName()
                    .copy().append(net.minecraft.network.chat.Component.literal(" was shown the demo!")
                            .withStyle(ChatFormatting.GREEN)), false);
        }
        return 1;
    }
}
