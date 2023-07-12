package dev.slne.surf.essentials.commands.cheat.gui;

import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public abstract class GuiCommand extends EssentialsCommand {

    private final String guiNameWithArticle;

    GuiCommand(String commandName, String GUINameWithArticle, String selfPermission, String otherPermission, String... aliases) {
        super(commandName, "/" + commandName + " [<players>]", "Opens the " + commandName + " GUI for the specified players.", aliases);
        guiNameWithArticle = GUINameWithArticle;

        withRequirement(EssentialsUtil.checkPermissions(selfPermission, otherPermission));
        executesNative((NativeResultingCommandExecutor) (sender, args) -> openGUI(sender.getCallee(), List.of(getPlayerOrException(sender))));
        then(playersArgument("players")
                .withPermission(otherPermission)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> openGUI(sender.getCallee(), EssentialsUtil.checkPlayerSuggestion(sender.getCallee(), args.<Collection<Player>>getUnchecked("players")))));
    }

    private int openGUI(CommandSender sender, Collection<Player> targets) {
        for (Player target : targets) {
            open(target);
        }

        if (targets.size() == 1) {
            EssentialsUtil.sendSuccess(sender, Component.text("%s wurde für ".formatted(guiNameWithArticle), Colors.SUCCESS)
                    .append(EssentialsUtil.getDisplayName(targets.iterator().next()))
                    .append(Component.text(" geöffnet", Colors.SUCCESS)));
        } else {
            EssentialsUtil.sendSuccess(sender, Component.text("%s wurde für ".formatted(guiNameWithArticle), Colors.SUCCESS)
                    .append(Component.text(targets.size(), Colors.TERTIARY))
                    .append(Component.text(" Spieler geöffnet", Colors.SUCCESS)));
        }

        return 1;
    }

    public abstract void open(Player player);
}
