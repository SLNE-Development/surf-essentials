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

import static dev.slne.surf.essentials.main.commands.general.other.poll.PollUtil.*;

public class VoteCommand extends EssentialsCommand {
    public VoteCommand(PluginCommand command) {
        super(command);
        command.setUsage("/vote <poll> <yes|no>");
        command.setDescription("Vote for polls!");
        command.permissionMessage(EssentialsUtil.NO_PERMISSION());
        command.setPermission("surf.essentials.commands.poll.vote");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            // Check if there are enough arguments
            if (args.length < 2){
                // Not enough arguments, send a message with the current polls
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Vote jetzt für eine Umfrage!", SurfColors.GREEN))
                        .append(Component.newline()
                                .append(SurfApi.getPrefix()))
                        .append(Component.newline()
                                .append(SurfApi.getPrefix()))
                        .append(Component.text("Aktuelle Umfragen: ", SurfColors.INFO))
                        .append(Component.text(Arrays.toString(polls.toArray()), SurfColors.TERTIARY))));
                return true;
            }

            // Check if the specified poll exists
            if(!polls.contains(args[0])){
                // Poll does not exist, send an error message
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Aktuell läuft keine Umfrage mit dem Namen ", SurfColors.ERROR))
                        .append(Component.text(args[0], SurfColors.TERTIARY))
                        .append(Component.text("!", SurfColors.ERROR))));
                return true;
            }

            // Check if the second argument is "yes" or "no"
            if (!isYesNo(args[1])){
                // Second argument is not "yes" or "no", send an error message
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Du musst ", SurfColors.ERROR))
                        .append(Component.text("yes/no", SurfColors.TERTIARY))
                        .append(Component.text(" angeben!", SurfColors.ERROR))));
                return true;
            }

            // Check if the player has already voted in the specified poll
            if (hasVoted(player, args[0])){
                // Player has already voted, send an error message
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Du kannst nur einmal voten!", SurfColors.ERROR))));
                return true;
            }

            // Add the player's vote to the poll
            if (args[1].equalsIgnoreCase("yes")) addYesCount(args[0]);
            else if (args[1].equalsIgnoreCase("no")) addNoCount(args[0]);
            // Mark the player as having voted in the poll
            addVoted(player, args[0]);
            // Send a success message
            successVote(player);

        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>(); // the final completion list
        String currentarg = args[args.length - 1]; // the current argument

        switch (args.length) {
            case 1 -> completions.addAll(polls);
            case 2 -> completions.addAll(Arrays.asList("yes", "no"));
        }

        completions.removeIf(s -> !s.startsWith(currentarg.toLowerCase()));
        return completions;
    }

    private Boolean isYesNo(String check){
        if (check.equalsIgnoreCase("yes")) return true;
        return check.equalsIgnoreCase("no");
    }
}
