package dev.slne.surf.essentials.commands.general.other.troll.trolls;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.abtract.CraftUtil;
import dev.slne.surf.essentials.utils.color.Colors;
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
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> waterTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity().getPlayer(), 60))
                .then(Commands.argument("time", IntegerArgumentType.integer(1, 3600))
                        .executes(context -> waterTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity().getPlayer(),
                                IntegerArgumentType.getInteger(context, "time"))));
    }

    private static int waterTroll(CommandContext<CommandSourceStack> context, Player target, int timeInSeconds) throws CommandSyntaxException {
        EssentialsUtil.checkPlayerSuggestion(context.getSource(), CraftUtil.toServerPlayer(target));
        CommandSourceStack source = context.getSource();

        if (playersInTroll.contains(target)){
            cancelWater(target);

            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, target.displayName().colorIfAbsent(Colors.TERTIARY)
                        .append(Component.text(" hat jetzt keine Wasserphobie mehr", Colors.SUCCESS)));
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
            EssentialsUtil.sendSuccess(source, target.displayName().colorIfAbsent(Colors.TERTIARY)
                    .append(Component.text(" hat nun Wasserphobie!", Colors.SUCCESS)));
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
