package dev.slne.surf.essentials.commands.minecraft;

import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

public class DefaultGamemodeCommand extends EssentialsCommand {
    public DefaultGamemodeCommand() {
        super("defaultgamemode", "defaultgamemode [<defaultgamemode>]", "Change default gamemode");

        withPermission(Permissions.DEFAULT_GAMEMODE_PERMISSION);

        executesNative((NativeResultingCommandExecutor) (sender, args) -> getGameMode(sender.getCallee()));
        then(gameModeArgument("gamemode")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> setGameMode(sender.getCallee(), args.getUnchecked("gamemode"))));
    }


    private int setGameMode(CommandSender source, GameMode newGameMode) {
        val server = source.getServer();
        int playersUpdated = 0;

        server.setDefaultGameMode(newGameMode);

        if (EssentialsUtil.properties_forceGamemode()) {
            for (HumanEntity player : server.getOnlinePlayers()) {
                player.setGameMode(newGameMode);
                playersUpdated++;
            }
        }

        EssentialsUtil.sendSuccess(source, Component.text("Der standard Spielmodus wurde auf ", Colors.SUCCESS)
                .append(Component.translatable(newGameMode.translationKey(), Colors.VARIABLE_VALUE))
                .append(Component.text(" gesetzt!", Colors.SUCCESS)));
        return playersUpdated;
    }

    private int getGameMode(CommandSender source) {
        val gameType = source.getServer().getDefaultGameMode();

        EssentialsUtil.sendSuccess(source, Component.text("Der standard Spielmodus ist ", Colors.INFO)
                .append(Component.translatable(gameType.translationKey(), Colors.VARIABLE_VALUE))
                .append(Component.text("!", Colors.INFO)));

        return 1;
    }
}
