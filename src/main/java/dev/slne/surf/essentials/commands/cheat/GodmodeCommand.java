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

import java.util.Collection;
import java.util.Collections;

public class GodmodeCommand {

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("godmode", GodmodeCommand::literal);
        SurfEssentials.registerPluginBrigadierCommand("god", GodmodeCommand::literal);
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
            EssentialsUtil.sendSuccess(target, (Component.text("Du bist nun ", Colors.GREEN))
                    .append(Component.text(target.isInvulnerable() ? "unverwundbar!" : "verwundbar!", Colors.GREEN)));
        }

        ServerPlayer target = targets.iterator().next();
        if (source.isPlayer()){
            if (successfulChanges == 1 && source.getPlayerOrException() != targets.iterator().next()){
                EssentialsUtil.sendSuccess(source, target.adventure$displayName.colorIfAbsent(Colors.TERTIARY)
                        .append(Component.text(" ist nun ", Colors.SUCCESS))
                        .append(Component.text(target.isInvulnerable() ? "unverwundbar!" : "verwundbar!", Colors.SUCCESS)));
            }else if (successfulChanges >= 1 && source.getPlayerOrException() != targets.iterator().next()){
                EssentialsUtil.sendSuccess(source, Component.text(successfulChanges, Colors.TERTIARY)
                        .append(Component.text(" Spieler sind nun ", Colors.SUCCESS))
                        .append(Component.text(enable ? "unverwundbar!" : "verwundbar!", Colors.SUCCESS)));
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
