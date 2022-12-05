package dev.slne.surf.essentials.main.commands.general.other.poll;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class PollUtil {
    private static Component line = Component.newline().append(SurfApi.getPrefix());

    private static boolean poll;
    public static  int yesCount;
    public static  int noCount;
    public static ArrayList<String> polls = new ArrayList<>();
    public static  ArrayList<String> hasVoted = new ArrayList<>();
    public static ArrayList<String> yesCount2 = new ArrayList<>();

    public static void isPoll(Boolean bool){
        poll = bool;
    }

    public static Boolean isPoll(){
        return poll;
    }

    public static void addYesCount(String poll){
        yesCount2.add(poll);
    }

    public static Integer getYesCount(String poll){
        AtomicInteger count = new AtomicInteger();
        yesCount2.forEach(s -> {
            if (s.contains(poll)){
                count.set(count.get() + 1);
            }
        });
        return count.get();
    }

    public static void resetYesCount(String poll) {
        yesCount2.remove(poll);
        yesCount = 0;
    }

    public static void addNoCount(int count){
        noCount += count;
    }

    public static void resetNoCount(){
        noCount = 0;
    }

    public static void addPollName(String name){
        polls.add(name);
    }

    public static void removePollName(String name){
        polls.remove(name);
    }

    public static void addVoted(Player player, String poll){
        hasVoted.add(player.getUniqueId() + "_" + poll);
    }

    public static Boolean hasVoted(Player player, String poll){
        return hasVoted.contains(player.getUniqueId() + "_" + poll);
    }

    public static void startPoll(String name, Integer timeInSeconds, String description){
        Bukkit.broadcast(line
                .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                .append(line)
                .append(Component.text("Neue Umfrage: %s".formatted(name), SurfColors.SECONDARY))
                .append(line)
                .append(line)
                .append(Component.text(description, TextColor.fromHexString("#eea990")))
                .append(line)
                .append(line)
                .append(Component.text("Verbleibende Zeit: ", SurfColors.INFO))
                .append(Component.text(convertSeconds(timeInSeconds), SurfColors.TERTIARY))
                .append(line)
                .append(line)
                .append(Component.text("Ja ", SurfColors.GREEN)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Abstimmen", SurfColors.DARK_GRAY)))
                        .clickEvent(ClickEvent.runCommand("/vote yes")))
                .append(Component.text("|", SurfColors.GRAY))
                .append(Component.text(" Nein", SurfColors.RED)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Abstimmen", SurfColors.DARK_GRAY)))
                        .clickEvent(ClickEvent.runCommand("/vote no")))
                .append(line)
                .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                .append(Component.newline()));
    }
    public static void endPoll(String name, String description){
        Bukkit.broadcast(line
                .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                .append(line)
                .append(Component.text("Ergebnisse von: %s".formatted(name), SurfColors.SECONDARY))
                .append(line)
                .append(line)
                .append(Component.text(description, TextColor.fromHexString("#eea990")))
                .append(line)
                .append(line)
                .append(Component.text("Ja ", SurfColors.GREEN)
                        .append(Component.text("%dx".formatted(PollUtil.yesCount))))
                .append(Component.text(" | ", SurfColors.GRAY))
                .append(Component.text("Nein ", SurfColors.RED)
                        .append(Component.text("%dx".formatted(PollUtil.noCount), SurfColors.RED)))
                .append(line)
                .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                .append(Component.newline()));
        removePollName(name);
    }

    private static String convertSeconds(int totalSeconds){
        int hours, minutes, seconds;
        hours = totalSeconds / 3600;
        minutes = (totalSeconds % 3600) / 60;
        seconds = totalSeconds % 60;

        return String.format("%02dh:%02dm:%02ds", hours, minutes, seconds);
    }

    public static void reminder(String poll, String description, int timeLeftSeconds){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (hasVoted.contains(onlinePlayer.getUniqueId() + "_" + poll)) continue;
            SurfApi.getUser(onlinePlayer).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(line)
                    .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                    .append(line)
                    .append(Component.text("Vergiss nicht zu voten: %s".formatted(poll), SurfColors.SECONDARY))
                    .append(line)
                    .append(line)
                    .append(Component.text(description, TextColor.fromHexString("#eea990")))
                    .append(line)
                    .append(line)
                    .append(Component.text("Verbleibende Zeit: ", SurfColors.INFO))
                    .append(Component.text(convertSeconds(timeLeftSeconds), SurfColors.TERTIARY))
                    .append(line)
                    .append(line)
                    .append(Component.text("Ja ", SurfColors.GREEN)
                            .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Abstimmen", SurfColors.DARK_GRAY)))
                            .clickEvent(ClickEvent.runCommand("/vote yes")))
                    .append(Component.text("|", SurfColors.GRAY))
                    .append(Component.text(" Nein", SurfColors.RED)
                            .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Abstimmen", SurfColors.DARK_GRAY)))
                            .clickEvent(ClickEvent.runCommand("/vote no")))
                    .append(line)
                    .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY))
                    .append(Component.newline())));
        }
    }
    public static void successVote(Player target){
        SurfApi.getUser(target).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Vielen Dank für deine Stimme", SurfColors.SUCCESS))));
    }
}
