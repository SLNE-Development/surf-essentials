package dev.slne.surf.essentials.commands.general;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand extends EssentialsCommand {
    private static final Component line = Component.newline().append(EssentialsUtil.getPrefix());

    public InfoCommand() {
        super("info", "info <player>", "Gets information about a player", "information");

        withPermission(Permissions.INFO_PERMISSION);

        then(playerArgument("player")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> info(sender.getCallee(), args.getUnchecked("player"))));
    }

    private static int info(CommandSender source, Player playerUnchecked) throws WrapperCommandSyntaxException {
        val player = EssentialsUtil.checkPlayerSuggestion(source, playerUnchecked);
        val playerAddress = player.getAddress();

        if (playerAddress == null) throw Exceptions.ERROR_NO_INTERNET_ADDRESS;

        val uuid = player.getUniqueId().toString();
        val ip = playerAddress.getHostName() + ":" + playerAddress.getPort();
        val client = EssentialsUtil.getDefaultIfNull(player.getClientBrandName(), "__null__");
        val nameMc = "https://de.namemc.com/profile/" + uuid;
        double health = player.getHealth();
        int food = player.getFoodLevel();


        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(player)
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
