package dev.slne.surf.essentials.commands.tp;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

public class SpawnCommand extends EssentialsCommand {
    public SpawnCommand() {
        super("spawn", "/spawn [<players>]", "Teleports the targets to the overworld-spawn.", "spawntp", "tpspawn");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.TELEPORT_SPAWN_SELF, Permissions.TELEPORT_SPAWN_OTHER));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> tp(sender.getCallee(), Collections.singleton(getPlayerOrException(sender))));
        then(playersArgument("players")
                .withPermission(Permissions.TELEPORT_SPAWN_OTHER)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> tp(sender.getCallee(), args.getUnchecked("players"))));
    }

    public int tp(CommandSender sourceStack, Collection<Player> playersUnchecked) throws WrapperCommandSyntaxException {
        val players = EssentialsUtil.checkPlayerSuggestion(sourceStack, playersUnchecked);
        val overworld = sourceStack.getServer().getWorlds().get(0);
        int successfullyTeleported = 0;

        for (Player player : players) {
            player.teleportAsync(overworld.getSpawnLocation()).thenAccept(__ -> EssentialsUtil.sendSuccess(player, "Du wurdest zum Spawn teleportiert"));
            successfullyTeleported++;
        }

        boolean isSelf = sourceStack instanceof Player player && players.iterator().next().equals(player);
        if (successfullyTeleported == 1 && !isSelf) {
            EssentialsUtil.sendSuccess(sourceStack, EssentialsUtil.getDisplayName(players.iterator().next())
                    .append(Component.text(" wurde zum Spawn teleportiert", Colors.SUCCESS)));
        } else if (successfullyTeleported != 1) {
            EssentialsUtil.sendSuccess(sourceStack, Component.text("Es wurden ", Colors.SUCCESS)
                    .append(Component.text(successfullyTeleported, Colors.TERTIARY))
                    .append(Component.text(" Spieler zum Spawn teleportiert", Colors.SUCCESS)));
        }

        return 1;
    }
}
