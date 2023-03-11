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
                target.getBukkitEntity().setAllowFlight(!target.getBukkitEntity().getAllowFlight());
                target.getBukkitEntity().setFlying(!target.getBukkitEntity().isFlying());
            } else {
                target.getBukkitEntity().setAllowFlight(allowFly);
                target.getBukkitEntity().setFlying(allowFly);
            }
            successfulChanges++;
            EssentialsUtil.sendSuccess(target.getBukkitEntity(), (Component.text("Du kannst nun ", Colors.GREEN)
                            .append(Component.text((target.getAbilities().mayfly) ? "fliegen!" : "nicht mehr fliegen!", Colors.GREEN))));
        }

        if (source.isPlayer()) {
            if (successfulChanges == 1) {
                if (source.getPlayerOrException() == targets.iterator().next()) return 1;
                EssentialsUtil.sendSuccess(source, targets.iterator().next().adventure$displayName.colorIfAbsent(Colors.TERTIARY)
                        .append(Component.text(" kann nun " + ((targets.iterator().next().getAbilities().mayfly) ? "fliegen!" : "nicht mehr fliegen!"))));
            } else {
                if (toggle) {
                    EssentialsUtil.sendSuccess(source, Component.text("Die Flugfunktion wurde für ", Colors.SUCCESS)
                            .append(Component.text(successfulChanges, Colors.TERTIARY))
                            .append(Component.text(" Spieler umgeschaltet!", Colors.SUCCESS)));
                } else {
                    EssentialsUtil.sendSuccess(source, Component.text("Die Flugfunktion wurde für ", Colors.SUCCESS)
                            .append(Component.text(successfulChanges, Colors.TERTIARY))
                            .append(Component.text(" Spieler ", Colors.SUCCESS))
                            .append(Component.text((allowFly) ? "aktiviert" : "deaktiviert", Colors.TERTIARY))
                            .append(Component.text("!", Colors.SUCCESS)));
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
