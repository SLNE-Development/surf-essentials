package dev.slne.surf.essentialsold.commands.general.other;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.SurfEssentials;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TimerCommand extends EssentialsCommand {
    private static final Title.Times defaultTimes = Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ofSeconds(1));

    private static final HashMap<BossBar, Boolean> BOSS_BARS = new HashMap<>(); // Bossbar - cancelled
    private static final List<Integer> titleTaskIds = new ArrayList<>();
    private static final List<Integer> actionbarTaskIds = new ArrayList<>();

    public TimerCommand() {
        super("timer", "timer <where> <time> <targets> [<Timer name>]", "Shows a timer to the players", "countdown");

        withPermission(Permissions.TIMER_PERMISSION);

        then(literal("actionbar")
                .then(timeArgument("time")
                        .then(playersArgument("targets")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> actionbarTimer(
                                        sender.getCallee(),
                                        args.getUnchecked("time"),
                                        args.getUnchecked("targets"),
                                        "Timer"
                                ))
                                .then(wordArgument("timerName")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> actionbarTimer(
                                                sender.getCallee(),
                                                args.getUnchecked("time"),
                                                args.getUnchecked("targets"),
                                                args.getUnchecked("timerName")
                                        ))
                                ))));

        then(literal("title")
                .then(timeArgument("time")
                        .then(playersArgument("targets")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> titleTimer(
                                        sender.getCallee(),
                                        args.getUnchecked("time"),
                                        args.getUnchecked("targets")
                                )))));

        then(literal("bossbar")
                .then(timeArgument("time")
                        .then(playersArgument("targets")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> bossbarTimer(
                                        sender.getCallee(),
                                        args.getUnchecked("time"),
                                        args.getUnchecked("targets"),
                                        "Timer"
                                ))
                                .then(wordArgument("timerName")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> bossbarTimer(
                                                sender.getCallee(),
                                                args.getUnchecked("time"),
                                                args.getUnchecked("targets"),
                                                args.getUnchecked("timerName")
                                        ))
                                ))));

        then(literal("cancel")
                .then(literal("bossbar")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> removeBossbars(sender.getCallee())))
                .then(literal("title")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> removeTitles(sender.getCallee())))
                .then(literal("actionbar")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> removeActionbars(sender.getCallee()))));
    }

    private int actionbarTimer(CommandSender source, Integer timeInTicks, Collection<Player> targetsUnchecked, String timerName) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        val targetUUIDS = targets.stream().map(Entity::getUniqueId).toList();

        val timeInSeconds = new AtomicInteger(timeInTicks / 20);

        playStartSound(targets);

        Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
            if (timeInSeconds.get() <= 0) bukkitTask.cancel();
            if (!actionbarTaskIds.contains(bukkitTask.getTaskId())) actionbarTaskIds.add(bukkitTask.getTaskId());

            val actionBarText = Component.text("%s:".formatted(timerName), Colors.INFO)
                    .append(Component.text(" %s".formatted(EssentialsUtil.ticksToString(timeInSeconds.get() * 20)), Colors.GREEN));

            for (UUID uuid : targetUUIDS) {
                val player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.sendActionBar(actionBarText);
                }
            }

            playSounds(targets, timeInSeconds.get());

            timeInSeconds.getAndDecrement();
        }, 0, 20);

        return sendSuccess(source, timeInTicks);
    }

    private int titleTimer(CommandSender source, Integer timeInTicks, Collection<Player> targetsUnchecked) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        val targetUUIDS = targets.stream().map(Entity::getUniqueId).toList();
        val timeInSeconds = new AtomicInteger(timeInTicks / 20);
        val titleText = Component.empty();

        playStartSound(targets);

        Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
            if (timeInSeconds.get() <= 0) bukkitTask.cancel();
            if (!titleTaskIds.contains(bukkitTask.getTaskId())) titleTaskIds.add(bukkitTask.getTaskId());

            val subtitleText = Component.text(EssentialsUtil.ticksToString(timeInSeconds.get() * 20), Colors.GREEN);

            for (UUID uuid : targetUUIDS) {
                val player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.showTitle(Title.title(titleText, subtitleText, defaultTimes));
                }
            }

            playSounds(targets, timeInSeconds.get());

            timeInSeconds.getAndDecrement();
        }, 0, 20);

        return sendSuccess(source, timeInTicks);
    }

    private int bossbarTimer(CommandSender source, Integer timeInTicks, Collection<Player> targetsUnchecked, String timerName) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        val targetUUIDS = targets.stream().map(Entity::getUniqueId).toList();
        val timeInSeconds = new AtomicInteger(timeInTicks / 20);
        val bossBar = BossBar.bossBar(Component.text(timerName, Colors.VARIABLE_VALUE), 1f, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);

        BOSS_BARS.put(bossBar, false);

        playStartSound(targets);
        for (UUID targetUUID : targetUUIDS) {
            val player = Bukkit.getPlayer(targetUUID);
            if (player == null) continue;
            player.showBossBar(bossBar);
        }

        Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
            if (timeInSeconds.get() <= 0 || isCanceled(bossBar)) {
                for (UUID uuid : targetUUIDS) {
                    val player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.hideBossBar(bossBar);
                    }
                }
                bukkitTask.cancel();
            }

            val percent = (((timeInSeconds.get() * 20) * 100.0f) / timeInTicks) / 100.0f;
            bossBar.progress(percent);
            playSounds(targets, timeInSeconds.get());

            timeInSeconds.getAndDecrement();
        }, 0, 20);

        return sendSuccess(source, timeInTicks);
    }

    private int removeBossbars(CommandSender source) {
        BOSS_BARS.replaceAll((customBossEvent, canceled) -> true);

        EssentialsUtil.sendSuccess(source, "Alle Bossbar-Timer wurden abgebrochen!");
        return 1;
    }

    private int removeTitles(CommandSender source) {
        for (Integer titleTaskId : titleTaskIds) {
            Bukkit.getScheduler().cancelTask(titleTaskId);
        }

        EssentialsUtil.sendSuccess(source, "Alle Titel-Timer wurden abgebrochen!");

        return 1;
    }

    private int removeActionbars(CommandSender source) {
        for (Integer actionbarTaskId : actionbarTaskIds) {
            Bukkit.getScheduler().cancelTask(actionbarTaskId);
        }

        EssentialsUtil.sendSuccess(source, "Alle Actionbar-Timer wurden abgebrochen!");

        return 1;
    }

    private void playSounds(Collection<? extends Audience> targets, int timeInSeconds) {
        switch (timeInSeconds) {
            case 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 -> {
                for (Audience target : targets) {
                    target.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 1f, 2f));
                }
            }
        }
    }

    private void playStartSound(Collection<? extends Audience> targets) {
        for (Audience target : targets) {
            target.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Sound.Source.MASTER, 1f, 0.9f));
        }
    }

    private int sendSuccess(CommandSender source, int timeInTicks) {
        EssentialsUtil.sendSuccess(source, Component.text("Ein ", Colors.SUCCESS)
                .append(Component.text(EssentialsUtil.ticksToString(timeInTicks), Colors.TERTIARY))
                .append(Component.text(" Timer wurde gestartet!")));

        return 1;
    }

    private boolean isCanceled(BossBar customBossEvent) {
        return BOSS_BARS.getOrDefault(customBossEvent, false);
    }
}
