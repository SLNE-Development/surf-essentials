package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.bukkit.Bukkit;

public class ReloadCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"reload", "rl"};
    }

    @Override
    public String usage() {
        return "/reload [<confirm | permissions | commands>]";
    }

    @Override
    public String description() {
        return "Reloads the server (Not recommended)";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(4, Permissions.RELOAD_PERMISSION));

        literal.executes(context -> askForConfirmation(context.getSource()));

        literal.then(Commands.literal("permissions")
                .executes(context -> reloadPermissions(context.getSource())));

        literal.then(Commands.literal("commands")
                .executes(context -> reloadCommands(context.getSource())));

        literal.then(Commands.literal("confirm")
                .executes(context -> reload(context.getSource())));
    }

    private int askForConfirmation(CommandSourceStack source) {
        EssentialsUtil.sendSourceMessage(source, Component.text("Bist du sicher, dass du den Server neu laden möchtest? Dies kann Fehler und Speicherlecks verursachen. Gebe zum bestätigen ", Colors.WARNING)
                .append(Component.text("/reload confirm", Colors.VARIABLE_KEY)
                        .hoverEvent(HoverEvent.showText(Component.text("Oder klicke hier zum Bestätigen", Colors.INFO)))
                        .clickEvent(ClickEvent.suggestCommand("/reload confirm")))
                .append(Component.text(" ein.", Colors.WARNING)));
        return 0;
    }

    private int reloadPermissions(CommandSourceStack source) {
        Bukkit.getServer().reloadPermissions();

        EssentialsUtil.sendSourceSuccess(source, "Die Permissions wurden erfolgreich neu geladen.");
        return 1;
    }

    private int reloadCommands(CommandSourceStack source) {
        if (Bukkit.getServer().reloadCommandAliases()){
            EssentialsUtil.sendSourceSuccess(source, "Command-Aliasse erfolgreich neu geladen.");
            return 1;
        } else {
            EssentialsUtil.sendSourceError(source, "Beim neu laden der Command-Aliasse ist ein Fehler aufgetreten.");
            return 0;
        }
    }

    private int reload(CommandSourceStack source){
        EssentialsUtil.sendSourceInfo(source, "Lade Command-Aliasse neu...");
        Bukkit.getServer().reloadCommandAliases();
        EssentialsUtil.sendSourceInfo(source, "Lade Permissions neu...");
        Bukkit.getServer().reloadData();
        EssentialsUtil.sendSourceInfo(source, "Lade Server neu...");
        EssentialsUtil.getMinecraftServer().server.reload();

        EssentialsUtil.sendSourceSuccess(source, "Der Server wurde neu geladen.");
        return 1;
    }
}
