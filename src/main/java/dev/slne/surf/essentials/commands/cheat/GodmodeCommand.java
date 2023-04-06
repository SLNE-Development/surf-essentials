package dev.slne.surf.essentials.commands.cheat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Collections;

public class GodmodeCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"godmode", "god"};
    }

    @Override
    public String usage() {
        return "/god [<enable | disable> <players>] ";
    }

    @Override
    public String description() {
        return "Change the godmode of players";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.GOD_MODE_SELF_PERMISSION, Permissions.GOD_MODE_OTHER_PERMISSION));

        literal.executes(context -> godmode(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), !context.getSource().getPlayerOrException().isInvulnerable()));
        literal.then(Commands.literal("enable")
                .then(Commands.argument("players", EntityArgument.players())
                        .requires(EssentialsUtil.checkPermissions(Permissions.GOD_MODE_OTHER_PERMISSION))
                        .executes(context -> godmode(context.getSource(), EntityArgument.getPlayers(context, "players"), true))));

        literal.then(Commands.literal("disable")
                .then(Commands.argument("players", EntityArgument.players())
                        .requires(EssentialsUtil.checkPermissions(Permissions.GOD_MODE_OTHER_PERMISSION))
                        .executes(context -> godmode(context.getSource(), EntityArgument.getPlayers(context, "players"), false))));
    }

    private int godmode(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked, boolean enable) throws CommandSyntaxException{
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
                EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(targets.iterator().next())
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
