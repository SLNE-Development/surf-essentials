package dev.slne.surf.essentialsold.commands.general.other.poll;

import dev.slne.surf.essentialsold.SurfEssentials;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class Poll {
    private static final Map<String, Poll> polls = new HashMap<>();
    private static final Component line = Component.newline().append(EssentialsUtil.getPrefix());

    public static boolean checkPollExists(String name) {
        return polls.get(name) != null;
    }

    public static @NotNull Poll getPoll(String name) {
        if (!checkPollExists(name))
            throw new IllegalStateException("No poll exists with the name '%s'".formatted(name));
        return polls.get(name);
    }

    Set<UUID> votedPlayers = new HashSet<>();
    String name;
    String question;
    @NonFinal
    int duration;
    @NonFinal
    boolean startedTimer;
    @Setter
    @NonFinal
    boolean stopSilent;
    @NonFinal
    int taskId;
    @NonFinal
    int yesCount;
    @NonFinal
    int noCount;

    @Builder
    public Poll(String name, String question, int durationInSeconds) {
        this.name = name;
        this.question = question;
        this.duration = durationInSeconds;

        polls.putIfAbsent(name, this);
    }

    @Contract(pure = true)
    public static @NotNull Collection<Poll> getPolls() {
        return polls.values();
    }

    public void startMessage(Collection<? extends Player> players) {
        players.forEach(player -> player.sendMessage(line
                .append(Component.text("----------------------------------------", Colors.DARK_GRAY))
                .append(line)
                .append(Component.text("Neue Umfrage: %s".formatted(name), Colors.SECONDARY))
                .append(line)
                .append(line)
                .append(Component.text(question, TextColor.fromHexString("#eea990")))
                .append(line)
                .append(line)
                .append(Component.text("Verbleibende Zeit: ", Colors.INFO))
                .append(Component.text(EssentialsUtil.ticksToString(duration * 20), TextColor.fromHexString("#FF7F50")))
                .append(line)
                .append(line)
                .append(Component.text("Ja ", Colors.GREEN)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Abstimmen", Colors.DARK_GRAY)))
                        .clickEvent(ClickEvent.runCommand("/vote %s yes".formatted(name))))
                .append(Component.text("|", Colors.GRAY))
                .append(Component.text(" Nein", Colors.RED)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Abstimmen", Colors.DARK_GRAY)))
                        .clickEvent(ClickEvent.runCommand("/vote %s no".formatted(name))))
                .append(line)
                .append(Component.text("----------------------------------------", Colors.DARK_GRAY))
                .append(Component.newline())));
    }

    public void startTimer() {
        if (startedTimer) throw new IllegalStateException("Timer already started");
        startedTimer = true;

        Bukkit.getScheduler().runTaskTimerAsynchronously(SurfEssentials.getInstance(), bukkitTask -> { // runs every second
            this.taskId = bukkitTask.getTaskId();
            if (duration <= 0) {
                stop();
                return;
            }
            duration--;

            switch (duration) { // remind players to vote if they haven't already
                case 18000, 14400, 10800, 7200, 3600, 1800, 900, 600, 300, 60, 30, 15 -> sendReminder();
            }
        }, 0, 20L);

    }

    public void stop() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!stopSilent) {
                player.sendMessage(line.append(Component.text("----------------------------------------", Colors.DARK_GRAY))
                        .append(line)
                        .append(Component.text("Ergebnisse von: %s".formatted(name), Colors.SECONDARY))
                        .append(line)
                        .append(line)
                        .append(Component.text(question, TextColor.fromHexString("#eea990")))
                        .append(line)
                        .append(line)
                        .append(Component.text("Ja ", Colors.GREEN)
                                .append(Component.text("%dx".formatted(yesCount))))
                        .append(Component.text(" | ", Colors.GRAY))
                        .append(Component.text("Nein ", Colors.RED)
                                .append(Component.text("%dx".formatted(noCount), Colors.RED)))
                        .append(line)
                        .append(Component.text("----------------------------------------", Colors.DARK_GRAY))
                        .append(Component.newline()));
                player.playSound(player, Sound.BLOCK_BEACON_DEACTIVATE, 0.5F, 0);
            }
            votedPlayers.remove(player.getUniqueId());
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), bukkitTask2 -> polls.remove(name), 10);
        Bukkit.getScheduler().cancelTask(taskId);
    }


    public void sendReminder() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (votedPlayers.contains(player.getUniqueId())) continue;

            player.sendMessage(EssentialsUtil.getPrefix()
                    .append(line)
                    .append(Component.text("----------------------------------------", Colors.DARK_GRAY))
                    .append(line)
                    .append(Component.text("Vergiss nicht zu voten: %s".formatted(name), Colors.VARIABLE_VALUE))
                    .append(line)
                    .append(line)
                    .append(Component.text(question, TextColor.fromHexString("#eea990")))
                    .append(line)
                    .append(line)
                    .append(Component.text("Verbleibende Zeit: ", Colors.INFO))
                    .append(Component.text(EssentialsUtil.ticksToString(duration * 20), Colors.TERTIARY))
                    .append(line)
                    .append(line)
                    .append(Component.text("Ja ", Colors.GREEN)
                            .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Abstimmen", Colors.DARK_GRAY)))
                            .clickEvent(ClickEvent.runCommand("/vote %s yes".formatted(name))))
                    .append(Component.text("|", Colors.GRAY))
                    .append(Component.text(" Nein", Colors.RED)
                            .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Abstimmen", Colors.DARK_GRAY)))
                            .clickEvent(ClickEvent.runCommand("/vote %s no".formatted(name))))
                    .append(line)
                    .append(Component.text("----------------------------------------", Colors.DARK_GRAY))
                    .append(Component.newline()));

            player.playSound(player, Sound.BLOCK_BEACON_POWER_SELECT, 0.5F, 0);
        }
    }

    public boolean addVote(Player player, boolean isYes) {
        if (votedPlayers.contains(player.getUniqueId())) return false;

        if (isYes) yesCount++;
        else noCount++;

        votedPlayers.add(player.getUniqueId());
        return true;
    }
}
