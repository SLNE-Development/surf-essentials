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

public class FlyCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"fly", "flymode"};
    }

    @Override
    public String usage() {
        return "/fly [<players>] [<enable | disable>]";
    }

    @Override
    public String description() {
        return "Toggles the fly mode";
    }

    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.FLY_SELF_PERMISSION, Permissions.FLY_OTHER_PERMISSION));

        literal.executes(context -> fly(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), true, true));

        literal.then(Commands.argument("players", EntityArgument.players())
                .requires(EssentialsUtil.checkPermissions(Permissions.FLY_OTHER_PERMISSION))
                .executes(context -> fly(context.getSource(), EntityArgument.getPlayers(context, "players"), true, true))
                .then(Commands.literal("enable")
                        .executes(context -> fly(context.getSource(), EntityArgument.getPlayers(context, "players"), false, true)))
                .then(Commands.literal("disable")
                        .executes(context -> fly(context.getSource(), EntityArgument.getPlayers(context, "players"), false, false))));
    }

    private int fly(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked, boolean toggle, Boolean allowFly) throws CommandSyntaxException {
        final var targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int successfulChanges = 0;

        for (ServerPlayer target : targets) {
            if (toggle) {
                var shouldFly = target.getBukkitEntity().getAllowFlight();
                target.getBukkitEntity().setAllowFlight(!shouldFly);
                target.getBukkitEntity().setFlying(!shouldFly);
            } else {
                target.getBukkitEntity().setAllowFlight(allowFly);
                target.getBukkitEntity().setFlying(allowFly);
            }
            successfulChanges++;
            EssentialsUtil.sendSuccess(target, Component.text("Du kannst nun ", Colors.GREEN)
                    .append(Component.text((target.getAbilities().mayfly) ? "fliegen!" : "nicht mehr fliegen!", Colors.GREEN)));
        }

        if (successfulChanges == 1) {
            if (source.isPlayer() && source.getPlayerOrException() == targets.iterator().next()) return 1;
            EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(targets.iterator().next())
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
        return successfulChanges;

    }
}
