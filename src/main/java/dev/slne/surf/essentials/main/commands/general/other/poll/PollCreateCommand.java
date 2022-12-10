package dev.slne.surf.essentials.main.commands.general.other.poll;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class PollCreateCommand {
    public static void create(Player player, String[] args) {
        // Check if there are enough arguments
        if (args.length < 3) {
            // Not enough arguments, send an error message
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Korrekte Benutzung: ", SurfColors.RED))
                    .append(Component.text("/poll create <name> <time> <question>", SurfColors.TERTIARY))));
            return;
        }

        // Check if the time argument is an integer
        if (!EssentialsUtil.isInt(args[2])) {
            // Time argument is not an integer, send an error message
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du musst eine gültige Zeit angeben!", SurfColors.ERROR))));
            return;
        }

        // Get the poll name from the arguments
        String pollName = args[1];

        // Check if the poll already exists
        if (PollUtil.polls.contains(pollName)) {
            // Poll already exists, send an error message
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Die Umfrage existiert schon!", SurfColors.ERROR))));
            return;
        }

        // Get the time for the poll in seconds from the arguments
        AtomicReference<Integer> timeInSeconds = new AtomicReference<>(Integer.parseInt(args[2]));

        // Get the poll question from the arguments
        String question = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

        // Start the poll
        PollUtil.startPoll(pollName, timeInSeconds.get(), question);

        // Create a new task to run asynchronously
        new BukkitRunnable() {
            @Override
            public void run() {
                // Get the time remaining for the poll
                int time = timeInSeconds.get();

                // Check if the poll time has elapsed
                if (time == 0) cancel(); // Cancel the task

                // Decrement the time remaining for the poll
                time -= 1;

                // Check if a reminder should be sent
                if (time == 30 * 60 || time == 15 * 60 || time == 10 * 60 || time == 5 * 60 || time == 60) {
                    // Send a reminder message
                    PollUtil.reminder(pollName, time);
                }

                // Update the time remaining for the poll
                timeInSeconds.set(time);
            }
        }.runTaskTimerAsynchronously(SurfEssentials.getInstance(), 0, 20);

        // Schedule a task to end the poll when the time elapses
        Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), () -> {
            // End the poll
            PollUtil.endPoll(pollName, false);
        }, timeInSeconds.get() * 20);
    }

}
