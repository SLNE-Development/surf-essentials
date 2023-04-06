package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Collections;

public class RuleCommand {

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("rule", RuleCommand::literal);
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(0, Permissions.RULE_SELF_PERMISSION));
        literal.executes(context -> sendRules(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException())));

        literal.then(Commands.argument("players", EntityArgument.players())
                .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.RULE_OTHER_PERMISSION))
                .executes(context -> sendRules(context.getSource(), EntityArgument.getPlayers(context, "players"))));
    }

    private static int sendRules(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked) throws CommandSyntaxException{
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
        if (source.isPlayer()){
            if (successfulSends == 1 && !(targets.iterator().next() == source.getPlayerOrException())){
                EssentialsUtil.sendSuccess(source, Component.text("Die Regeln wurden an ", Colors.SUCCESS)
                        .append(targets.iterator().next().adventure$displayName.colorIfAbsent(Colors.TERTIARY))
                        .append(Component.text(" gesendet!", Colors.SUCCESS)));
            }else if (!(targets.iterator().next() == source.getPlayerOrException())){
                EssentialsUtil.sendSuccess(source, Component.text("Die Regeln wurden an ", Colors.SUCCESS)
                        .append(Component.text(successfulSends, Colors.TERTIARY))
                        .append(Component.text(" Spieler gesendet!", Colors.SUCCESS)));
            }
        }else {
            if (successfulSends == 1){
                source.sendSuccess(targets.iterator().next().getDisplayName()
                        .copy().append(" has received the rules.")
                        .withStyle(ChatFormatting.GREEN), false);
            }else {
                source.sendSuccess(net.minecraft.network.chat.Component.literal(String.valueOf(targets.size()))
                                .withStyle(ChatFormatting.GOLD)
                        .append(" players have received the rules.")
                        .withStyle(ChatFormatting.GREEN), false);
            }
        }
        return 1;
    }
}
