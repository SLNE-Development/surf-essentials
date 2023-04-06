package dev.slne.surf.essentials.commands.general.other;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TimerCommand extends BrigadierCommand {
    private static final HashMap<CustomBossEvent, Boolean> isBossbarCanceled = new HashMap<>();
    private static final List<Integer> titleTaskIds = new ArrayList<>();
    private static final List<Integer> actionbarTaskIds = new ArrayList<>();

    @Override
    public String[] names() {
        return new String[]{"timer", "countdown"};
    }

    @Override
    public String usage() {
        return "/timer <where> <time> <targets> [<Timer name>]";
    }

    @Override
    public String description() {
        return "Shows a timer to the targets";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.TIMER_PERMISSION));

        literal.then(Commands.literal("actionbar")
                .then(Commands.argument("time", TimeArgument.time())
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> actionbarTimer(context.getSource(), IntegerArgumentType.getInteger(context, "time"),
                                        EntityArgument.getPlayers(context, "targets"), "Timer"))
                                .then(Commands.argument("timerName", StringArgumentType.greedyString())
                                        .executes(context -> actionbarTimer(context.getSource(), IntegerArgumentType.getInteger(context, "time"),
                                                EntityArgument.getPlayers(context, "targets"), StringArgumentType.getString(context, "timerName")))))));

        literal.then(Commands.literal("bossbar")
                .then(Commands.argument("time", TimeArgument.time())
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("timerName", StringArgumentType.greedyString())
                                        .executes(context -> bossbarTimer(context.getSource(), IntegerArgumentType.getInteger(context, "time"),
                                                EntityArgument.getPlayers(context, "targets"), StringArgumentType.getString(context, "timerName")))))));

        literal.then(Commands.literal("title")
                .then(Commands.argument("time", TimeArgument.time())
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> titleTimer(context.getSource(), IntegerArgumentType.getInteger(context, "time"),
                                        EntityArgument.getPlayers(context, "targets"))))));

        literal.then(Commands.literal("removeall")
                .then(Commands.literal("actionbar")
                        .executes(context -> removeActionbars(context.getSource())))

                .then(Commands.literal("bossbar")
                        .executes(context -> removeBossbars(context.getSource())))

                .then(Commands.literal("title")
                        .executes(context -> removeTitles(context.getSource()))));
    }

    private int actionbarTimer(CommandSourceStack source, int timeInTicks, Collection<ServerPlayer> targetsUnchecked, String timerName) throws CommandSyntaxException{
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        Collection<UUID> targetUUIDS = targets.stream().map(Entity::getUUID).collect(Collectors.toSet());

        AtomicInteger timeInSeconds = new AtomicInteger(timeInTicks / 20);

        playStartSound(targets);

        Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
            if (timeInSeconds.get() <= 0) bukkitTask.cancel();
            if (!actionbarTaskIds.contains(bukkitTask.getTaskId())) actionbarTaskIds.add(bukkitTask.getTaskId());

            ClientboundSetActionBarTextPacket actionBarTextPacket = new ClientboundSetActionBarTextPacket(PaperAdventure
                    .asVanilla(Component.text("%s:".formatted(timerName), Colors.INFO)
                            .append(Component.text(" %s".formatted(EssentialsUtil.ticksToString(timeInSeconds.get() * 20)), Colors.GREEN))));

            sendTimerPacket(targetUUIDS, actionBarTextPacket);
            playSounds(targets, timeInSeconds.get());

            timeInSeconds.getAndDecrement();
        }, 0, 20);

       return sendSuccess(source, timeInTicks);
    }

    private int titleTimer(CommandSourceStack source, int timeInTicks, Collection<ServerPlayer> targetsUnchecked) throws CommandSyntaxException{
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        Collection<UUID> targetUUIDS = targets.stream().map(Entity::getUUID).collect(Collectors.toSet());
        AtomicInteger timeInSeconds = new AtomicInteger(timeInTicks / 20);
        ClientboundSetTitleTextPacket titleTextPacket = new ClientboundSetTitleTextPacket(net.minecraft.network.chat.Component.literal(" "));

        playStartSound(targets);

        Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
            if (timeInSeconds.get() <= 0) bukkitTask.cancel();
            if (!titleTaskIds.contains(bukkitTask.getTaskId())) titleTaskIds.add(bukkitTask.getTaskId());

            ClientboundSetSubtitleTextPacket subtitleTextPacket = new ClientboundSetSubtitleTextPacket(PaperAdventure
                    .asVanilla(Component.text(EssentialsUtil.ticksToString(timeInSeconds.get() * 20), Colors.GREEN)));

            sendTimerPacket(targetUUIDS, titleTextPacket, subtitleTextPacket);
            playSounds(targets, timeInSeconds.get());

            timeInSeconds.getAndDecrement();
        }, 0, 20);

        return sendSuccess(source, timeInTicks);
    }

    private int bossbarTimer(CommandSourceStack source, int timeInTicks, Collection<ServerPlayer> targetsUnchecked, String timerName) throws CommandSyntaxException{
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        Collection<UUID> targetUUIDS = targets.stream().map(Entity::getUUID).collect(Collectors.toSet());
        AtomicInteger timeInSeconds = new AtomicInteger(timeInTicks / 20);
        CustomBossEvents customBossEvents = source.getServer().getCustomBossEvents();

        CustomBossEvent customBossEvent = customBossEvents.create(new ResourceLocation("timer", UUID.randomUUID().toString()),
                ComponentUtils.updateForEntity(source, PaperAdventure.asVanilla(Component.text(timerName, Colors.TERTIARY)), null, 0));

        customBossEvent.setVisible(true);
        customBossEvent.setProgress(100f);
        customBossEvent.setDarkenScreen(false);
        customBossEvent.setColor(BossEvent.BossBarColor.GREEN);
        customBossEvent.setOverlay(BossEvent.BossBarOverlay.PROGRESS);

        isBossbarCanceled.put(customBossEvent, false);

        playStartSound(targets);
        for (UUID targetUUID : targetUUIDS) {
            ServerPlayer player = source.getServer().getPlayerList().getPlayer(targetUUID);
            if (player == null) continue;
            customBossEvent.addPlayer(player);
        }

        Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
            if (timeInSeconds.get() <= 0 || isCanceled(customBossEvent)){
                customBossEvent.removeAllPlayers();
                customBossEvents.remove(customBossEvent);
                bukkitTask.cancel();
            }

            float percent = (((timeInSeconds.get() * 20) * 100.0f) / timeInTicks) / 100.0f;
            customBossEvent.setProgress(percent);

            playSounds(targets, timeInSeconds.get());

            timeInSeconds.getAndDecrement();
        }, 0, 20);

        return sendSuccess(source, timeInTicks);
    }

    private int removeBossbars(CommandSourceStack source) throws CommandSyntaxException {
        isBossbarCanceled.replaceAll((customBossEvent, canceled) -> canceled = true);

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, "Alle Bossbar-Timer wurden abgebrochen!");
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("All boss bar timers were canceled!")
                    .withStyle(ChatFormatting.GREEN), false);
        }
        return 1;
    }

    private int removeTitles(CommandSourceStack source) throws CommandSyntaxException {
        for (Integer titleTaskId : titleTaskIds) {
            Bukkit.getScheduler().cancelTask(titleTaskId);
        }

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, "Alle Titel-Timer wurden abgebrochen!");
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("All title timers were canceled!")
                    .withStyle(ChatFormatting.GREEN), false);
        }
        return 1;
    }

    private int removeActionbars(CommandSourceStack source) throws CommandSyntaxException {
        for (Integer actionbarTaskId : actionbarTaskIds) {
            Bukkit.getScheduler().cancelTask(actionbarTaskId);
        }

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, "Alle Actionbar-Timer wurden abgebrochen!");
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("All actionbar timers were canceled!")
                    .withStyle(ChatFormatting.GREEN), false);
        }
        return 1;
    }

    private void sendTimerPacket(Collection<UUID> targetsUUIDS, Packet<?>... packets){
        ClientboundSetTitlesAnimationPacket animationPacket = new ClientboundSetTitlesAnimationPacket(0, 20, 20);

        for (UUID uuid : targetsUUIDS) {
            ServerPlayer player = MinecraftServer.getServer().getPlayerList().getPlayer(uuid);
            if (player != null){
                player.connection.send(animationPacket);
                for (Packet<?> packet : packets) {
                    player.connection.send(packet);
                }
            }
        }
    }

    private void playSounds(Collection<ServerPlayer> targets, int timeInSeconds){
        switch (timeInSeconds){
            case 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 -> {
                for (ServerPlayer target : targets) {
                    target.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 1f, 2f);
                }
            }
        }
    }

    private void playStartSound(Collection<ServerPlayer> targets){
        for (ServerPlayer target : targets) {
            target.playSound(SoundEvents.PLAYER_LEVELUP, 1f, 0.9f);
        }
    }

    private int sendSuccess(CommandSourceStack source, int timeInTicks) throws CommandSyntaxException {
        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, Component.text("Ein ", Colors.SUCCESS)
                    .append(Component.text(EssentialsUtil.ticksToString(timeInTicks), Colors.TERTIARY))
                    .append(Component.text(" Timer wurde gestartet!")));
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("A " + EssentialsUtil.ticksToString(timeInTicks) + " timer was started")
                    .withStyle(ChatFormatting.GRAY), false);
        }
        return 1;
    }

    private boolean isCanceled(CustomBossEvent customBossEvent){
        return isBossbarCanceled.getOrDefault(customBossEvent, false);
    }

    public static void removeRemainingBossbars(){
        isBossbarCanceled.forEach((customBossEvent, aBoolean) -> MinecraftServer.getServer().getCustomBossEvents().remove(customBossEvent));
    }
}
