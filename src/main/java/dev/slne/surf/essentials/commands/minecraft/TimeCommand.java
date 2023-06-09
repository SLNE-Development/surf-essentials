package dev.slne.surf.essentials.commands.minecraft;

import com.google.common.collect.Iterators;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.exceptions.InvalidStringTimeException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.configuration.GlobalConfiguration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.event.world.TimeSkipEvent;

import java.util.Iterator;

public class TimeCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"time"};
    }

    @Override
    public String usage() {
        return "/time <query | add | set | day | noon | night | midnight>";
    }

    @Override
    public String description() {
        return "Change game time";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.TIME_PERMISSION));

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

        ResourceKey<Level> resourceKey = serverLevel.dimension();
        String timeName;
        switch (whatTime) {
            case 0 -> timeName = "Zeit";
            case 1 -> timeName = "Tageszeit";
            case 2 -> timeName = "gesamte Zeit";
            default -> timeName = "undefined";
        }
        EssentialsUtil.sendSuccess(source, Component.text("Die ", Colors.INFO)
                .append(Component.text(timeName, Colors.TERTIARY))
                .append(Component.text(" in der Welt ", Colors.INFO))
                .append(Component.text(resourceKey.location().toString().replace("minecraft:", ""), Colors.TERTIARY))
                .append(Component.text(" beträgt ", Colors.INFO))
                .append(Component.text(time, Colors.TERTIARY)
                        .hoverEvent(HoverEvent.showText(Component.text(EssentialsUtil.ticksToString(time), Colors.INFO))))
                .append(Component.text(" Ticks!", Colors.INFO)));

        return time;
    }

    private static int setTime(CommandSourceStack source, int time) throws CommandSyntaxException {
        Iterator<ServerLevel> iterator = GlobalConfiguration.get().commands.timeCommandAffectsAllWorlds ?
                source.getServer().getAllLevels().iterator() : Iterators.singletonIterator(source.getLevel());

        iterator.forEachRemaining(level -> {
            TimeSkipEvent timeSkipEvent = new TimeSkipEvent(level.getWorld(), TimeSkipEvent.SkipReason.COMMAND, time - level.getDayTime());
            EssentialsUtil.callEvent(timeSkipEvent);
            if (!timeSkipEvent.isCancelled()) level.setDayTime(level.getDayTime() + timeSkipEvent.getSkipAmount());
        });


        EssentialsUtil.sendSuccess(source, Component.text("Die Zeit wurde auf ", Colors.SUCCESS)
                .append(Component.text(time, Colors.TERTIARY)
                        .hoverEvent(HoverEvent.showText(Component.text(EssentialsUtil.ticksToString(time), Colors.INFO))))
                .append(Component.text(" Ticks gesetzt!", Colors.SUCCESS)));

        return getDayTime(source.getLevel());
    }

    private static int addTime(CommandSourceStack source, int time) throws CommandSyntaxException {
        Iterator<ServerLevel> iterator = GlobalConfiguration.get().commands.timeCommandAffectsAllWorlds ?
                source.getServer().getAllLevels().iterator() : Iterators.singletonIterator(source.getLevel());

        iterator.forEachRemaining(level -> {
            TimeSkipEvent timeSkipEvent = new TimeSkipEvent(level.getWorld(), TimeSkipEvent.SkipReason.COMMAND, time);
            EssentialsUtil.callEvent(timeSkipEvent);
            if (!timeSkipEvent.isCancelled()) level.setDayTime(level.getDayTime() + timeSkipEvent.getSkipAmount());
        });


        EssentialsUtil.sendSuccess(source, Component.text("Es wurden ", Colors.SUCCESS)
                .append(Component.text(time, Colors.TERTIARY)
                        .hoverEvent(HoverEvent.showText(Component.text(EssentialsUtil.ticksToString(time), Colors.INFO))))
                .append(Component.text(" Ticks zur Zeit hinzugefügt!", Colors.SUCCESS)));

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
            long timeDifference = (addTime - level.getDayTime()) % 24000;
            if (timeDifference < 0) timeDifference += 24000;
            long time = level.getDayTime() + timeDifference;

            TimeSkipEvent event = new TimeSkipEvent(level.getWorld(), TimeSkipEvent.SkipReason.CUSTOM, time - level.getDayTime());
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            level.setDayTime(level.getDayTime() + event.getSkipAmount());

            level.getPlayers(player -> {
                player.connection.send(new ClientboundSetTimePacket(player.level.getGameTime(), player.getPlayerTime(), player.level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
                return true;
            });
        });

        EssentialsUtil.sendSuccess(source, Component.text("Die Zeit wurde auf ", Colors.SUCCESS)
                .append(Component.text(namedTime, Colors.TERTIARY))
                .append(Component.text(" gesetzt!")));

        return 0;
    }

    private static int getDayTime(ServerLevel world) {
        return (int) (world.getDayTime() % 24000L);
    }
}
