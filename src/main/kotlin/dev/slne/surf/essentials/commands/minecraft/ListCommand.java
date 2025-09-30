package dev.slne.surf.essentials.commands.minecraft;

import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class ListCommand extends EssentialsCommand {
    public ListCommand() {
        super("list", "list [<uuids>]", "Lists all players that are currently online and visible for you");

        withPermission(Permissions.LIST_PERMISSION);

        executesNative((NativeResultingCommandExecutor) (sender, args) -> listPlayerNames(sender.getCallee(), false));
        then(literal("uuids")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> listPlayerNames(sender.getCallee(), true)));
    }

    private int listPlayerNames(CommandSender source, boolean withUUID) {
        val server = source.getServer();
        val onlinePlayers = EssentialsUtil.checkPlayerSuggestionWithoutException(source, new ArrayList<>(server.getOnlinePlayers()));
        val maxPlayers = server.getMaxPlayers();

        EssentialsUtil.sendSuccess(source, Component.text("Es sind gerade ", Colors.INFO)
                .append(Component.text(onlinePlayers.size(), Colors.VARIABLE_VALUE))
                .append(Component.text(" von ", Colors.INFO))
                .append(Component.text(maxPlayers, Colors.VARIABLE_VALUE))
                .append(Component.text(" Spieler%s online: ".formatted(maxPlayers == 1 ? "" : "n"), Colors.INFO)
                        .append(Component.join(JoinConfiguration.commas(true), onlinePlayers.stream().map(player -> {
                                    if (withUUID) {
                                        return Component.text("(", Colors.VARIABLE_VALUE)
                                                .append(EssentialsUtil.getDisplayName(player))
                                                .append(Component.text(")", Colors.VARIABLE_VALUE))
                                                .append(Component.text(" %s".formatted(player.getUniqueId()), Colors.VARIABLE_KEY));
                                    } else {
                                        return EssentialsUtil.getDisplayName(player);
                                    }
                                })
                                .toList())
                        )
                )
        );
        return onlinePlayers.size();
    }
}
