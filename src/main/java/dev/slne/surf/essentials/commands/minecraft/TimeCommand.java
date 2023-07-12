package dev.slne.surf.essentials.commands.minecraft;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Collections;

public class TimeCommand extends EssentialsCommand {
    private boolean currentlySkipping = false;

    public TimeCommand() {
        super("time", "time <set|add|query> <value>", "Sets the time to a specific value");

        withPermission(Permissions.TIME_PERMISSION);

        executesNative((NativeResultingCommandExecutor) (sender, args) -> queryTime(
                sender,
                QueryTime.DAYTIME
        ));

        then(addQueryTimes());
        then(literal("add")
                .then(timeArgument("time")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> addTime(
                                sender,
                                args.getUnchecked("time")
                        ))
                )
        );
        then(literal("set")
                .then(timeArgument("time")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> setTime(
                                sender,
                                args.getUnchecked("time")
                        ))
                )
        );

        for (NamedTime namedTime : NamedTime.values()) {
            then(literal(namedTime.name().toLowerCase())
                    .executesNative((NativeResultingCommandExecutor) (sender, args) -> addNamedTime(
                            sender,
                            namedTime
                    ))
            );
        }
    }

    private Argument<?> addQueryTimes() {
        LiteralArgument literal = literal("query");
        for (QueryTime time : QueryTime.values()) {
            literal.then(literal(time.name().toLowerCase())
                    .executesNative((NativeResultingCommandExecutor) (sender, args) -> queryTime(
                            sender,
                            time
                    ))
            );
        }
        return literal;
    }

    private int queryTime(NativeProxyCommandSender source, QueryTime queryTime) {
        val world = source.getWorld();
        val time = queryTime.getTime(world);
        EssentialsUtil.sendSuccess(source.getCallee(), Component.text("Die ", Colors.INFO)
                .append(Component.text(queryTime.name, Colors.VARIABLE_KEY))
                .append(Component.text(" in der Welt ", Colors.INFO))
                .append(EssentialsUtil.getDisplayName(world))
                .append(Component.text(" beträgt ", Colors.INFO))
                .append(Component.text(time, Colors.VARIABLE_VALUE)
                        .hoverEvent(HoverEvent.showText(Component.text(EssentialsUtil.ticksToString(time), Colors.INFO))))
                .append(Component.text(" Ticks!", Colors.INFO)));

        return (int) time;
    }

    private int setTime(NativeProxyCommandSender source, int time) {
        val senderWorld = source.getWorld();
        val worlds = EssentialsUtil.timeCommandAffectsAllWorlds() ?
                source.getServer().getWorlds() : Collections.singletonList(senderWorld);

        if (currentlySkipping) {
            EssentialsUtil.sendError(source, Component.text("Es wird bereits Zeit übersprungen!", Colors.ERROR));
            return getDayTime(senderWorld);
        }

        for (World world : worlds) {
            world.setFullTime(time);
        }

        EssentialsUtil.sendSuccess(source.getCallee(), Component.text("Die Zeit wurde auf ", Colors.SUCCESS)
                .append(Component.text(time, Colors.VARIABLE_VALUE)
                        .hoverEvent(HoverEvent.showText(Component.text(EssentialsUtil.ticksToString(time), Colors.INFO))))
                .append(Component.text(" Ticks gesetzt!", Colors.SUCCESS)));

        return getDayTime(senderWorld);
    }

    private int addTime(NativeProxyCommandSender source, int time) {
        val senderWorld = source.getWorld();
        val worlds = EssentialsUtil.timeCommandAffectsAllWorlds() ?
                source.getServer().getWorlds() : Collections.singletonList(senderWorld);

        if (currentlySkipping) {
            EssentialsUtil.sendError(source.getCallee(), Component.text("Es wird bereits Zeit übersprungen!", Colors.ERROR));
            return getDayTime(senderWorld);
        }

        for (World world : worlds) {
            if (time > 24000) {
                world.setFullTime(world.getFullTime() + time);
            } else {
                smoothTimeSkip(world, time);
            }
        }

        EssentialsUtil.sendSuccess(source.getCallee(), Component.text("Es wurden ", Colors.SUCCESS)
                .append(Component.text(time, Colors.VARIABLE_VALUE)
                        .hoverEvent(HoverEvent.showText(Component.text(EssentialsUtil.ticksToString(time), Colors.INFO))))
                .append(Component.text(" Ticks zur Zeit hinzugefügt!", Colors.SUCCESS)));

        return getDayTime(senderWorld);
    }

    private int addNamedTime(NativeProxyCommandSender source, NamedTime namedTime) {
        val senderWorld = source.getWorld();
        val worlds = EssentialsUtil.timeCommandAffectsAllWorlds() ?
                source.getServer().getWorlds() : Collections.singletonList(senderWorld);

        if (currentlySkipping) {
            EssentialsUtil.sendError(source.getCallee(), Component.text("Es wird bereits Zeit übersprungen!", Colors.ERROR));
            return getDayTime(senderWorld);
        }

        for (World world : worlds) {
            long margin = (namedTime.time - world.getFullTime()) % 24_000L; // TODO test
            if (margin < 0L) {
                margin += 24000L;
            }
            smoothTimeSkip(world, margin);
        }

        EssentialsUtil.sendSuccess(source.getCallee(), Component.text("Die Zeit wurde auf ", Colors.SUCCESS)
                .append(Component.text(namedTime.name, Colors.VARIABLE_VALUE))
                .append(Component.text(" gesetzt!")));

        return getDayTime(senderWorld);
    }

    /**
     * Smoothly skips the given amount of time in the given world
     *
     * @param world     the world
     * @param timeToAdd the time to add
     */
    private void smoothTimeSkip(World world, long timeToAdd) {
        long goalTime = (world.getFullTime() + timeToAdd);
        Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), (bukkitTask) -> {
            long newTime = (world.getFullTime() + 100);

            if (newTime >= goalTime) {
                bukkitTask.cancel();
                currentlySkipping = false;
                world.setFullTime(goalTime);
                return;
            }

            currentlySkipping = true;
            world.setFullTime(newTime);
        }, 0, 1);
    }

    /**
     * Gets the day time of the given world
     *
     * @param world the world
     * @return the day time
     */
    private static int getDayTime(World world) {
        return (int) (world.getFullTime() % 24_000L);
    }

    /**
     * An enum that represents the different time queries.
     */
    @RequiredArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    private enum QueryTime {
        DAY("Zeit", world -> world.getFullTime() / 24_000L % Integer.MAX_VALUE),
        DAYTIME("Tageszeit", TimeCommand::getDayTime),
        GAME_TIME("gesamte Zeit", World::getGameTime);

        String name; // the name of the query
        World2Long timeFunction; // the function that returns the time

        /**
         * Gets the time of the given world
         *
         * @param world the world
         * @return the time
         */
        public long getTime(World world) {
            return timeFunction.apply(world);
        }
    }

    /**
     * An enum that represents the different named times.
     */
    @RequiredArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    private enum NamedTime {
        DAY("Tag", 500),
        NOON("Mittag", 6_000),
        NIGHT("Nacht", 13_000),
        MIDNIGHT("Mitternacht", 18_000);

        String name; // the name of the time
        int time; // the time
    }

    /**
     * A function that takes a world and returns a long
     */
    @FunctionalInterface
    private interface World2Long {

        /**
         * Applies this function to the given argument.
         *
         * @param world the function argument
         * @return the function result
         */
        long apply(World world);
    }
}
