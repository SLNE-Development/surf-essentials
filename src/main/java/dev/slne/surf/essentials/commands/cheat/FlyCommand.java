package dev.slne.surf.essentials.commands.cheat;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Collections;

@PermissionTag(name = Permissions.FLY_SELF_PERMISSION, desc = "Allows you to toggle the fly mode for yourself")
@PermissionTag(name = Permissions.FLY_OTHER_PERMISSION, desc = "Allows you to toggle the fly mode for others")
public class FlyCommand {

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("fly", FlyCommand::literal).setUsage("/fly [<players>] [<enable | disable>]")
                .setDescription("Toggles the fly mode for the targets");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.FLY_SELF_PERMISSION));

        literal.executes(context -> fly(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), true, true));

        literal.then(Commands.argument("players", EntityArgument.players())
                .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.FLY_OTHER_PERMISSION))
                .executes(context -> fly(context.getSource(), EntityArgument.getPlayers(context, "players"), true, true))
                .then(Commands.literal("enable")
                        .executes(context -> fly(context.getSource(), EntityArgument.getPlayers(context, "players"), false, true)))
                .then(Commands.literal("disable")
                        .executes(context -> fly(context.getSource(), EntityArgument.getPlayers(context, "players"), false, false))));
    }

    private static int fly(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked, boolean toggle, Boolean allowFly) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int successfulChanges = 0;

        for (ServerPlayer target : targets) {
            if (toggle) {
                target.getAbilities().mayfly = !target.getAbilities().mayfly;
                target.getAbilities().flying = !target.getAbilities().flying;
            } else {
                target.getAbilities().mayfly = allowFly;
                target.getAbilities().flying = allowFly;
            }
            target.onUpdateAbilities();
            successfulChanges++;
            SurfApi.getUser(target.getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du kannst nun ", SurfColors.GREEN)
                            .append(Component.text((target.getAbilities().mayfly) ? "fliegen!" : "nicht mehr fliegen!", SurfColors.GREEN)))));
        }

        if (source.isPlayer()) {
            if (successfulChanges == 1) {
                if (source.getPlayerOrException() == targets.iterator().next()) return 1;
                EssentialsUtil.sendSuccess(source, targets.iterator().next().adventure$displayName.colorIfAbsent(SurfColors.TERTIARY)
                        .append(Component.text(" kann nun " + ((targets.iterator().next().getAbilities().mayfly) ? "fliegen!" : "nicht mehr fliegen!"))));
            } else {
                if (toggle) {
                    EssentialsUtil.sendSuccess(source, Component.text("Die Flugfunktion wurde für ", SurfColors.SUCCESS)
                            .append(Component.text(successfulChanges, SurfColors.TERTIARY))
                            .append(Component.text(" Spieler umgeschaltet!", SurfColors.SUCCESS)));
                } else {
                    EssentialsUtil.sendSuccess(source, Component.text("Die Flugfunktion wurde für ", SurfColors.SUCCESS)
                            .append(Component.text(successfulChanges, SurfColors.TERTIARY))
                            .append(Component.text(" Spieler ", SurfColors.SUCCESS))
                            .append(Component.text((allowFly) ? "aktiviert" : "deaktiviert", SurfColors.TERTIARY))
                            .append(Component.text("!", SurfColors.SUCCESS)));
                }
            }
        } else {
            if (toggle) {
                source.sendSuccess(net.minecraft.network.chat.Component.literal("fly mode toggled for " + successfulChanges + " players!"), false);
            } else {
                source.sendSuccess(net.minecraft.network.chat.Component.literal("fly mode " + (allowFly ? "enabled" : "disabled") + " for " + successfulChanges +
                        " players"), false);
            }
        }
        return successfulChanges;

    }
}
