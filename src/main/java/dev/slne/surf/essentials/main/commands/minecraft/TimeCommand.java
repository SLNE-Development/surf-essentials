package dev.slne.surf.essentials.main.commands.minecraft;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.google.common.collect.Iterators;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.exceptions.InvalidStringTimeException;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import dev.slne.surf.essentials.main.utils.Permissions;
import io.papermc.paper.configuration.GlobalConfiguration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.event.world.TimeSkipEvent;

import java.util.Iterator;

@PermissionTag(name = Permissions.TIME_PERMISSION, desc = "This is the permission for the 'time' command")
public class TimeCommand{

    public static void register() {
        SurfEssentials.registerPluginBrigadierCommand("time", TimeCommand::literal).setUsage("/time [<set | add]")
                .setDescription("Get set or add time in time in the current world");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.TIME_PERMISSION));

        literal.executes(context -> queryTime(context.getSource(), context.getSource().getLevel(), getDayTime(context.getSource().getLevel()), 1));

        literal.then(Commands.literal("query")
                .then(Commands.literal("day")
                        .executes(context -> queryTime(context.getSource(), context.getSource().getLevel(), (int) (context.getSource().getLevel().getDayTime() / 24000L % 2147483647L), 0)))
                .then(Commands.literal("daytime")
                        .executes(context -> queryTime(context.getSource(), context.getSource().getLevel(), getDayTime(context.getSource().getLevel()), 1)))
                .then(Commands.literal("gametime")
                        .executes(context -> queryTime(context.getSource(), context.getSource().getLevel(), (int) context.getSource().getLevel().getGameTime(), 2))));


        literal.then(Commands.literal("add")
                .then(Commands.argument("time", TimeArgument.time())
                        .executes(context -> addTime(context.getSource(), IntegerArgumentType.getInteger(context, "time")))));

        literal.then(Commands.literal("set")
                .then(Commands.argument("time", TimeArgument.time())
                        .executes(context -> setTime(context.getSource(), IntegerArgumentType.getInteger(context, "time")))));

        literal.then(Commands.literal("day")
                .executes(context -> addNamedTime(context.getSource(), "Tag")));

        literal.then(Commands.literal("noon")
                .executes(context -> addNamedTime(context.getSource(), "Mittag")));

        literal.then(Commands.literal("night")
                .executes(context -> addNamedTime(context.getSource(), "Nacht")));

        literal.then(Commands.literal("midnight")
                .executes(context -> addNamedTime(context.getSource(), "Mitternacht")));
    }

    private static int queryTime(CommandSourceStack source, ServerLevel serverLevel, int time, int whatTime) throws CommandSyntaxException {
        if (source.isPlayer()) {
            ResourceKey<Level> resourceKey = serverLevel.dimension();
            String timeName;
            switch (whatTime) {
                case 0 -> timeName = "Zeit";
                case 1 -> timeName = "Tageszeit";
                case 2 -> timeName = "gesamte Zeit";
                default -> timeName = "undefined";
            }
            EssentialsUtil.sendSuccess(source, Component.text("Die ", SurfColors.INFO)
                    .append(Component.text(timeName, SurfColors.TERTIARY))
                    .append(Component.text(" in der Welt ", SurfColors.INFO))
                    .append(Component.text(resourceKey.location().toString().replace("minecraft:", ""), SurfColors.TERTIARY))
                    .append(Component.text(" beträgt ", SurfColors.INFO))
                    .append(Component.text(time, SurfColors.TERTIARY)
                            .hoverEvent(HoverEvent.showText(Component.text(EssentialsUtil.ticksToString(time), SurfColors.INFO))))
                    .append(Component.text(" Ticks!", SurfColors.INFO)));
        } else {
            source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.time.query", time), false);
        }
        return time;
    }

    private static int setTime(CommandSourceStack source, int time) throws CommandSyntaxException {
        Iterator<ServerLevel> iterator = GlobalConfiguration.get().commands.timeCommandAffectsAllWorlds ?
                source.getServer().getAllLevels().iterator() : Iterators.singletonIterator(source.getLevel());

        iterator.forEachRemaining(level -> {
            TimeSkipEvent timeSkipEvent = new TimeSkipEvent(level.getWorld(), TimeSkipEvent.SkipReason.COMMAND, time - level.getDayTime());
            SurfApi.callEvent(timeSkipEvent);
            if (!timeSkipEvent.isCancelled()) level.setDayTime(level.getDayTime() + timeSkipEvent.getSkipAmount());
        });

        if (source.isPlayer()) {
            EssentialsUtil.sendSuccess(source, Component.text("Die Zeit wurde auf ", SurfColors.SUCCESS)
                    .append(Component.text(time, SurfColors.TERTIARY)
                            .hoverEvent(HoverEvent.showText(Component.text(EssentialsUtil.ticksToString(time), SurfColors.INFO))))
                    .append(Component.text(" Ticks gesetzt!", SurfColors.SUCCESS)));
        } else {
            source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.time.set", time), false);
        }
        return getDayTime(source.getLevel());
    }

    private static int addTime(CommandSourceStack source, int time) throws CommandSyntaxException {
        Iterator<ServerLevel> iterator = GlobalConfiguration.get().commands.timeCommandAffectsAllWorlds ?
                source.getServer().getAllLevels().iterator() : Iterators.singletonIterator(source.getLevel());

        iterator.forEachRemaining(level -> {
            TimeSkipEvent timeSkipEvent = new TimeSkipEvent(level.getWorld(), TimeSkipEvent.SkipReason.COMMAND, time);
            SurfApi.callEvent(timeSkipEvent);
            if (!timeSkipEvent.isCancelled()) level.setDayTime(level.getDayTime() + timeSkipEvent.getSkipAmount());
        });

        if (source.isPlayer()) {
            EssentialsUtil.sendSuccess(source, Component.text("Es wurden ", SurfColors.SUCCESS)
                    .append(Component.text(time, SurfColors.TERTIARY)
                            .hoverEvent(HoverEvent.showText(Component.text(EssentialsUtil.ticksToString(time), SurfColors.INFO))))
                    .append(Component.text(" Ticks zur Zeit hinzugefügt!", SurfColors.SUCCESS)));
        } else {
            source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.time.set", getDayTime(source.getLevel())), false);
        }
        return getDayTime(source.getLevel());
    }

    private static int addNamedTime(CommandSourceStack source, String namedTime) throws CommandSyntaxException {
        int addTime = switch (namedTime) {
            case "Tag" -> 500;
            case "Mittag" -> 6000;
            case "Nacht" -> 13000;
            case "Mitternacht" -> 18000;
            default -> throw new InvalidStringTimeException("Invalid time: \"" + namedTime + "\"");
        };
        Iterator<ServerLevel> iterator = GlobalConfiguration.get().commands.timeCommandAffectsAllWorlds ?
                source.getServer().getAllLevels().iterator() : Iterators.singletonIterator(source.getLevel());

        iterator.forEachRemaining(level -> {
            int dayDuration = 24000;
            int currentTime = (int) level.getGameTime();
            int newTime = currentTime + (dayDuration - currentTime) + addTime;
            int timeSkipped = (newTime + dayDuration - currentTime) % dayDuration;

            TimeSkipEvent timeSkipEvent = new TimeSkipEvent(level.getWorld(), TimeSkipEvent.SkipReason.COMMAND, timeSkipped);
            Bukkit.getPluginManager().callEvent(timeSkipEvent);
            if (!timeSkipEvent.isCancelled()) level.setDayTime(newTime);
        });

        if (source.isPlayer()) {
            EssentialsUtil.sendSuccess(source, Component.text("Die Zeit wurde auf ", SurfColors.SUCCESS)
                    .append(Component.text(namedTime, SurfColors.TERTIARY))
                    .append(Component.text(" gesetzt!")));
        } else {
            source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.time.set", getDayTime(source.getLevel())), false);
        }
        return 0;
    }

    private static int getDayTime(ServerLevel world) {
        return (int) (world.getDayTime() % 24000L);
    }
}
