package dev.slne.surf.essentials.main.commands.cheat;

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
import aetherial.spigot.plugin.annotation.permission.PermissionTag;

import java.util.Collection;
import java.util.Collections;

@PermissionTag(name = Permissions.GOD_MODE_SELF_PERMISSION, desc = "Allows you to make yourself invulnerable")
@PermissionTag(name = Permissions.GOD_MODE_OTHER_PERMISSION, desc = "Allows you to make others invulnerable")
public class GodmodeCommand {

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("godmode", GodmodeCommand::literal).setUsage("/godmode [<enable | disable>] [<players>]")
                .setDescription("makes the targets invulnerable");
        SurfEssentials.registerPluginBrigadierCommand("god", GodmodeCommand::literal).setUsage("/godmode [<enable | disable>] [<players>]")
                .setDescription("makes the targets invulnerable");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.GOD_MODE_SELF_PERMISSION));

        literal.executes(context -> godmode(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), !context.getSource().getPlayerOrException().isInvulnerable()));
        literal.then(Commands.literal("enable")
                .then(Commands.argument("players", EntityArgument.players())
                        .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.GOD_MODE_OTHER_PERMISSION))
                        .executes(context -> godmode(context.getSource(), EntityArgument.getPlayers(context, "players"), true))));

        literal.then(Commands.literal("disable")
                .then(Commands.argument("players", EntityArgument.players())
                        .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.GOD_MODE_OTHER_PERMISSION))
                        .executes(context -> godmode(context.getSource(), EntityArgument.getPlayers(context, "players"), false))));
    }

    private static int godmode(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked, boolean enable) throws CommandSyntaxException{
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int successfulChanges = 0;

        for (ServerPlayer target : targets) {
            target.setInvulnerable(enable);
            successfulChanges ++;
            SurfApi.getUser(target.getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du bist nun ", SurfColors.GREEN))
                    .append(Component.text(target.isInvulnerable() ? "unverwundbar!" : "verwundbar!", SurfColors.GREEN))));
        }

        ServerPlayer target = targets.iterator().next();
        if (source.isPlayer()){
            if (successfulChanges == 1 && source.getPlayerOrException() != targets.iterator().next()){
                EssentialsUtil.sendSuccess(source, target.adventure$displayName.colorIfAbsent(SurfColors.TERTIARY)
                        .append(Component.text(" ist nun ", SurfColors.SUCCESS))
                        .append(Component.text(target.isInvulnerable() ? "unverwundbar!" : "verwundbar!", SurfColors.SUCCESS)));
            }else if (successfulChanges >= 1 && source.getPlayerOrException() != targets.iterator().next()){
                EssentialsUtil.sendSuccess(source, Component.text(successfulChanges, SurfColors.TERTIARY)
                        .append(Component.text(" Spieler sind nun ", SurfColors.SUCCESS))
                        .append(Component.text(enable ? "unverwundbar!" : "verwundbar!", SurfColors.SUCCESS)));
            }
        }else {
            if (successfulChanges == 1){
                source.sendSuccess(target.getDisplayName()
                        .copy().append(net.minecraft.network.chat.Component.literal(" is now " + (target.isInvulnerable() ? "invulnerable" : "vulnerable"))), false);
            }else {
                source.sendSuccess(net.minecraft.network.chat.Component.literal(successfulChanges + " players are now " + (enable ? "invulnerable" : "vulnerable")), false);
            }
        }
        return successfulChanges;
    }
}
