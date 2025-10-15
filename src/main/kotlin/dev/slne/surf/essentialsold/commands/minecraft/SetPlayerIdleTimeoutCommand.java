package dev.slne.surf.essentialsold.commands.minecraft;

import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SetPlayerIdleTimeoutCommand extends EssentialsCommand {
    public SetPlayerIdleTimeoutCommand() {
        super("setplayeridletimeout", "setplayeridletimeout [<minutes>]", "Sets the time until a player is kicked for being idle");

        withPermission(Permissions.SET_PLAYER_IDLE_TIMEOUT_PERMISSION);

        executesNative((NativeResultingCommandExecutor) (sender, args) -> queryIdleTimeout(sender.getCallee()));
        then(integerArgument("minutes", 0)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> setIdleTimeout(sender.getCallee(), args.getUnchecked("minutes"))));
    }

    private int queryIdleTimeout(@NotNull CommandSender source) {
        val timeout = source.getServer().getIdleTimeout();
        EssentialsUtil.sendSuccess(source, Component.text("Das Untätigkeitslimit ist gerade auf ", Colors.INFO)
                .append(Component.text(timeout, Colors.VARIABLE_VALUE))
                .append(Component.text(timeout == 1 ? " Minute" : " Minuten", Colors.INFO))
                .append(Component.text(" gesetzt.", Colors.INFO)));
        return timeout;
    }

    @Contract("_, _ -> param2")
    private int setIdleTimeout(@NotNull CommandSender source, Integer minutes) {
        source.getServer().setIdleTimeout(minutes);

        EssentialsUtil.sendSuccess(source, Component.text("Das Untätigkeitslimit wurde auf ", Colors.SUCCESS)
                .append(Component.text(minutes, Colors.VARIABLE_VALUE))
                .append(Component.text((minutes == 1) ? " Minute" : " Minuten", Colors.SUCCESS))
                .append(Component.text(" gesetzt.", Colors.SUCCESS)));

        return minutes;
    }
}
