package dev.slne.surf.essentials.main.commands.general.other.poll;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class PollUtil {
    private static Component line = Component.newline().append(SurfApi.getPrefix());

    private static boolean poll;
    public static List<String> polls = Collections.synchronizedList(new ArrayList<>());
    public static List<String> pollDescription = Collections.synchronizedList(new ArrayList<>());
    public static List<String> canceledPolls = Collections.synchronizedList(new ArrayList<>());
    public static List<String> deletedPolls = Collections.synchronizedList(new ArrayList<>());
    public static List<String> hasVoted = Collections.synchronizedList(new ArrayList<>());
    public static List<String> yesCount = Collections.synchronizedList(new ArrayList<>());
    public static List<String> noCount = Collections.synchronizedList(new ArrayList<>());


    public static void isPoll(Boolean bool){
        poll = bool;
    }

    public static Boolean isPoll(){
        return poll;
    }

    public static void addYesCount(String poll){
        yesCount.add(poll);
    }

    public static long getYesCount(String poll){
        return yesCount.parallelStream().filter(s -> s.equals(poll)).count();
    }

    public static void resetYesCount(String poll) {
        Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), () -> yesCount.removeIf(s -> s.equals(poll)));
    }

    public static void addNoCount(String poll) {
        Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), () -> noCount.add(poll));
    }

    public static long getNoCount(String poll){
        return noCount.parallelStream().filter(s -> s.equals(poll)).count();
    }

    public static void resetNoCount(String poll) {
        Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), () -> noCount.removeIf(s -> s.equals(poll)));
    }

    public static void addPollName(String name) {
        Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), () -> polls.add(name));
    }

    public static void removePollName(String name) {
        Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), () -> polls.remove(name));
    }

    public static void addVoted(Player player, String poll) {
        Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), () -> hasVoted.add(player.getUniqueId() + "_" + poll));
    }
    public static void removeVoted(Player player, String poll) {
        Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), () -> hasVoted.remove(player.getUniqueId() + "_" + poll));
    }

    public static Boolean hasVoted(Player player, String poll){
        return hasVoted.contains(player.getUniqueId() + "_" + poll);
    }


    public static void addPollDescription(String poll, String description) {
        pollDescription.add(poll + "_" + description);
    }
    public static String getPollDescription(String poll) {
        Optional<String> description = pollDescription.parallelStream()
                .filter(s -> s.startsWith(poll))
                .findFirst();
        return description.map(s -> s.replaceAll(poll + "_", "")).orElse("[keine Beschreibung angegeben...]");
    }
    public static void removePollDescription(String poll) {
        if (pollDescription != null) pollDescription.parallelStream().filter(s -> s.startsWith(poll));
    }
    public static void deletePoll(String poll){
        deletedPolls.add(poll);
        polls.remove(poll);
        removePollDescription(poll);
        removePollName(poll);
        resetNoCount(poll);
        resetYesCount(poll);
        isPoll(false);
    }

    public static void startPoll(String name, Integer timeInSeconds, String description){
        addPollDescription(name, description);
        Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), () -> Bukkit.broadcast(line
                .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                .append(line)
                .append(Component.text("Neue Umfrage: %s".formatted(name), SurfColors.SECONDARY))
                .append(line)
                .append(line)
                .append(Component.text(getPollDescription(name), TextColor.fromHexString("#eea990")))
                .append(line)
                .append(line)
                .append(Component.text("Verbleibende Zeit: ", SurfColors.INFO))
                .append(Component.text(convertSeconds(timeInSeconds), TextColor.fromHexString("#FF7F50")))
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
                .append(Component.newline())));
        for (Player player : Bukkit.getOnlinePlayers()) {
            SurfApi.getUser(player).thenAcceptAsync(user -> user.playSound(Sound.BLOCK_BEACON_ACTIVATE, 1, 3));
        }
        addPollName(name);
        isPoll(true);
    }
    public static void endPoll(String name, boolean canceled){
        if (canceledPolls.contains(name) && !canceled){
            canceledPolls.remove(name);
            return;
        }
        if (deletedPolls.contains(name)){
            canceledPolls.remove(name);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), () -> Bukkit.broadcast(line
                .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                .append(line)
                .append(Component.text("Ergebnisse von: %s".formatted(name), SurfColors.SECONDARY))
                .append(line)
                .append(line)
                .append(Component.text(getPollDescription(name), TextColor.fromHexString("#eea990")))
                .append(line)
                .append(line)
                .append(Component.text("Ja ", SurfColors.GREEN)
                        .append(Component.text("%dx".formatted(PollUtil.getYesCount(name)))))
                .append(Component.text(" | ", SurfColors.GRAY))
                .append(Component.text("Nein ", SurfColors.RED)
                        .append(Component.text("%dx".formatted(PollUtil.getNoCount(name)), SurfColors.RED)))
                .append(line)
                .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                .append(Component.newline())));
        for (Player player : Bukkit.getOnlinePlayers()) {
            SurfApi.getUser(player).thenAcceptAsync(user -> user.playSound(Sound.BLOCK_BEACON_DEACTIVATE, 1, 3));
        }
        removePollDescription(name);
        removePollName(name);
        resetNoCount(name);
        resetYesCount(name);
        isPoll(false);
    }

    private static String convertSeconds(long totalSeconds){
        LocalTime time = LocalTime.ofSecondOfDay(0);
        try {
            // Create a LocalTime object from the seconds
           time = LocalTime.ofSecondOfDay(totalSeconds);
        }catch (DateTimeException ignored){}

        // returns the time in the format "HH:mm:ss"
        return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static void reminder(String poll, int timeLeftSeconds){
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (hasVoted.contains(onlinePlayer.getUniqueId() + "_" + poll)) continue;
                SurfApi.getUser(onlinePlayer).thenAcceptAsync(user -> {
                    user.sendMessage(SurfApi.getPrefix()
                        .append(line)
                        .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                        .append(line)
                        .append(Component.text("Vergiss nicht zu voten: %s".formatted(poll), SurfColors.SECONDARY))
                        .append(line)
                        .append(line)
                        .append(Component.text(getPollDescription(poll), TextColor.fromHexString("#eea990")))
                        .append(line)
                        .append(line)
                        .append(Component.text("Verbleibende Zeit: ", SurfColors.INFO))
                        .append(Component.text(convertSeconds(timeLeftSeconds), SurfColors.TERTIARY))
                        .append(line)
                        .append(line)
                        .append(Component.text("Ja ", SurfColors.GREEN)
                                .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Abstimmen", SurfColors.DARK_GRAY)))
                                .clickEvent(ClickEvent.runCommand("/vote %s yes".formatted(poll))))
                        .append(Component.text("|", SurfColors.GRAY))
                        .append(Component.text(" Nein", SurfColors.RED)
                                .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Abstimmen", SurfColors.DARK_GRAY)))
                                .clickEvent(ClickEvent.runCommand("/vote %s no".formatted(poll))))
                        .append(line)
                        .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                        .append(Component.newline()));
                    user.playSound(Sound.BLOCK_BEACON_POWER_SELECT, 1,3);
                });
            }
    }
    public static void successVote(Player target){
        SurfApi.getUser(target).thenAcceptAsync(user -> {
            user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Vielen Dank für deine Stimme", SurfColors.SUCCESS)));
            user.playSound(Sound.BLOCK_BEACON_POWER_SELECT, 1, 3);
        });
    }
}
