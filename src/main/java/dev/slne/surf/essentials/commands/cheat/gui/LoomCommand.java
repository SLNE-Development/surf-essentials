package dev.slne.surf.essentials.commands.cheat.gui;

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
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.List;

public class LoomCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"loom"};
    }

    @Override
    public String usage() {
        return "/loom [<targets>]";
    }

    @Override
    public String description() {
        return "Opens the loom gui for the targets";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.LOOM_SELF_PERMISSION, Permissions.LOOM_OTHER_PERMISSION));
        literal.executes(context -> open(context.getSource(), List.of(context.getSource().getPlayerOrException())));

        literal.then(Commands.argument("targets", EntityArgument.players())
                .requires(EssentialsUtil.checkPermissions(Permissions.LOOM_OTHER_PERMISSION))
                .executes(context -> open(context.getSource(), EntityArgument.getPlayers(context, "targets"))));
    }

    private static int open(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        for (Player target : targets) {
            target.getBukkitEntity().openLoom(target.getBukkitEntity().getLocation(), true);
        }

        if (targets.size() == 1) {
            EssentialsUtil.sendSuccess(source, Component.text("Der Webstuhl wurde für ", Colors.SUCCESS)
                    .append(targets.iterator().next().adventure$displayName.colorIfAbsent(Colors.TERTIARY))
                    .append(Component.text(" geöffnet", Colors.SUCCESS)));
        } else {
            EssentialsUtil.sendSuccess(source, Component.text("Der Webstuhl wurde für ", Colors.SUCCESS)
                    .append(Component.text(targets.size(), Colors.TERTIARY))
                    .append(Component.text(" Spieler geöffnet", Colors.SUCCESS)));
        }
        return 1;
    }
}
