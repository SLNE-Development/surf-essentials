package dev.slne.surf.essentials.commands.general.other.poll;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.*;

@PermissionTag(name = Permissions.POLL_PERMISSION, desc = "This is the permission for the 'poll' command")
public class PollCommand {

    private static final List<String> pollNames = new ArrayList<>();
    private static final List<String> pollsToRemove = new ArrayList<>();
    private static final HashMap<String, String> pollQuestions = new HashMap<>();
    private static final HashMap<String, Integer> pollDurationInSeconds = new HashMap<>();

    private static final HashMap<ServerPlayer, String> votedPlayer = new HashMap<>();
    private static final HashMap<String, Integer> yesCounts = new HashMap<>();
    private static final HashMap<String, Integer> noCounts = new HashMap<>();

    private static final Component line = Component.newline().append(SurfApi.getPrefix());

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("poll", PollCommand::literal).setUsage("/poll <create | list | end | remove>")
                .setDescription("Manage polls");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.TROLL_PERMISSION));

        literal.then(Commands.literal("create")
                .then(Commands.argument("name", StringArgumentType.word())
                        .then(Commands.argument("durationInSeconds", IntegerArgumentType.integer(1, 21600))
                                .then(Commands.argument("question", StringArgumentType.greedyString())
                                        .executes(context -> createPoll(context.getSource(), StringArgumentType.getString(context, "name"),
                                                IntegerArgumentType.getInteger(context, "durationInSeconds"), StringArgumentType.getString(context, "question")))))));

        literal.then(Commands.literal("end")
                .then(Commands.argument("name", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            for (String pollName : pollNames) {
                                builder.suggest(pollName, net.minecraft.network.chat.Component.literal(getQuestion(pollName)));
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> endPoll(context.getSource(), StringArgumentType.getString(context, "name")))));

        literal.then(Commands.literal("remove")
                .then(Commands.argument("name", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            for (String pollName : pollNames) {
                                builder.suggest(pollName, net.minecraft.network.chat.Component.literal(getQuestion(pollName)));
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> removePoll(context.getSource(), StringArgumentType.getString(context, "name")))));

        literal.then(Commands.literal("list")
                .executes(context -> listPolls(context.getSource())));
    }

    private static int createPoll(CommandSourceStack source, String name, int durationInSeconds, String question) throws CommandSyntaxException{
        // check if the poll already exists
        if (pollNames.contains(name)){
            if (source.isPlayer()){
                EssentialsUtil.sendError(source, Component.text("Es läuft bereits eine Umfrage mit dem Namen ", SurfColors.ERROR)
                        .append(Component.text(name, SurfColors.TERTIARY)
                                .hoverEvent(HoverEvent.showText(Component.text("Zeit: ", SurfColors.INFO)
                                        .append(Component.text(EssentialsUtil.ticksToString(getDurationInSeconds(name)), SurfColors.GREEN))
                                        .append(Component.text("Frage: ", SurfColors.INFO))
                                        .append(Component.newline())
                                        .append(Component.text(getQuestion(name), SurfColors.TERTIARY)))))
                        .append(Component.text("!", SurfColors.ERROR)));
            }else {
                source.sendFailure(net.minecraft.network.chat.Component.literal("The poll ")
                        .withStyle(ChatFormatting.RED)
                        .append(name)
                        .withStyle(ChatFormatting.GOLD)
                        .append(" already exists!")
                        .withStyle(ChatFormatting.RED));
            }
            return 0;
        }
        // add the poll to the lists
        pollNames.add(name);
        pollQuestions.put(name, question);
        pollDurationInSeconds.put(name, durationInSeconds);

        // send poll to online players
        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            SurfApi.getUser(player.getUUID()).thenAcceptAsync(user -> {
                user.sendMessage(startPollMessage(name, durationInSeconds, question));
                user.playSound(Sound.BLOCK_BEACON_ACTIVATE, 0.5F, 0);
            });
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(SurfEssentials.getInstance(), bukkitTask -> { // runs every second
            // check if poll is canceled
            if (pollDurationInSeconds.getOrDefault(name, 0) <= 0){
                // send results to online players
                for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
                    if (!pollsToRemove.contains(name)){
                        SurfApi.getUser(player.getUUID()).thenAcceptAsync(user -> {
                            user.sendMessage(pollResultMessage(name));
                            user.playSound(Sound.BLOCK_BEACON_DEACTIVATE, 0.5F, 0);
                        });
                    }
                    votedPlayer.remove(player, name);
                }
                // remove the poll from the lists
                Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), bukkitTask2 -> {
                    removePollFromCount(name);
                    removeQuestion(name);
                    removeDuration(name);
                    pollNames.removeAll(Collections.singleton(name));
                }, 10);
                //cancel task
                bukkitTask.cancel();
                return;
            }
            // decrease the duration by one second
            setDurationInSeconds(name, getDurationInSeconds(name) - 1);

            switch (getDurationInSeconds(name)){ // remind players to vote if they haven't already
                case 18000, 14400, 10800, 7200, 3600, 1800, 900, 600, 300, 60, 30, 15 -> sendReminder(source, name);
            }
        }, 0, 20L);
        return 1;
    }

    private static int endPoll(CommandSourceStack source, String name) throws CommandSyntaxException{
        if (!isValidPoll(name)){
            if (source.isPlayer()){
                EssentialsUtil.sendError(source, Component.text("Die Umfrage ", SurfColors.ERROR)
                        .append(Component.text(name, SurfColors.TERTIARY))
                        .append(Component.text(" existiert nicht!")));
            }else {
                source.sendFailure(net.minecraft.network.chat.Component.literal("The poll ")
                        .withStyle(ChatFormatting.RED)
                        .append(name)
                        .withStyle(ChatFormatting.GOLD)
                        .append(" does not exist!")
                        .withStyle(ChatFormatting.RED));
            }
            return 0;
        }

        setDurationInSeconds(name, 0);

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, "Die Umfrage wird beendet.");
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("The poll will be closed."), false);
        }
        return 1;
    }

    private static int removePoll(CommandSourceStack source, String name) throws CommandSyntaxException {
        if (!isValidPoll(name)){
            if (source.isPlayer()){
                EssentialsUtil.sendError(source, Component.text("Die Umfrage ", SurfColors.ERROR)
                        .append(Component.text(name, SurfColors.TERTIARY))
                        .append(Component.text(" existiert nicht!")));
            }else {
                source.sendFailure(net.minecraft.network.chat.Component.literal("The poll ")
                        .withStyle(ChatFormatting.RED)
                        .append(name)
                        .withStyle(ChatFormatting.GOLD)
                        .append(" does not exist!")
                        .withStyle(ChatFormatting.RED));
            }
            return 0;
        }

        pollsToRemove.add(name);
        setDurationInSeconds(name, 0);

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, "Die Umfrage wird gelöscht.");
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("The poll will be removed."), false);
        }
        return 1;
    }

    private static int listPolls(CommandSourceStack source) throws CommandSyntaxException{
        if (pollNames.size() == 0){
            if (source.isPlayer()){
                EssentialsUtil.sendError(source, "Aktuell laufen keine Umfragen!");
            }else {
                source.sendFailure(net.minecraft.network.chat.Component.literal("No polls are active right now."));
            }
            return pollNames.size();
        }

        if (source.isPlayer()){
            ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();
            builder.append((Component.text("Aktuelle Umfragen: ", SurfColors.INFO)));
            for (String poll : pollNames) {
                builder.append(Component.newline()
                        .append(SurfApi.getPrefix()
                                .append(Component.text("                      - ", SurfColors.INFO)))
                        .append(Component.text(poll, SurfColors.TERTIARY)
                                .hoverEvent(HoverEvent.showText(Component.text("Dauer: ", SurfColors.INFO)
                                        .append(Component.text(EssentialsUtil.ticksToString(getDurationInSeconds(poll) * 20), SurfColors.GREEN))
                                        .append(Component.newline())
                                        .append(Component.text("Frage: ", SurfColors.INFO))
                                        .append(Component.text(getQuestion(poll), SurfColors.TERTIARY))))));
            }

            EssentialsUtil.sendSuccess(source, builder.build());
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("Active polls: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Arrays.toString(pollNames.toArray()))
                    .withStyle(ChatFormatting.GOLD), false);
        }
        return pollNames.size();
    }


    private static String getQuestion(String name){
        return pollQuestions.get(name);
    }
    private static void removeQuestion(String name){
        pollQuestions.remove(name);
    }

    private static Integer getDurationInSeconds(String name){
        return pollDurationInSeconds.get(name);
    }
    private static void setDurationInSeconds(String name, int durationInSeconds){
        pollDurationInSeconds.put(name, durationInSeconds);
    }
    private static void removeDuration(String name){
        pollDurationInSeconds.remove(name);
    }

    public static boolean isValidPoll(String name){
        return pollNames.contains(name);
    }

    public static void addYesCount(String name){
        yesCounts.put(name, yesCounts.getOrDefault(name, 0) + 1);
    }
    private static Integer getYesCount(String name){
        return yesCounts.getOrDefault(name, 0);
    }

    public static void addNoCount(String name){
        noCounts.put(name, noCounts.getOrDefault(name, 0) + 1);
    }
    private static Integer getNoCount(String name){
        return noCounts.getOrDefault(name, 0);
    }

    private static void removePollFromCount(String name){
        yesCounts.remove(name);
        noCounts.remove(name);
    }

    public static List<String> polls(){
        return pollNames;
    }

    public static void addVotedPlayer(ServerPlayer player, String name){
        votedPlayer.put(player, name);
    }

    public static boolean hasVoted(ServerPlayer player, String name){
        return name.equals(votedPlayer.getOrDefault(player, null));
    }


    private static void sendReminder(CommandSourceStack source, String name){
        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            if (votedPlayer.containsKey(player)) continue;
            SurfApi.getUser(player.getUUID()).thenAcceptAsync(user -> {
                user.sendMessage(pollReminderMessage(name));
                user.playSound(Sound.BLOCK_BEACON_POWER_SELECT, 0.5F, 0);
            });
        }
    }


    private static Component startPollMessage(String name, int durationInSeconds, String question){
        return line
                .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                .append(line)
                .append(Component.text("Neue Umfrage: %s".formatted(name), SurfColors.SECONDARY))
                .append(line)
                .append(line)
                .append(Component.text(question, TextColor.fromHexString("#eea990")))
                .append(line)
                .append(line)
                .append(Component.text("Verbleibende Zeit: ", SurfColors.INFO))
                .append(Component.text(EssentialsUtil.ticksToString(durationInSeconds * 20), TextColor.fromHexString("#FF7F50")))
                .append(line)
                .append(line)
                .append(Component.text("Ja ", SurfColors.GREEN)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Abstimmen", SurfColors.DARK_GRAY)))
                        .clickEvent(ClickEvent.runCommand("/vote %s yes".formatted(name))))
                .append(Component.text("|", SurfColors.GRAY))
                .append(Component.text(" Nein", SurfColors.RED)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Abstimmen", SurfColors.DARK_GRAY)))
                        .clickEvent(ClickEvent.runCommand("/vote %s no".formatted(name))))
                .append(line)
                .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                .append(Component.newline());
    }

    private static Component pollReminderMessage(String name){
        return SurfApi.getPrefix()
                .append(line)
                .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                .append(line)
                .append(Component.text("Vergiss nicht zu voten: %s".formatted(name), SurfColors.SECONDARY))
                .append(line)
                .append(line)
                .append(Component.text(getQuestion(name), TextColor.fromHexString("#eea990")))
                .append(line)
                .append(line)
                .append(Component.text("Verbleibende Zeit: ", SurfColors.INFO))
                .append(Component.text(EssentialsUtil.ticksToString(getDurationInSeconds(name) * 20), SurfColors.TERTIARY))
                .append(line)
                .append(line)
                .append(Component.text("Ja ", SurfColors.GREEN)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Abstimmen", SurfColors.DARK_GRAY)))
                        .clickEvent(ClickEvent.runCommand("/vote %s yes".formatted(name))))
                .append(Component.text("|", SurfColors.GRAY))
                .append(Component.text(" Nein", SurfColors.RED)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Abstimmen", SurfColors.DARK_GRAY)))
                        .clickEvent(ClickEvent.runCommand("/vote %s no".formatted(name))))
                .append(line)
                .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                .append(Component.newline());
    }

    private static Component pollResultMessage(String name) {
        return line.append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                .append(line)
                .append(Component.text("Ergebnisse von: %s".formatted(name), SurfColors.SECONDARY))
                .append(line)
                .append(line)
                .append(Component.text(getQuestion(name), TextColor.fromHexString("#eea990")))
                .append(line)
                .append(line)
                .append(Component.text("Ja ", SurfColors.GREEN)
                        .append(Component.text("%dx".formatted(getYesCount(name)))))
                .append(Component.text(" | ", SurfColors.GRAY))
                .append(Component.text("Nein ", SurfColors.RED)
                        .append(Component.text("%dx".formatted(getNoCount(name)), SurfColors.RED)))
                .append(line)
                .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                .append(Component.newline());
    }
}
