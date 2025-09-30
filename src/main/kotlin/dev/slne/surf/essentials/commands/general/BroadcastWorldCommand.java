package dev.slne.surf.essentials.commands.general;

import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class BroadcastWorldCommand extends EssentialsCommand {
    public BroadcastWorldCommand() {
        super("broadcastworld", "worldbroadcast <world> <message>", "Broadcast a message to a world", "worldalert", "broadcastworld");

        withPermission(Permissions.BROADCAST_WORLD_PERMISSION);

        then(worldArgument("world")
                .then(greedyStringArgument("message")
                        .replaceSuggestions(EssentialsUtil.suggestColors())
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> broadcastWorld(sender.getCallee(), Objects.requireNonNull(args.getUnchecked("world")), args.getUnchecked("message")))));

    }

    private int broadcastWorld(CommandSender source, World level, String message) {
        val broadcast = EssentialsUtil.getPrefix().append(EssentialsUtil.deserialize(message).colorIfAbsent(Colors.TERTIARY));

        for (Audience player : level.getPlayers()) {
            player.sendMessage(broadcast);
            player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL, Sound.Source.MASTER, 1f, 1f));
        }

        EssentialsUtil.sendSuccess(source, Component.text("Es wurde eine Nachricht an alle Spieler in der Welt ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(level))
                .append(Component.text(" geschickt!", Colors.SUCCESS)));
        return 1;
    }
}
