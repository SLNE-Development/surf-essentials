package dev.slne.surf.essentials.commands.minecraft;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@PermissionTag(name = Permissions.FORCELOAD_PERMISSION, desc = "This is the permission for the 'forceload' command")
public class ForceloadCommand {
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("forceload", ForceloadCommand::literal).setUsage("/forceload <query | add | remove>")
                .setDescription("Query, add or remove force loaded chunks");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.FORCELOAD_PERMISSION));

        literal.then(Commands.literal("query")
                .executes(context -> list(context.getSource()))
                .then(Commands.argument("position", ColumnPosArgument.columnPos())
                        .executes(context -> query(context.getSource(), ColumnPosArgument.getColumnPos(context, "position")))));

        literal.then(Commands.literal("remove")
                        .then(Commands.argument("from", ColumnPosArgument.columnPos())
                                .executes(context -> changeForceload(context.getSource(), ColumnPosArgument.getColumnPos(context, "from"),
                                        ColumnPosArgument.getColumnPos(context, "from"), context.getSource().getLevel(), false))
                                .then(Commands.argument("to", ColumnPosArgument.columnPos())
                                        .executes(context -> changeForceload(context.getSource(), ColumnPosArgument.getColumnPos(context, "from"),
                                                ColumnPosArgument.getColumnPos(context, "to"), context.getSource().getLevel(), false))
                                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                                                .executes(context -> changeForceload(context.getSource(), ColumnPosArgument.getColumnPos(context, "from"),
                                                        ColumnPosArgument.getColumnPos(context, "to"), DimensionArgument.getDimension(context, "dimension"), false)))))
                .then(Commands.literal("all")
                        .executes(context -> removeAll(context.getSource(), context.getSource().getLevel()))
                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                                .executes(context -> removeAll(context.getSource(), DimensionArgument.getDimension(context, "dimension"))))));

        literal.then(Commands.literal("add")
                .then(Commands.argument("from", ColumnPosArgument.columnPos())
                        .executes(context -> changeForceload(context.getSource(), ColumnPosArgument.getColumnPos(context, "from"),
                                ColumnPosArgument.getColumnPos(context, "from"), context.getSource().getLevel(), true))
                        .then(Commands.argument("to", ColumnPosArgument.columnPos())
                                .executes(context -> changeForceload(context.getSource(), ColumnPosArgument.getColumnPos(context, "from"),
                                        ColumnPosArgument.getColumnPos(context, "to"), context.getSource().getLevel(), true))
                                .then(Commands.argument("dimension", DimensionArgument.dimension())
                                        .executes(context -> changeForceload(context.getSource(), ColumnPosArgument.getColumnPos(context, "from"),
                                                ColumnPosArgument.getColumnPos(context, "to"), DimensionArgument.getDimension(context, "dimension"), true))))));
    }

    private static int list(CommandSourceStack source)throws CommandSyntaxException{
        ServerLevel serverLevel = source.getLevel();
        ResourceKey<Level> resourceKey = serverLevel.dimension();
        LongSet longSet = serverLevel.getForcedChunks();
        int forcedChunks = longSet.size();

        if (source.isPlayer()){
            if (forcedChunks > 0){
                ComponentBuilder<TextComponent, TextComponent.Builder> forcedChunkListBuilder = Component.text();
                forcedChunkListBuilder.append(Component.text("Es ", SurfColors.INFO)
                        .append(Component.text((forcedChunks == 1) ? "wird " : "werden ", SurfColors.INFO))
                        .append(Component.text(forcedChunks, SurfColors.TERTIARY))
                        .append(Component.text((forcedChunks == 1) ? " Chunk" : " Chunks", SurfColors.INFO))
                        .append(Component.text(" in ", SurfColors.INFO))
                        .append(Component.text(resourceKey.location().toString(), SurfColors.TERTIARY))
                        .append(Component.text(" dauerhaft geladen: ", SurfColors.INFO)));

                for (Long aLong : longSet) {
                    int x = ChunkPos.getX(aLong);
                    int z = ChunkPos.getZ(aLong);
                    int y = serverLevel.getWorld().getHighestBlockYAt(x, z);

                    forcedChunkListBuilder.append(Component.text(new ChunkPos(aLong).toString(), SurfColors.SECONDARY)
                                    .hoverEvent(HoverEvent.showText(Component.text("Klicke zum teleportieren", SurfColors.INFO)))
                                    .clickEvent(ClickEvent.suggestCommand("/teleport %d %d %d".formatted(x, y, z)))
                            .append(Component.text(", ", SurfColors.INFO)));
                }

                EssentialsUtil.sendSuccess(source, forcedChunkListBuilder.build());
            }else {
                EssentialsUtil.sendSuccess(source, Component.text("Es werden keine Chunks in ", SurfColors.INFO)
                        .append(Component.text(resourceKey.location().toString(), SurfColors.TERTIARY))
                        .append(Component.text(" dauerhaft geladen!", SurfColors.INFO)));
            }
        }else {
            if (forcedChunks > 0) {
                List<String> strings = new ArrayList<>();
                for (Long l : longSet) {
                    ChunkPos pos = new ChunkPos(l);
                    strings.add(pos.toString());
                }
                Collections.sort(strings);
                String string = String.join(", ", strings);

                if (forcedChunks == 1) {
                    source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.forceload.list.single", resourceKey.location(), string), false);
                } else {
                    source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.forceload.list.multiple", forcedChunks, resourceKey.location(), string), false);
                }
            } else {
                source.sendFailure(net.minecraft.network.chat.Component.translatable("commands.forceload.added.none", resourceKey.location()));
            }
        }
        return forcedChunks;
    }

    private static int query(CommandSourceStack source, ColumnPos columnPos)throws CommandSyntaxException{
        ChunkPos chunkPosition = columnPos.toChunkPos();
        ServerLevel serverLevel = source.getLevel();
        ResourceKey<Level> resourceKey = serverLevel.dimension();
        boolean isForcedChunk = serverLevel.getForcedChunks().contains(chunkPosition.toLong());

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, Component.text("Der Chunk ", SurfColors.INFO)
                    .append(Component.text(chunkPosition.toString(), SurfColors.TERTIARY))
                    .append(Component.text(" in ", SurfColors.INFO))
                    .append(Component.text(resourceKey.location().toString(), SurfColors.TERTIARY))
                    .append(Component.text((isForcedChunk) ? " wird " : " wird nicht ", SurfColors.GOLD))
                    .append(Component.text("dauerhaft geladen!", SurfColors.INFO)));
        }else {
            if (isForcedChunk) {
                source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.forceload.query.success",
                        chunkPosition, resourceKey.location()), false);
            } else {
                throw ERROR_NOT_TICKING.create(chunkPosition, resourceKey.location());
            }
        }
        return 1;
    }

    private static int removeAll(CommandSourceStack source, ServerLevel serverLevel) throws CommandSyntaxException {
        ResourceKey<Level> resourceKey = serverLevel.dimension();
        LongSet longSet = serverLevel.getForcedChunks();
        int successfulRemoved = 0;

        for (Long aLong : longSet) {
            serverLevel.setChunkForced(ChunkPos.getX(aLong), ChunkPos.getZ(aLong), false);
            ++successfulRemoved;
        }

        if (successfulRemoved == 0) throw (source.isPlayer() ? ERROR_NO_FORCE_LOADED_CHUNKS_DE : ERROR_NO_FORCE_LOADED_CHUNKS).create(resourceKey.location());

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, Component.text("Es wird kein Chunk mehr dauerhaft in ", SurfColors.SUCCESS)
                    .append(Component.text(resourceKey.location().toString(), SurfColors.TERTIARY))
                    .append(Component.text(" geladen!", SurfColors.SUCCESS)));
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.forceload.removed.all", resourceKey.location()), false);
        }
        return 1;
    }

    private static int changeForceload(CommandSourceStack source, ColumnPos from, ColumnPos to, ServerLevel serverLevel, boolean forceLoaded) throws CommandSyntaxException {
        int minX = Math.min(from.x(), to.x());
        int minZ = Math.min(from.z(), to.z());
        int maxX = Math.max(from.x(), to.x());
        int maxZ = Math.max(from.z(), to.z());

        //check if position is in world
        if (!(minX >= -30000000 && minZ >= -30000000 && maxX < 30000000 && maxZ < 30000000)) throw BlockPosArgument.ERROR_OUT_OF_WORLD.create();

        int startX = SectionPos.blockToSectionCoord(minX);
        int startZ = SectionPos.blockToSectionCoord(minZ);
        int endX = SectionPos.blockToSectionCoord(maxX);
        int endY = SectionPos.blockToSectionCoord(maxZ);
        long totalSections = ((long) (endX - startX) + 1L) * ((long) (endY - startZ) + 1L);
        if (totalSections > MAX_CHUNK_LIMIT) throw ERROR_TOO_MANY_CHUNKS.create(MAX_CHUNK_LIMIT, totalSections);

        ResourceKey<Level> resourceKey = serverLevel.dimension();
        ChunkPos chunkPos = null;
        int numForced = 0;

        for (int x = startX; x <= endX; ++x) {
            for (int z = startZ; z <= endY; ++z) {
                boolean stateChanged = serverLevel.setChunkForced(x, z, forceLoaded);
                if (stateChanged) {
                    ++numForced;
                    if (chunkPos == null) {
                        chunkPos = new ChunkPos(x, z);
                    }
                }
            }
        }

        if (source.isPlayer()){
            if (numForced == 0) {
                throw (forceLoaded ? ERROR_ALL_ADDED_DE : ERROR_NONE_REMOVED_DE).create();
            } else {
                if (numForced == 1) {
                    EssentialsUtil.sendSuccess(source, Component.text("Der Chunk ", SurfColors.SUCCESS)
                            .append(Component.text(chunkPos.toString(), SurfColors.TERTIARY))
                            .append(Component.text(" in ", SurfColors.SUCCESS))
                            .append(Component.text(resourceKey.location().toString(), SurfColors.TERTIARY))
                            .append(Component.text(" wird nun ", SurfColors.SUCCESS)
                                    .append(Component.text((forceLoaded) ? "dauerhaft geladen!" : "nicht mehr dauerhaft geladen!", SurfColors.SUCCESS))));
                } else {
                    ChunkPos chunkPos2 = new ChunkPos(startX, startZ);
                    ChunkPos chunkPos3 = new ChunkPos(endX, endY);
                    EssentialsUtil.sendSuccess(source, Component.text(numForced, SurfColors.GREEN)
                            .append(Component.text(" Chunks werden von ", SurfColors.SUCCESS))
                            .append(Component.text(chunkPos2.toString(), SurfColors.TERTIARY))
                            .append(Component.text(" bis zu ", SurfColors.SUCCESS))
                            .append(Component.text(chunkPos3.toString(), SurfColors.TERTIARY))
                            .append(Component.text(" in ", SurfColors.SUCCESS))
                            .append(Component.text(resourceKey.location().toString(), SurfColors.TERTIARY))
                            .append(Component.text(forceLoaded ? " dauerhaft" : " nicht mehr dauerhaft", SurfColors.SUCCESS))
                            .append(Component.text(" geladen!", SurfColors.SUCCESS)));
                }
            }
        }else {
            if (numForced == 0) {
                throw (forceLoaded ? ERROR_ALL_ADDED : ERROR_NONE_REMOVED).create();
            } else {
                if (numForced == 1) {
                    source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.forceload." +
                            (forceLoaded ? "added" : "removed") + ".single", chunkPos, resourceKey.location()), true);
                } else {
                    ChunkPos chunkPos2 = new ChunkPos(startX, startZ);
                    ChunkPos chunkPos3 = new ChunkPos(endX, endY);
                    source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.forceload." +
                            (forceLoaded ? "added" : "removed") + ".multiple", numForced, resourceKey.location(), chunkPos2, chunkPos3), true);
                }
            }
        }
        return numForced;
    }

    private static final int MAX_CHUNK_LIMIT = 256;
    private static final Dynamic2CommandExceptionType ERROR_TOO_MANY_CHUNKS = new Dynamic2CommandExceptionType((maxCount, count) ->
            net.minecraft.network.chat.Component.translatable("commands.forceload.toobig", maxCount, count));

    private static final SimpleCommandExceptionType ERROR_ALL_ADDED = new SimpleCommandExceptionType(
            net.minecraft.network.chat.Component.translatable("commands.forceload.added.failure"));

    private static final SimpleCommandExceptionType ERROR_NONE_REMOVED = new SimpleCommandExceptionType(
            net.minecraft.network.chat.Component.translatable("commands.forceload.removed.failure"));

    private static final Dynamic2CommandExceptionType ERROR_NOT_TICKING = new Dynamic2CommandExceptionType((chunkPos, registryKey) ->
            net.minecraft.network.chat.Component.translatable("commands.forceload.query.failure", chunkPos, registryKey));

    private static final SimpleCommandExceptionType ERROR_ALL_ADDED_DE = new SimpleCommandExceptionType(PaperAdventure.asVanilla(SurfApi.getPrefix()
            .append(Component.text("Die Chunks werden bereits dauerhaft geladen!", SurfColors.ERROR))));

    private static final SimpleCommandExceptionType ERROR_NONE_REMOVED_DE = new SimpleCommandExceptionType(PaperAdventure.asVanilla(SurfApi.getPrefix()
            .append(Component.text("Die Chunks wurden auch vorher nicht dauerhaft geladen!", SurfColors.ERROR))));

    private static final DynamicCommandExceptionType ERROR_NO_FORCE_LOADED_CHUNKS = new DynamicCommandExceptionType(dimension ->
            net.minecraft.network.chat.Component.literal("There are no forceloaded chunks in ")
            .append(net.minecraft.network.chat.Component.literal(dimension.toString())));

    private static final DynamicCommandExceptionType ERROR_NO_FORCE_LOADED_CHUNKS_DE = new DynamicCommandExceptionType(dimension ->
            net.minecraft.network.chat.Component.literal("Es gibt keine dauerhaft geladenen Chunks in ")
                    .append(net.minecraft.network.chat.Component.literal(dimension.toString())));

}
