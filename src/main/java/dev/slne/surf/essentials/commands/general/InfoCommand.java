package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
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
        SurfEssentials.registerPluginBrigadierCommand("information", InfoCommand::literal).setUsage("/information <player>")
                .setUsage("Get some information about the player");
        SurfEssentials.registerPluginBrigadierCommand("info", InfoCommand::literal).setUsage("/information <player>")
                .setUsage("Get some information about the player");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.INFO_PERMISSION));

        literal.then(Commands.argument("player", EntityArgument.player())
                .executes(context -> info(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private static int info(CommandSourceStack source, ServerPlayer playerUnchecked) throws CommandSyntaxException {
        ServerPlayer player = EssentialsUtil.checkSinglePlayerSuggestion(source, playerUnchecked);
        Component line = Component.newline().append(SurfApi.getPrefix());
        Component name = player.adventure$displayName.colorIfAbsent(SurfColors.TERTIARY);
        UUID uuid = player.getUUID();
        String nameMc = "https://de.namemc.com/profile/" + uuid;
        float health = player.getHealth();
        float food = player.getFoodData().getFoodLevel();

        EssentialsUtil.sendSuccess(source, Component.empty().append(name
                .append(Component.text(":", SurfColors.INFO)).decorate(TextDecoration.UNDERLINED))
                .append(line)
                .append(line)
                .append(Component.text("UUID: ", SurfColors.INFO))
                .append(Component.text(uuid.toString(), SurfColors.TERTIARY)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Kopieren", SurfColors.INFO)))
                        .clickEvent(ClickEvent.copyToClipboard(uuid.toString())))
                .append(line)
                .append(Component.text("Name Mc: ", SurfColors.INFO)
                        .append(Component.text("Hier", SurfColors.SECONDARY)
                                .hoverEvent(HoverEvent.showText(Component.text("Klicke zum öffnen", SurfColors.INFO)))
                                .clickEvent(ClickEvent.openUrl(nameMc))))
                .append(line)
                .append(Component.text("Leben: ", SurfColors.INFO)
                        .append(Component.text(health, SurfColors.GREEN)))
                .append(line)
                .append(Component.text("Essen: ", SurfColors.INFO)
                        .append(Component.text(food, SurfColors.GREEN))));
        return 1;
    }
}
