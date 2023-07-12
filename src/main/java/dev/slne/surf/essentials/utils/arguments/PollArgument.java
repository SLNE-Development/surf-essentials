package dev.slne.surf.essentials.utils.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.essentials.commands.general.other.poll.Poll;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;

import java.util.concurrent.CompletableFuture;

/**
 * Custom argument for {@link Poll}s
 */
public class PollArgument extends CustomArgument<Poll, String> {
    /**
     * A {@link Poll} argument
     *
     * @param nodeName the name of the node for this argument
     */
    public PollArgument(String nodeName) {
        super(new StringArgument(nodeName), info -> {
            if (!Poll.checkPollExists(info.input())) // check if poll is invalid
                throw CustomArgumentException.fromAdventureComponent(Exceptions.POLL_NOT_EXISTS.message(info.input())); // throw an error if the input is not a Poll
            return Poll.getPoll(info.input());
        });

        replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.supplyAsync(() -> Poll.getPolls().stream().map(Poll::getName).toList()))); // suggest all Polls
    }
}
