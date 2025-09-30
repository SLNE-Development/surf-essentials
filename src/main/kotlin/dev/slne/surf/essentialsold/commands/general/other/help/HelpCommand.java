package dev.slne.surf.essentialsold.commands.general.other.help;

import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand { // TODO bukkit overrides this

    public HelpCommand() {
        /**
        super("help", "help <Command | Plugin> [<page>]", "Shows you the usage of commands");

        withPermission(Permissions.HELP_PERMISSION);

        Bukkit.getServer().getCommandMap().getKnownCommands().forEach((s, command) -> {
            then(literal(s)
                    .executesNative((NativeResultingCommandExecutor) (sender, args) -> showCommandHelp(sender.getCallee(), command)));
        });

        for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
            then(literal(plugin.getName())
                    .executesNative((NativeResultingCommandExecutor) (sender, args) -> showPluginHelp(sender.getCallee(), plugin, 1))
                    .then(integerArgument("page", 1, getAllCommandsFromPlugin(null, plugin).length)
                            .executesNative((NativeResultingCommandExecutor) (sender, args) -> showPluginHelp(sender.getCallee(), plugin, args.getUnchecked("page")))));
        }
         */
    }

    private int showCommandHelp(CommandSender source, Command command) {
       val builder = Component.text();

        builder.append(header()
                .append(newLine())
                .append(newLine()));

        if (!command.getDescription().equals("")) {
            builder.append(description(command.getDescription()))
                    .append(newLine())
                    .append(newLine());
        }
        builder.append(correctUsage(command.getUsage()))
                .append(newLine())
                .append(newLine());

        if (command.getAliases().size() != 0) {
            builder.append(aliases(command.getAliases()))
                    .append(newLine())
                    .append(newLine());
        }

        if (command.getPermission() != null) {
            builder.append(permission(command.getPermission())
                    .append(newLine()));
        }

        EssentialsUtil.sendSuccess(source, builder.build());

        return 1;
    }

    private int showPluginHelp(CommandSender source, Plugin plugin, Integer page) {
        val allCommands = getAllCommandsFromPlugin(source, plugin);
        val currentPageCommands = allCommands[page - 1].translateEscapes().split("\n");
        val builder = Component.text();

        builder.append(header()
                .append(newLine()));

        for (String command : currentPageCommands) {
            builder.append(EssentialsUtil.deserialize(command).colorIfAbsent(Colors.TERTIARY))
                    .append(newLine());
        }

        builder.append(newLine());

        if (page != 1) {
            builder.append(Component.text("⬅ Zurück", Colors.GREEN)
                    .hoverEvent(HoverEvent.showText(Component.text("Gehe eine Seite zurück", Colors.INFO)))
                    .clickEvent(ClickEvent.runCommand("/help %s %d".formatted(plugin.getName(), page - 1)))
                    .append(Component.text("──", Colors.GRAY)));
        } else {
            builder.append(Component.text("────────", Colors.GRAY));
        }

        builder.append(Component.text("───────", Colors.GRAY));

        if (allCommands.length >= page + 1) {
            builder.append(Component.text("➡ Weiter", Colors.GREEN)
                    .hoverEvent(HoverEvent.showText(Component.text("Gehe eine Seite weiter", Colors.INFO)))
                    .clickEvent(ClickEvent.runCommand("/help %s %d".formatted(plugin.getName(), page + 1))));
        } else {
            builder.append(Component.text("────────", Colors.GRAY));
        }

        EssentialsUtil.sendSuccess(source, builder.build());

        return 1;
    }

    private String[] getAllCommandsFromPlugin(@Nullable CommandSender source, @NotNull Plugin plugin) {
        source = (source == null) ? Bukkit.getConsoleSender() : source;

        val helpMap = Bukkit.getServer().getHelpMap();
        val topic = helpMap.getHelpTopic(plugin.getName());

        if (topic == null) {
            return new String[]{"§cNo help for " + plugin.getName()};
        }

        val fullText = topic.getFullText(source).translateEscapes();
        val splitText = fullText.split("\n");

        val stringArrayList = new ArrayList<String>();
        var stringBuilder = new StringBuilder();
        for (int i = 0; i < splitText.length; i++) {
            stringBuilder.append(splitText[i]).append("\n");
            if ((i + 1) % 7 == 0) {
                stringArrayList.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
            }
        }
        if (stringBuilder.length() > 0) {
            stringArrayList.add(stringBuilder.toString());
        }

        return stringArrayList.toArray(new String[0]);
    }


    private Component prefix() {
        return Component.text(">> ", Colors.DARK_GRAY)
                .append(Component.text("Help", Colors.GREEN))
                .append(Component.text(" | ", Colors.DARK_GRAY));
    }

    private Component header() {
        return Component.text("╾────────── ", Colors.GRAY)
                .append(Component.text("Help", Colors.GREEN))
                .append(Component.text(" ──────────╼", Colors.GRAY));
    }

    private Component correctUsage(String usage) {
        return Component.text("Korrekte Benutzung: ", TextColor.fromHexString("#e67e22"))
                .append(Component.text(usage, Colors.VARIABLE_VALUE))
                .colorIfAbsent(Colors.VARIABLE_VALUE);
    }

    private Component description(String description) {
        return Component.text("Beschreibung: ", TextColor.fromHexString("#e67e22"))
                .append(Component.text(description, Colors.VARIABLE_VALUE));
    }

    private Component aliases(List<String> aliases) {
        return Component.text("Aliases: ", TextColor.fromHexString("#e67e22"))
                .append(Component.join(JoinConfiguration.commas(true), aliases.stream()
                        .map(alias -> Component.text(alias, Colors.VARIABLE_VALUE))
                        .toList()));
    }

    private Component permission(String permission) {
        return Component.text("Permission: ", TextColor.fromHexString("#e67e22"))
                .append(Component.text(permission, Colors.VARIABLE_VALUE));
    }

    private Component newLine() {
        return Component.newline().append(prefix());
    }
}
