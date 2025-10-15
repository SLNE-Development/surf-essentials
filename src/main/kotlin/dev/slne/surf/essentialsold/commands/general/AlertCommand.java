package dev.slne.surf.essentialsold.commands.general;

import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class AlertCommand extends EssentialsCommand {
    public AlertCommand() {
        super("alert", "alert <message>", "Sends an alert message to all online players");

        withPermission(Permissions.ALERT_PERMISSION);

        then(greedyStringArgument("message")
                .replaceSuggestions(EssentialsUtil.suggestColors())
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> alert(sender.getCallee(), args.getUnchecked("message"))));
    }

    private static int alert(CommandSender source, String message){
        Bukkit.broadcast(EssentialsUtil.getPrefix()
                .append(EssentialsUtil.deserialize(message).colorIfAbsent(Colors.VARIABLE_VALUE)));

        for (Audience audience : source.getServer().getOnlinePlayers()) {
            audience.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL, Sound.Source.MASTER, 1f, 1f));
        }
        return 1;
    }
}
