package dev.slne.surf.essentials.commands.minecraft;

import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends EssentialsCommand {
    public ReloadCommand() {
        super("reload", "[<confirm | permissions | commands>]", "Reloads the server (Not recommended)");

        withPermission(Permissions.RELOAD_PERMISSION);

        executesNative((NativeResultingCommandExecutor) (sender, args) -> askForConfirmation(sender.getCallee()));

        then(literal("permissions")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> reloadPermissions(sender.getCallee())));

        then(literal("commands")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> reloadCommands(sender.getCallee())));

        then(literal("confirm")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> reload(sender.getCallee())));
    }

    private int askForConfirmation(CommandSender source) {
        EssentialsUtil.sendSuccess(source, Component.text("Bist du sicher, dass du den Server neu laden möchtest? Dies kann Fehler und Speicherlecks verursachen. Gebe zum bestätigen ", Colors.WARNING)
                .append(Component.text("/reload confirm", Colors.VARIABLE_KEY)
                        .hoverEvent(HoverEvent.showText(Component.text("Oder klicke hier zum Bestätigen", Colors.INFO)))
                        .clickEvent(ClickEvent.suggestCommand("/reload confirm")))
                .append(Component.text(" ein.", Colors.WARNING)));
        return 0;
    }

    private int reloadPermissions(CommandSender source) {
        source.getServer().reloadPermissions();

        EssentialsUtil.sendSuccess(source, "Die Permissions wurden erfolgreich neu geladen.");
        return 1;
    }

    private int reloadCommands(CommandSender source) {
        if (source.getServer().reloadCommandAliases()){
            EssentialsUtil.sendSuccess(source, "Command-Aliasse erfolgreich neu geladen.");
            return 1;
        } else {
            EssentialsUtil.sendError(source, "Beim neu laden der Command-Aliasse ist ein Fehler aufgetreten.");
            return 0;
        }
    }

    private int reload(CommandSender source){
        val server = source.getServer();

        EssentialsUtil.sendInfo(source, "Lade Command-Aliasse neu...");
        server.reloadCommandAliases();

        EssentialsUtil.sendInfo(source, "Lade Permissions neu...");
        server.reloadPermissions();

        EssentialsUtil.sendInfo(source, "Lade Server neu...");
        server.reload();

        EssentialsUtil.sendSuccess(source, "Der Server wurde neu geladen.");
        return 1;
    }
}
