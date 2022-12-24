package dev.slne.surf.essentials.main.commands.general.other.troll.trolls;

import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class WaterTroll {

    public static ArrayList<Player> playersInTroll = new ArrayList<>();

    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> water(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(stack -> stack.getBukkitSender().hasPermission("surf.essentials.commands.troll.water"));
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> waterTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity().getPlayer(), 60))
                .then(Commands.argument("time", IntegerArgumentType.integer(1, 3600))
                        .executes(context -> waterTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity().getPlayer(),
                                IntegerArgumentType.getInteger(context, "time"))));
    }

    private static int waterTroll(CommandContext<CommandSourceStack> context, Player target, int timeInSeconds) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        if (playersInTroll.contains(target)){
            cancelWater(target);

            if (source.isPlayer()){
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(target.displayName().colorIfAbsent(SurfColors.YELLOW))
                        .append(Component.text(" hat jetzt keine Wasserphobie mehr", SurfColors.SUCCESS))));
            }else{
                source.sendSuccess(EntityArgument.getPlayer(context, "player").getDisplayName()
                        .copy().append(net.minecraft.network.chat.Component.literal(" now no longer has water phobia")
                                .withStyle(ChatFormatting.GREEN)), false);
            }
            return 1;
        }

        playersInTroll.add(target);

        Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), () -> playersInTroll.remove(target), 20L * timeInSeconds);

        //success message
        if (source.isPlayer()){
            SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(target.displayName().colorIfAbsent(SurfColors.YELLOW))
                    .append(Component.text(" hat nun Wasserphobie!", SurfColors.SUCCESS))));
        }else{
            source.sendSuccess(EntityArgument.getPlayer(context, "player").getDisplayName()
                    .copy().append(net.minecraft.network.chat.Component.literal(" now has water phobia!")
                            .withStyle(ChatFormatting.GREEN)), false);
        }
        return 1;
    }

    public static void cancelWater(Player player){
        playersInTroll.remove(player);
    }

}
