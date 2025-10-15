package dev.slne.surf.essentialsold.commands.general.other.poll;

import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Objects;

public class PollCommand extends EssentialsCommand {
    public PollCommand() {
        super("poll", "poll <create | end | remove | list>", "Manage polls");

        withPermission(Permissions.POLL_PERMISSION);

        then(literal("create")
                .then(wordArgument("name")
                        .then(integerArgument("durationInSeconds", 1, 21600)
                                .then(greedyStringArgument("question")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> createPoll(sender.getCallee(), args.getUnchecked("name"),
                                                args.getUnchecked("durationInSeconds"), args.getUnchecked("question")))))));

        then(literal("end")
                .then(pollArgument("poll")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> endPoll(sender.getCallee(), Objects.requireNonNull(args.getUnchecked("poll"))))));

        then(literal("remove")
                .then(pollArgument("poll")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> removePoll(sender.getCallee(), Objects.requireNonNull(args.getUnchecked("poll"))))));

        then(literal("list")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> listPolls(sender.getCallee())));
    }

    private int createPoll(CommandSender source, String name, Integer durationInSeconds, String question) {
        if (Poll.checkPollExists(name)) {
            val poll = Poll.getPoll(name);
            EssentialsUtil.sendError(source, Component.text("Es läuft bereits eine Umfrage mit dem Namen ", Colors.ERROR)
                    .append(Component.text(poll.getName(), Colors.TERTIARY)
                            .hoverEvent(HoverEvent.showText(Component.text("Zeit: ", Colors.INFO)
                                    .append(Component.text(EssentialsUtil.ticksToString(poll.getDuration() * 20), Colors.GREEN))
                                    .appendNewline()
                                    .append(Component.text("Frage: ", Colors.INFO))
                                    .append(Component.newline())
                                    .append(Component.text(poll.getQuestion(), Colors.TERTIARY)))))
                    .append(Component.text("!", Colors.ERROR)));
            return 0;
        }

        val poll = Poll.builder()
                .name(name)
                .question(question)
                .durationInSeconds(durationInSeconds)
                .build();

        poll.startMessage(Bukkit.getOnlinePlayers());
        poll.startTimer();

        return 1;
    }

    private int endPoll(CommandSender source, Poll poll) {
        poll.stop();
        EssentialsUtil.sendSuccess(source, "Die Umfrage wird beendet.");
        return 1;
    }

    private int removePoll(CommandSender source, Poll poll) {
        poll.setStopSilent(true);
        poll.stop();

        EssentialsUtil.sendSuccess(source, "Die Umfrage wird gelöscht.");
        return 1;
    }

    private static int listPolls(CommandSender source) {
        final Collection<Poll> polls = Poll.getPolls();
        if (polls.size() == 0) {
            EssentialsUtil.sendError(source, "Aktuell laufen keine Umfragen!");
            return polls.size();
        }


        EssentialsUtil.sendSuccess(
                source,
                Component.text("Aktuelle Umfragen: ", Colors.INFO)
                        .append(Component.join(JoinConfiguration.commas(true), polls.stream().map(poll ->
                                Component.text(poll.getName(), Colors.TERTIARY)
                                        .hoverEvent(HoverEvent.showText(Component.text("Dauer: ", Colors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(poll.getDuration() * 20), Colors.GREEN))
                                                .append(Component.newline())
                                                .append(Component.text("Frage: ", Colors.INFO))
                                                .append(Component.text(poll.getQuestion(), Colors.TERTIARY))))).toList()))
        );
        return polls.size();
    }
}
