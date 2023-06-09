package dev.slne.surf.essentials.commands.general.other.poll;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.bukkit.Bukkit;

public class PollCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"poll"};
    }

    @Override
    public String usage() {
        return "/poll <create | end | remove | list>";
    }

    @Override
    public String description() {
        return "Manage polls";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.POLL_PERMISSION));

        literal.then(Commands.literal("create")
                .then(Commands.argument("name", StringArgumentType.word())
                        .then(Commands.argument("durationInSeconds", IntegerArgumentType.integer(1, 21600))
                                .then(Commands.argument("question", StringArgumentType.greedyString())
                                        .executes(context -> createPoll(context.getSource(), StringArgumentType.getString(context, "name"),
                                                IntegerArgumentType.getInteger(context, "durationInSeconds"), StringArgumentType.getString(context, "question")))))));

        literal.then(Commands.literal("end")
                .then(Commands.argument("name", StringArgumentType.word())
                        .suggests(activePollSuggestions())
                        .executes(context -> endPoll(context.getSource(), StringArgumentType.getString(context, "name")))));

        literal.then(Commands.literal("remove")
                .then(Commands.argument("name", StringArgumentType.word())
                        .suggests(activePollSuggestions())
                        .executes(context -> removePoll(context.getSource(), StringArgumentType.getString(context, "name")))));

        literal.then(Commands.literal("list")
                .executes(context -> listPolls(context.getSource())));
    }

    private int createPoll(CommandSourceStack source, String name, int durationInSeconds, String question) {
        if (Poll.checkPollExists(name)) {
            if (source.isPlayer()) {
                final var poll = Poll.getPoll(name).join();
                EssentialsUtil.sendError(source, Component.text("Es läuft bereits eine Umfrage mit dem Namen ", Colors.ERROR)
                        .append(Component.text(poll.getName(), Colors.TERTIARY)
                                .hoverEvent(HoverEvent.showText(Component.text("Zeit: ", Colors.INFO)
                                        .append(Component.text(EssentialsUtil.ticksToString(poll.getDuration() * 20), Colors.GREEN))
                                        .appendNewline()
                                        .append(Component.text("Frage: ", Colors.INFO))
                                        .append(Component.newline())
                                        .append(Component.text(poll.getQuestion(), Colors.TERTIARY)))))
                        .append(Component.text("!", Colors.ERROR)));
            } else {
                source.sendFailure(net.minecraft.network.chat.Component.literal("The poll ")
                        .withStyle(ChatFormatting.RED)
                        .append(name)
                        .withStyle(ChatFormatting.GOLD)
                        .append(" already exists!")
                        .withStyle(ChatFormatting.RED));
            }
            return 0;
        }

        Poll.createPoll(name, question, durationInSeconds).thenAcceptAsync(poll -> {
            poll.startMessage(Bukkit.getOnlinePlayers());
            poll.startTimer();
        });
        return 1;
    }

    private int endPoll(CommandSourceStack source, String name) {
        if (!testPoll(name, source)) {
            return 0;
        }

        Poll.getPoll(name).thenAcceptAsync(Poll::stop);

        EssentialsUtil.sendSuccess(source, "Die Umfrage wird beendet.");

        return 1;
    }

    private int removePoll(CommandSourceStack source, String name) {
        if (!testPoll(name, source)) {
            return 0;
        }

        Poll.getPoll(name).thenAcceptAsync(poll -> {
            poll.setStopSilent(true);
            poll.stop();
        });

        EssentialsUtil.sendSuccess(source, "Die Umfrage wird gelöscht.");
        return 1;
    }

    private static int listPolls(CommandSourceStack source) {
        final var polls = Poll.getPolls();
        if (polls.size() == 0) {
            if (source.isPlayer()) {
                EssentialsUtil.sendError(source, "Aktuell laufen keine Umfragen!");
            } else {
                source.sendFailure(net.minecraft.network.chat.Component.literal("No polls are active right now."));
            }
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
                                                .append(Component.text(poll.getQuestion(), Colors.TERTIARY))))).toArray(Component[]::new)))
        );
        return polls.size();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean testPoll(String name, CommandSourceStack source) {
        if (!Poll.checkPollExists(name)) {
            if (source.isPlayer()) {
                EssentialsUtil.sendError(source, Component.text("Die Umfrage ", Colors.ERROR)
                        .append(Component.text(name, Colors.TERTIARY))
                        .append(Component.text(" existiert nicht!")));
            } else {
                source.sendFailure(net.minecraft.network.chat.Component.literal("The poll ")
                        .withStyle(ChatFormatting.RED)
                        .append(name)
                        .withStyle(ChatFormatting.GOLD)
                        .append(" does not exist!")
                        .withStyle(ChatFormatting.RED));
            }
            return false;
        }
        return true;
    }

    public static SuggestionProvider<CommandSourceStack> activePollSuggestions() {
        return (context, builder) -> {
            for (Poll poll : Poll.getPolls()) {
                builder.suggest(poll.getName(), net.minecraft.network.chat.Component.literal(poll.getQuestion()).withStyle(ChatFormatting.GRAY));
            }
            return builder.buildFuture();
        };
    }
}
