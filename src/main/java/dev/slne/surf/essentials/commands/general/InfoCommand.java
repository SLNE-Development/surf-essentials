package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class InfoCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"information", "info"};
    }

    @Override
    public String usage() {
        return "/info <player>";
    }

    @Override
    public String description() {
        return "Gets information about a player";
    }

    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(EssentialsUtil.checkPermissions(Permissions.INFO_PERMISSION));

        literal.then(Commands.argument("player", EntityArgument.player())
                .executes(context -> info(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private static int info(CommandSourceStack source, ServerPlayer playerUnchecked) throws CommandSyntaxException {
        ServerPlayer player = EssentialsUtil.checkPlayerSuggestion(source, playerUnchecked);
        Component line = Component.newline().append(EssentialsUtil.getPrefix());

        String uuid = player.getStringUUID();
        String ip = player.getIpAddress();
        String client = EssentialsUtil.getDefaultIfNull(player.connection.getClientBrandName(), "vanilla");
        String nameMc = "https://de.namemc.com/profile/" + uuid;
        float health = player.getHealth();
        int food = player.getFoodData().getFoodLevel();


        EssentialsUtil.sendSuccess(source, (EssentialsUtil.getDisplayName(player)
                .decorate(TextDecoration.UNDERLINED))
                .append(Component.text(":", Colors.INFO))
                .append(line)
                .append(line)
                .append(Component.text("UUID: ", Colors.INFO))
                .append(Component.text(uuid, Colors.TERTIARY)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Kopieren", Colors.INFO)))
                        .clickEvent(ClickEvent.copyToClipboard(uuid)))
                .append(line)
                .append(Component.text("IP: ", Colors.INFO)
                        .append(Component.text(ip, Colors.DARK_GREEN)
                                .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Kopieren", Colors.INFO)))
                                .clickEvent(ClickEvent.copyToClipboard(ip))))
                .append(line)
                .append(Component.text("Client brand: ", Colors.INFO)
                        .append(Component.text(client, Colors.TERTIARY)))
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
