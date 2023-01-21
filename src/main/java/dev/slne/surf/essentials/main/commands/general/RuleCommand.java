package dev.slne.surf.essentials.main.commands.general;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import dev.slne.surf.essentials.main.utils.Permissions;
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

@PermissionTag(name = Permissions.RULE_PERMISSION, desc = "This is the permission for the 'rule' command")
@PermissionTag(name = Permissions.RULE_SELF_PERMISSION, desc = "Allows the player to see the rules, but not send them to other players")
public class RuleCommand {

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("rule", RuleCommand::literal).setUsage("/rule [<players>]")
                .setDescription("Sends you or the targets the server rules");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(0, Permissions.RULE_SELF_PERMISSION));
        literal.executes(context -> sendRules(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException())));

        literal.then(Commands.argument("players", EntityArgument.players())
                .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.RULE_PERMISSION))
                .executes(context -> sendRules(context.getSource(), EntityArgument.getPlayers(context, "players"))));
    }

    private static int sendRules(CommandSourceStack source, Collection<ServerPlayer> targets) throws CommandSyntaxException{
        int successfulSends = 0;

        for (ServerPlayer player : targets) {
            SurfApi.getUser(player.getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Alle ", SurfColors.SUCCESS))
                    .append(Component.text("Regeln", SurfColors.GOLD))
                    .append(Component.text(" und", SurfColors.SUCCESS))
                    .append(Component.text(" Informationen", SurfColors.GOLD))
                    .append(Component.text(" findest du ", SurfColors.SUCCESS))
                    .append(Component.text("Hier", SurfColors.RED)
                            .decorate(TextDecoration.BOLD)
                            .hoverEvent(Component.text("Klicke um zu der Website zu kommen", SurfColors.GRAY))
                            .clickEvent(ClickEvent.openUrl("https://castcrafter.de/subserver")))));
            successfulSends++;
        }
        if (source.isPlayer()){
            if (successfulSends == 1 && !(targets.iterator().next() == source.getPlayerOrException())){
                EssentialsUtil.sendSuccess(source, Component.text("Die Regeln wurden an ", SurfColors.SUCCESS)
                        .append(targets.iterator().next().adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                        .append(Component.text(" gesendet!", SurfColors.SUCCESS)));
            }else if (!(targets.iterator().next() == source.getPlayerOrException())){
                EssentialsUtil.sendSuccess(source, Component.text("Die Regeln wurden an ", SurfColors.SUCCESS)
                        .append(Component.text(successfulSends, SurfColors.TERTIARY))
                        .append(Component.text(" Spieler gesendet!", SurfColors.SUCCESS)));
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
