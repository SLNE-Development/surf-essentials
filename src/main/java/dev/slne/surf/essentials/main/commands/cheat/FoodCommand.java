package dev.slne.surf.essentials.main.commands.cheat;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import dev.slne.surf.essentials.main.utils.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Sound;

import java.util.Collection;
import java.util.Collections;

@PermissionTag(name = Permissions.FEED_SELF_PERMISSION, desc = "Allows you to feed yourself")
@PermissionTag(name = Permissions.FEED_OTHER_PERMISSION, desc = "Allows you to feed other players")
public class FoodCommand {

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("feed", FoodCommand::literal).setUsage("/feed [<players>]")
                .setDescription("feeds the players");
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
            SurfApi.getUser(target.getUUID()).thenAcceptAsync(user -> {
                user.playSound(Sound.ENTITY_STRIDER_EAT, 1f, 0f);
                user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Du wurdest gefüttert!", SurfColors.GREEN)));
            });
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
