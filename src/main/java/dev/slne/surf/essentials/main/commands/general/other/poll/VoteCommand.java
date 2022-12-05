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

import java.util.Arrays;
import java.util.List;

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
            if (args.length < 2){
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Vote jetzt für eine umfrage!", SurfColors.INFO))
                        .append(Component.newline()
                                .append(SurfApi.getPrefix()))
                        .append(Component.newline()
                                .append(SurfApi.getPrefix()))
                        .append(Component.text("Aktuelle Umfragen: ", SurfColors.INFO))
                        .append(Component.text(Arrays.toString(PollUtil.polls.toArray()), SurfColors.TERTIARY))));
                return true;
            }

            if(!PollUtil.polls.contains(args[0])){
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Aktuell läuft keine Umfrage mit dem Namen ", SurfColors.ERROR))
                        .append(Component.text(args[0], SurfColors.TERTIARY))
                        .append(Component.text("!", SurfColors.ERROR))));
                return true;
            }
            if (!isYesNo(args[1])){
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Du musst ", SurfColors.ERROR))
                        .append(Component.text("yes/no", SurfColors.TERTIARY))
                        .append(Component.text(" angeben!", SurfColors.ERROR))));
                return true;
            }
            if (PollUtil.hasVoted(player, args[0])){
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Du kannst nur einmal voten!", SurfColors.ERROR))));
                return true;
            }
            if (args[1].equalsIgnoreCase("yes")){
                PollUtil.addYesCount(args[0]);
                PollUtil.addVoted(player, args[0]);
                PollUtil.successVote(player);
            }
            if (args[1].equalsIgnoreCase("no")){
                PollUtil.addNoCount(1);
                PollUtil.addVoted(player, args[0]);
                PollUtil.successVote(player);
            }

        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }

    private Boolean isYesNo(String check){
        if (check.equalsIgnoreCase("yes")) return true;
        return check.equalsIgnoreCase("no");
    }
}
