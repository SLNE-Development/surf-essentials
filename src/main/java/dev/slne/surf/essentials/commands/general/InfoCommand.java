package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class InfoCommand{

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("information", InfoCommand::literal);
        SurfEssentials.registerPluginBrigadierCommand("info", InfoCommand::literal);
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.INFO_PERMISSION));

        literal.then(Commands.argument("player", EntityArgument.player())
                .executes(context -> info(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private static int info(CommandSourceStack source, ServerPlayer playerUnchecked) throws CommandSyntaxException {
        ServerPlayer player = EssentialsUtil.checkSinglePlayerSuggestion(source, playerUnchecked);
        Component line = Component.newline().append(EssentialsUtil.getPrefix());
        Component name = player.adventure$displayName.colorIfAbsent(Colors.TERTIARY);
        UUID uuid = player.getUUID();
        String nameMc = "https://de.namemc.com/profile/" + uuid;
        float health = player.getHealth();
        float food = player.getFoodData().getFoodLevel();

        EssentialsUtil.sendSuccess(source, Component.empty().append(name
                .append(Component.text(":", Colors.INFO)).decorate(TextDecoration.UNDERLINED))
                .append(line)
                .append(line)
                .append(Component.text("UUID: ", Colors.INFO))
                .append(Component.text(uuid.toString(), Colors.TERTIARY)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Kopieren", Colors.INFO)))
                        .clickEvent(ClickEvent.copyToClipboard(uuid.toString())))
                .append(line)
                .append(Component.text("Name Mc: ", Colors.INFO)
                        .append(Component.text("Hier", Colors.SECONDARY)
                                .hoverEvent(HoverEvent.showText(Component.text("Klicke zum öffnen", Colors.INFO)))
                                .clickEvent(ClickEvent.openUrl(nameMc))))
                .append(line)
                .append(Component.text("Leben: ", Colors.INFO)
                        .append(Component.text(health, Colors.GREEN)))
                .append(line)
                .append(Component.text("Essen: ", Colors.INFO)
                        .append(Component.text(food, Colors.GREEN))));
        return 1;
    }
}
