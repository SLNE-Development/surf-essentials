package dev.slne.surf.essentials.main.commands.general.other.poll;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicReference;

public class PollCreateCommand {
    public static void create(Player player, String[] args) {
        if (args.length < 3) {
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Benutzung: ", SurfColors.WARNING))
                    .append(Component.text("/poll create <name> <time> <question>", SurfColors.INFO))));
            return;
        }

        if (!EssentialsUtil.isInt(args[2])) {
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du musst eine gültige Zeit angeben!", SurfColors.ERROR))));
            return;
        }

        String pollName = args[1];
        AtomicReference<Integer> timeInSeconds = new AtomicReference<>(Integer.parseInt(args[2]));
        String question = "";
        for (int i = 3; i < args.length; i++) question += args[i] + " ";
        final String finalQuestion = question;

        PollUtil.addPollName(pollName);
        PollUtil.isPoll(true);
        PollUtil.startPoll(pollName, timeInSeconds.get(), question);
        new BukkitRunnable() {
            @Override
            public void run() {
                int time = timeInSeconds.get();
                if (time == 0) cancel();
                time -= 1;
                if (time == 30*60){
                    PollUtil.reminder(pollName, finalQuestion, time);
                }
                if (time == 15*60){
                    PollUtil.reminder(pollName, finalQuestion, time);
                }
                if (time == 10*60){
                    PollUtil.reminder(pollName, finalQuestion, time);
                }
                if (time == 5*60){
                    PollUtil.reminder(pollName, finalQuestion, time);
                }
                if (time == 60){
                    PollUtil.reminder(pollName, finalQuestion, time);
                }
                timeInSeconds.set(time);
            }
        }.runTaskTimerAsynchronously(SurfEssentials.getInstance(), 0, 20);

        Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), () -> {
            PollUtil.endPoll(pollName, finalQuestion);
        }, timeInSeconds.get() * 20);
    }






/**














        String allArgs = "";
        for (int i = 2; i < args.length; i++) allArgs += args[i] + " ";//adds all arguments to the string
        final String finalAllArgs = allArgs;
        int timeSeconds;

        if (!EssentialsUtil.isInt(args[1])) timeSeconds = 900;
        else timeSeconds = Integer.parseInt(args[1]);

        if (args.length == 2){
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Bitte gib eine Frage an", SurfColors.INFO))));
            return;
        }

        PollUtil.isPoll(true);
        Bukkit.broadcast(SurfApi.getPrefix()
                .append(Component.newline())
                .append(SurfApi.getPrefix()
                        .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY)))
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                .append(Component.text(finalAllArgs, TextColor.fromHexString("#eea990")))
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                .append(Component.newline())
                .append(SurfApi.getPrefix()
                        .append(Component.text("Zeit: ", TextColor.fromHexString("#E5E1D6")))
                        .append(Component.text(time(timeSeconds), TextColor.fromHexString("#c0dad4"))))
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                .append(Component.text("Ja ", SurfColors.GREEN)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Abstimmen", SurfColors.DARK_GRAY)))
                        .clickEvent(ClickEvent.runCommand("/vote yes")))
                .append(Component.text("|", SurfColors.GRAY))
                .append(Component.text(" Nein", SurfColors.RED)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Abstimmen", SurfColors.DARK_GRAY)))
                        .clickEvent(ClickEvent.runCommand("/vote no")))
                .append(Component.newline())
                .append(SurfApi.getPrefix()
                        .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY)))
                .append(Component.newline())
                .append(SurfApi.getPrefix()));


        SurfEssentials.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), () -> {
            Bukkit.broadcast(SurfApi.getPrefix()
                    .append(Component.newline())
                    .append(SurfApi.getPrefix()
                            .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY)))
                    .append(Component.newline())
                    .append(SurfApi.getPrefix())
                    .append(Component.text(finalAllArgs, TextColor.fromHexString("#eea990")))
                    .append(Component.newline())
                    .append(SurfApi.getPrefix())
                    .append(Component.newline())
                    .append(SurfApi.getPrefix()
                            .append(Component.text("Zeit: ", TextColor.fromHexString("#E5E1D6")))
                            .append(Component.text("Beendet", TextColor.fromHexString("#c0dad4"))))
                    .append(Component.newline())
                    .append(SurfApi.getPrefix())
                    .append(Component.newline())
                    .append(SurfApi.getPrefix())
                    .append(Component.text("Ja ", SurfColors.GREEN)
                            .append(Component.text("%d%x".formatted(PollUtil.yesCount))))
                    .append(Component.text(" | ", SurfColors.GRAY))
                    .append(Component.text("Nein ", SurfColors.RED)
                            .append(Component.text("%d%%x".formatted(PollUtil.noCount), SurfColors.RED)))
                    .append(Component.newline())
                    .append(SurfApi.getPrefix()
                            .append(Component.text("----------------------------------------", SurfColors.DARK_GRAY)))
                    .append(Component.newline())
                    .append(SurfApi.getPrefix()));
            PollUtil.isPoll(false);
        }, timeSeconds* 20);
    }

    private static String time(int totalSeconds){
        int hours, minutes, seconds;
        hours = totalSeconds / 3600;
        minutes = (totalSeconds % 3600) / 60;
        seconds = totalSeconds % 60;

        return String.format("%02dh:%02dm:%02ds", hours, minutes, seconds);
    }
 */
}
