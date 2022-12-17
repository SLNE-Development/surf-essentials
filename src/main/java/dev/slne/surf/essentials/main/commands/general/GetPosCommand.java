package dev.slne.surf.essentials.main.commands.general;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;

public class GetPosCommand {
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("getpos", GetPosCommand::literal);
        SurfEssentials.registerPluginBrigadierCommand("position", GetPosCommand::literal);
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(stack -> stack.getBukkitSender().hasPermission("surf.essentials.commands.getpos"));
        literal.executes(context -> getpos(context.getSource(), context.getSource().getPlayerOrException()));
        literal.then(Commands.argument("player", EntityArgument.player())
                .executes(context -> getpos(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private static int getpos(CommandSourceStack source, Player player){
        double posX = Math.round(player.getX());
        double posY = Math.round(player.getY());
        double posZ = Math.round(player.getZ());

        if (source.isPlayer()){
            SurfApi.getUser(player.getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Die Position von ", SurfColors.INFO))
                    .append(Bukkit.getPlayer(player.getUUID()).displayName())
                    .append(Component.text(" ist: ", SurfColors.INFO))
                    .append(Component.text("%s, %s, %s".formatted(posX, posY, posZ), SurfColors.TERTIARY))));
        }else {
            source.sendSuccess(player.getDisplayName()
                    .copy().append(net.minecraft.network.chat.Component.literal("'s position: ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(net.minecraft.network.chat.Component.literal(posX + ", " + posY + ", " + posZ)
                                    .withStyle(ChatFormatting.GOLD))), false);
        }
        return 1;
    }
}
