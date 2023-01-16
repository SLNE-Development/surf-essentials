package dev.slne.surf.essentials.main.commands.cheat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Collections;

public class FoodCommand {
    public static String PERMISSION;

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("feed", FoodCommand::literal).setUsage("/feed [<players>]")
                .setDescription("feeds the players");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, PERMISSION));

        literal.executes(context -> feed(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException())));
        literal.then(Commands.argument("players", EntityArgument.players())
                .executes(context -> feed(context.getSource(), EntityArgument.getPlayers(context, "players"))));
    }

    private static int feed(CommandSourceStack source, Collection<ServerPlayer> targets)throws CommandSyntaxException{
        int successfulFeeds = 0;

        for (ServerPlayer target : targets) {
            target.getFoodData().setFoodLevel(EssentialsUtil.MAX_FOOD);
            successfulFeeds ++;
            //TODO: Add food sound
            SurfApi.getUser(target.getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du wurdest gefüttert!", SurfColors.GREEN))));
        }

        if(source.isPlayer()){
            if (successfulFeeds == 1 && source.getPlayerOrException() != targets.iterator().next()){
                EssentialsUtil.sendSuccess(source, targets.iterator().next().adventure$displayName.colorIfAbsent(SurfColors.TERTIARY)
                        .append(Component.text(" wurde gefüttert!", SurfColors.SUCCESS)));
            }else if (successfulFeeds >= 1 && source.getPlayerOrException() != targets.iterator().next()){
                EssentialsUtil.sendSuccess(source, Component.text(successfulFeeds, SurfColors.TERTIARY)
                        .append(Component.text(" Spieler wurden gefüttert!", SurfColors.SUCCESS)));
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
