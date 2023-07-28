package dev.slne.surf.essentials.commands.minecraft;

import dev.jorel.commandapi.AbstractArgumentTree;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.jorel.commandapi.wrappers.Location2D;
import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.copy.Mth;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class ForceloadCommand extends EssentialsCommand {
    private static final int MAX_CHUNK_LIMIT = 256;

    public ForceloadCommand() {
        super("forceload", "forceload <query | remove>", "Manage forceloaded Chunks");

        withPermission(Permissions.FORCELOAD_PERMISSION);

        then(literal("query")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> list(sender))
                .then(location2DArgument("position", LocationType.BLOCK_POSITION)
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> query(sender.getCallee(), Objects.requireNonNull(args.getUnchecked("position"))))));

        then(literal("remove")
                .then(buildForceload(false))
                .then(literal("all")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> removeAll(sender.getCallee(), sender.getWorld()))
                        .then(worldArgument("dimension")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> removeAll(sender.getCallee(), Objects.requireNonNull(args.getUnchecked("dimension")))))));

        then(literal("add")
                .then(buildForceload(true)));
    }

    private AbstractArgumentTree<?, Argument<?>, CommandSender> buildForceload(boolean forceload) {
        return location2DArgument("from", LocationType.BLOCK_POSITION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> changeForceload(
                        sender.getCallee(),
                        Objects.requireNonNull(args.getUnchecked("from")),
                        Objects.requireNonNull(args.getUnchecked("from")),
                        sender.getWorld(),
                        forceload
                ))
                .then(location2DArgument("to", LocationType.BLOCK_POSITION)
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> changeForceload(
                                sender.getCallee(),
                                Objects.requireNonNull(args.getUnchecked("from")),
                                Objects.requireNonNull(args.getUnchecked("to")),
                                sender.getWorld(),
                                forceload
                        ))
                        .then(worldArgument("dimension")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> changeForceload(
                                        sender.getCallee(),
                                        Objects.requireNonNull(args.getUnchecked("from")),
                                        Objects.requireNonNull(args.getUnchecked("from")),
                                        args.getUnchecked("dimension"),
                                        forceload
                                ))
                        )
                );
    }

    private int list(NativeProxyCommandSender source) {
        val world = source.getWorld();
        val forceLoadedChunks = world.getForceLoadedChunks();
        val forcedChunks = forceLoadedChunks.size();

        if (forcedChunks > 0) {
            EssentialsUtil.sendSuccess(source, Component.text("Es ", Colors.INFO)
                    .append(Component.text((forcedChunks == 1) ? "wird " : "werden ", Colors.INFO))
                    .append(Component.text(forcedChunks, Colors.TERTIARY))
                    .append(Component.text((forcedChunks == 1) ? " Chunk" : " Chunks", Colors.INFO))
                    .append(Component.text(" in ", Colors.INFO))
                    .append(EssentialsUtil.getDisplayName(world))
                    .append(Component.text(" dauerhaft geladen: ", Colors.INFO))
                    .append(Component.join(JoinConfiguration.commas(true), forceLoadedChunks.stream()
                            .map(chunk -> Component.text("[", Colors.SPACER)
                                    .append(Component.text(chunk.getX(), Colors.VARIABLE_VALUE))
                                    .append(Component.text(", ", Colors.INFO))
                                    .append(Component.text(chunk.getZ(), Colors.VARIABLE_VALUE))
                                    .append(Component.text("]", Colors.SPACER))
                                    .hoverEvent(HoverEvent.showText(Component.text("Klicke zum teleportieren", Colors.INFO)))
                                    .clickEvent(ClickEvent.suggestCommand("/teleport %d %d %d".formatted(chunk.getX() << 4, world.getHighestBlockAt(chunk.getX() << 4, chunk.getZ() << 4).getY(), chunk.getZ() << 4))))
                            .toList())));
        } else {
            EssentialsUtil.sendSuccess(source, Component.text("Es werden keine Chunks in ", Colors.INFO)
                    .append(EssentialsUtil.getDisplayName(world))
                    .append(Component.text(" dauerhaft geladen!", Colors.INFO)));
        }
        return forcedChunks;
    }

    private int query(CommandSender source, Location2D location2D) {
        val world = location2D.getWorld();
        val isForcedChunk = location2D.getWorld().isChunkForceLoaded(location2D.getBlockX(), location2D.getBlockZ());

        EssentialsUtil.sendSuccess(source, Component.text("Der Chunk ", Colors.INFO)
                .append(Component.text("%s %s".formatted(location2D.getBlockX() >> 4, location2D.getBlockZ() >> 4), Colors.VARIABLE_VALUE))
                .append(Component.text(" in ", Colors.INFO))
                .append(EssentialsUtil.getDisplayName(world))
                .append(Component.text((isForcedChunk) ? " wird " : " wird nicht ", Colors.INFO))
                .append(Component.text("dauerhaft geladen!", Colors.INFO)));

        return 1;
    }

    private int removeAll(CommandSender source, World world) throws WrapperCommandSyntaxException {
        int successfulRemoved = 0;

        for (Chunk forceLoadedChunk : world.getForceLoadedChunks()) {
            forceLoadedChunk.setForceLoaded(false);
            successfulRemoved++;
        }

        if (successfulRemoved == 0) throw Exceptions.ERROR_NO_FORCE_LOADED_CHUNKS;

        EssentialsUtil.sendSuccess(source, Component.text("Es wird kein Chunk mehr dauerhaft in ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(world))
                .append(Component.text(" geladen!", Colors.SUCCESS)));

        return 1;
    }

    private int changeForceload(CommandSender source, Location2D from, Location2D to, World world, boolean forceLoaded) throws WrapperCommandSyntaxException {
        // Get the chunk positions
        val minX = Math.min(from.x(), to.x());
        val minZ = Math.min(from.z(), to.z());
        val maxX = Math.max(from.x(), to.x());
        val maxZ = Math.max(from.z(), to.z());

        // check if position is in world
        if (!(minX >= -30000000 && minZ >= -30000000 && maxX < 30000000 && maxZ < 30000000))
            throw Exceptions.ERROR_OUT_OF_WORLD;

        // Get the start and end chunk positions
        val startX = Mth.floor(minX);
        val startZ = Mth.floor(minZ);
        val endX = Mth.floor(maxX);
        val endZ = Mth.floor(maxZ);
        val totalSections = ((long) (endX - startX) + 1L) * ((long) (endZ - startZ) + 1L);

        // Check if the amount of chunks is too high
        if (totalSections > MAX_CHUNK_LIMIT)
            throw Exceptions.ERROR_TOO_MANY_CHUNKS.create(MAX_CHUNK_LIMIT, totalSections);

        int numForced = 0, currentX = 0, currentZ = 0;

        // Loop through all chunks and force or unforce load them
        for (int x = startX; x <= endX; ++x) {
            for (int z = startZ; z <= endZ; ++z) {
                if (world.isChunkForceLoaded(x, z)) {
                    continue;
                }
                world.setChunkForceLoaded(x, z, forceLoaded);
                numForced++;
                currentX = x >> 4;
                currentZ = z >> 4;
            }
        }

        if (numForced == 1) {
            EssentialsUtil.sendSuccess(source, Component.text("Der Chunk ", Colors.SUCCESS)
                    .append(Component.text("[", Colors.GRAY))
                    .append(Component.text("%s %s".formatted(currentX, currentZ), Colors.VARIABLE_VALUE))
                    .append(Component.text("]", Colors.GRAY))
                    .append(Component.text(" in ", Colors.SUCCESS))
                    .append(EssentialsUtil.getDisplayName(world))
                    .append(Component.text(" wird nun ", Colors.SUCCESS)
                            .append(Component.text((forceLoaded) ? "dauerhaft geladen!" : "nicht mehr dauerhaft geladen!", Colors.SUCCESS))));
        } else {
            EssentialsUtil.sendSuccess(source, Component.text(numForced, Colors.GREEN)
                    .append(Component.text(" Chunks werden nun von ", Colors.SUCCESS))
                    .append(Component.text("%s %s".formatted(startX >> 4, startZ >> 4), Colors.TERTIARY))
                    .append(Component.text(" bis zu ", Colors.SUCCESS))
                    .append(Component.text("%s %s".formatted(endX >> 4, endZ >> 4), Colors.TERTIARY))
                    .append(Component.text(" in ", Colors.SUCCESS))
                    .append(EssentialsUtil.getDisplayName(world))
                    .append(Component.text(forceLoaded ? " dauerhaft" : " nicht mehr dauerhaft", Colors.SUCCESS))
                    .append(Component.text(" geladen!", Colors.SUCCESS)));
        }

        return numForced;
    }
}
