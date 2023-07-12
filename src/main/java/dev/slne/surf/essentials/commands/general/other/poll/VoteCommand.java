package dev.slne.surf.essentials.commands.general.other.poll;

import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.permission.Permissions;
import org.bukkit.entity.Player;

import java.util.Objects;

public class VoteCommand extends EssentialsCommand {
    public VoteCommand() {
        super("vote", "vote <poll> <yes | no>", "Vote on a poll");

        withPermission(Permissions.VOTE_PERMISSION);

        then(pollArgument("poll")
                .then(literal("yes")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> vote(getPlayerOrException(sender), Objects.requireNonNull(args.getUnchecked("poll")), true)))
                .then(literal("no")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> vote(getPlayerOrException(sender), Objects.requireNonNull(args.getUnchecked("poll")), false))));
    }

    private int vote(Player player, Poll poll, boolean yes) {

        if (!poll.addVote(player, yes)) {
            EssentialsUtil.sendError(player, "Du kannst nur einmal voten");
            return 0;
        }
        EssentialsUtil.sendSuccess(player, "Vielen Dank für deine Stimme!");

        return 1;
    }
}
