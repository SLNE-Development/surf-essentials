package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Collections;

public class RuleCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"rule"};
    }

    @Override
    public String usage() {
        return "/rule [<players>]";
    }

    @Override
    public String description() {
        return "Sends the rules to the players";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.RULE_SELF_PERMISSION, Permissions.RULE_OTHER_PERMISSION));
        literal.executes(context -> sendRules(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException())));

        literal.then(Commands.argument("players", EntityArgument.players())
                .requires(EssentialsUtil.checkPermissions(Permissions.RULE_OTHER_PERMISSION))
                .executes(context -> sendRules(context.getSource(), EntityArgument.getPlayers(context, "players"))));
    }

    private int sendRules(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int successfulSends = 0;

        for (ServerPlayer player : targets) {
            EssentialsUtil.sendSuccess(player, Component.text("Alle ", Colors.SUCCESS)
                    .append(Component.text("Regeln", Colors.GOLD))
                    .append(Component.text(" und", Colors.SUCCESS))
                    .append(Component.text(" Informationen", Colors.GOLD))
                    .append(Component.text(" findest du ", Colors.SUCCESS))
                    .append(Component.text("Hier", Colors.RED)
                            .decorate(TextDecoration.BOLD)
                            .hoverEvent(Component.text("Klicke um zu der Website zu kommen", Colors.GRAY))
                            .clickEvent(ClickEvent.openUrl("https://castcrafter.de/subserver"))));
            successfulSends++;
        }

        boolean isSelf = source.isPlayer() && source.getPlayerOrException() == targets.iterator().next();
        if (successfulSends == 1 && !isSelf) {
            EssentialsUtil.sendSuccess(source, Component.text("Die Regeln wurden an ", Colors.SUCCESS)
                    .append(EssentialsUtil.getDisplayName(targets.iterator().next()))
                    .append(Component.text(" gesendet!", Colors.SUCCESS)));
        } else if (!isSelf) {
            EssentialsUtil.sendSuccess(source, Component.text("Die Regeln wurden an ", Colors.SUCCESS)
                    .append(Component.text(successfulSends, Colors.TERTIARY))
                    .append(Component.text(" Spieler gesendet!", Colors.SUCCESS)));
        }

        return 1;
    }
}
