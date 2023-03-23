package dev.slne.surf.essentials.commands.cheat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Sound;

import java.util.Collection;
import java.util.Collections;

public class FoodCommand {

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("feed", FoodCommand::literal);
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.FEED_SELF_PERMISSION));

        literal.executes(context -> feed(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException())));
        literal.then(Commands.argument("players", EntityArgument.players())
                .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.FEED_OTHER_PERMISSION))
                .executes(context -> feed(context.getSource(), EntityArgument.getPlayers(context, "players"))));
    }

    private static int feed(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked)throws CommandSyntaxException{
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int successfulFeeds = 0;

        for (ServerPlayer target : targets) {
            target.getFoodData().setFoodLevel(EssentialsUtil.MAX_FOOD);
            successfulFeeds ++;

            target.getBukkitEntity().playSound(target.getBukkitEntity(), Sound.ENTITY_STRIDER_EAT, 1f, 0f);
            EssentialsUtil.sendSuccess(target, (Component.text("Du wurdest gefüttert!", Colors.GREEN)));

        }

        if(source.isPlayer()){
            if (successfulFeeds == 1 && source.getPlayerOrException() != targets.iterator().next()){
                EssentialsUtil.sendSuccess(source, targets.iterator().next().adventure$displayName.colorIfAbsent(Colors.TERTIARY)
                        .append(Component.text(" wurde gefüttert!", Colors.SUCCESS)));
            }else if (successfulFeeds >= 1 && source.getPlayerOrException() != targets.iterator().next()){
                EssentialsUtil.sendSuccess(source, Component.text(successfulFeeds, Colors.TERTIARY)
                        .append(Component.text(" Spieler wurden gefüttert!", Colors.SUCCESS)));
            }
        }else {
            if (successfulFeeds == 1){
                source.sendSuccess(targets.iterator().next().getDisplayName()
                        .copy().append(net.minecraft.network.chat.Component.literal(" has been fed")), false);
            }else {
                source.sendSuccess(net.minecraft.network.chat.Component.literal(targets.size() + " players have been fed"), false);
            }
        }
        return successfulFeeds;
    }

   
}
