package dev.slne.surf.essentials.main.commands.general.other.poll;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.main.commands.EssentialsCommand;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PollManager extends EssentialsCommand {
    public PollManager(PluginCommand command) {
        super(command);
        command.setPermission("surf.essentials.commands.poll.create");
        command.permissionMessage(EssentialsUtil.NO_PERMISSION());
        command.setDescription("create a poll");
        command.setUsage("/poll create|delete|end|list");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Korrekte Benutzung: ", SurfColors.RED))
                        .append(Component.text("/poll <create | end | delete | list>", SurfColors.TERTIARY))
                        .append(Component.newline()
                                .append(SurfApi.getPrefix()))
                        .append(Component.newline()
                                .append(SurfApi.getPrefix()))
                        .append(Component.text("Aktuelle Umfragen: ", SurfColors.INFO)
                                .append(Component.text(String.join(", ", PollUtil.polls), SurfColors.TERTIARY)))));
                return true;
            }

            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "create" -> PollCreateCommand.create(player, args);
                case "end" -> PollEndCommand.end(player, args);
                case "delete" -> PollDeleteCommand.deletePoll(player, args);
                case "list" -> PollListCommand.list(player, args);
                default ->
                        // Send an error message if the command is not recognized
                        SurfApi.getUser(player).thenAccept(user -> user.sendMessage(SurfApi.getPrefix()
                                .append(Component.text("Falscher Befehl: ", SurfColors.ERROR))
                                .append(Component.text(subCommand, SurfColors.TERTIARY))
                                .append(Component.text(" - verwende /poll <create | end | delete | list>", SurfColors.INFO))));
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Initialize the list of completions
        List<String> completions = new ArrayList<>();

        // Get the current argument being completed
        String currentarg = args[args.length - 1];

        // Check the number of arguments passed to the command
        if (args.length == 1) {
            // Add suggestions for the first argument
            completions.addAll(Arrays.asList("create", "delete", "end", "list"));
        } else {
            // Get the first argument (the subcommand)
            String subCommand = args[0].toLowerCase();

            switch (subCommand) {
                case "create":
                    // Add suggestions for the "create" subcommand
                    if (args.length == 2) {
                        completions.add("NAME");
                    } else if (args.length == 3) {
                        completions.addAll(Arrays.asList("SECONDS", "60", "300", "600", "900"));
                    } else completions.add("QUESTION");
                    break;
                case "end":
                case "delete":
                case "list":
                    // Add suggestions for the "end", "delete", and "list" subcommands
                    completions.add("NAME");
                    break;
                default:
                    // Don't provide suggestions for unknown subcommands
                    break;
            }
        }

        // Filter the completions list and return the suggestions that match the current argument
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(currentarg.toLowerCase()))
                .collect(Collectors.toList());
    }

}
