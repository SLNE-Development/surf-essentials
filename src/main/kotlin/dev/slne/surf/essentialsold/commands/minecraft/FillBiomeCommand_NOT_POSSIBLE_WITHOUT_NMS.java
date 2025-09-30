package dev.slne.surf.essentialsold.commands.minecraft;

/**
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.function.Predicate;

public class FillBiomeCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"fillBiome"};
    }

    @Override
    public String usage() {
        return "/fillBiome <from> <to> [<replace <filter>>]";
    }

    @Override
    public String description() {
        return "Change the biom from blocks";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.FILL_BIOME_PERMISSION));

        literal.then(Commands.argument("from", BlockPosArgument.blockPos())
                .then(Commands.argument("to", BlockPosArgument.blockPos())
                        .then(Commands.argument("biome", ResourceArgument.resource(this.commandBuildContext, Registries.BIOME))
                                .executes(context -> fillBiome(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "from"),
                                        BlockPosArgument.getLoadedBlockPos(context, "to"),
                                        ResourceArgument.getResource(context, "biome", Registries.BIOME),
                                        biomeHolder -> true
                                ))

                                .then(Commands.literal("replace")
                                        .then(Commands.argument("filter", ResourceOrTagArgument.resourceOrTag(this.commandBuildContext, Registries.BIOME))
                                                .executes(context -> {
                                                    final var filter = ResourceOrTagArgument.getResourceOrTag(context, "filter", Registries.BIOME);
                                                    return fillBiome(
                                                            context.getSource(),
                                                            BlockPosArgument.getLoadedBlockPos(context, "from"),
                                                            BlockPosArgument.getLoadedBlockPos(context, "to"),
                                                            ResourceArgument.getResource(context, "biome", Registries.BIOME),
                                                            filter
                                                    );
                                                }))))));
    }

    private int fillBiome(CommandSourceStack source, BlockPos fromPos, BlockPos toPos, Holder.Reference<Biome> biome,
                          Predicate<Holder<Biome>> filter) throws CommandSyntaxException {
        final var from = quantize(fromPos);
        final var to = quantize(toPos);
        final var boundingBox = BoundingBox.fromCorners(from, to);
        final var blocksToModify = boundingBox.getXSpan() * boundingBox.getYSpan() * boundingBox.getZSpan();
        final var blockLimit = EssentialsUtil.modificationBlockLimit(source);

        if (blocksToModify > blockLimit) throw ERROR_VOLUME_TOO_LARGE.create(blockLimit, blocksToModify);

        final var level = source.getLevel();
        final var chunks = new ArrayList<ChunkAccess>();
        final int minZ = SectionPos.blockToSectionCoord(boundingBox.minZ());
        final int maxZ = SectionPos.blockToSectionCoord(boundingBox.maxZ());
        final int minX = SectionPos.blockToSectionCoord(boundingBox.minX());
        final int maxX = SectionPos.blockToSectionCoord(boundingBox.maxX());

        for (int z = minZ; z <= maxZ; z++) {
            for (int x = minX; x <= maxX; x++) {
                ChunkAccess chunkAccess = level.getChunk(x, z, ChunkStatus.FULL, false);

                if (chunkAccess == null) throw ERROR_NOT_LOADED.create();

                chunks.add(chunkAccess);
            }
        }

        MutableInt counter = new MutableInt(0);

        for (ChunkAccess chunk : chunks) {
            chunk.fillBiomesFromNoise((x, y, z, noise) -> {
                final int blockX = QuartPos.toBlock(x);
                final int blockY = QuartPos.toBlock(y);
                final int blockZ = QuartPos.toBlock(z);
                final var noiseBiome = chunk.getNoiseBiome(x, y, z);

                if (boundingBox.isInside(blockX, blockY, blockZ) && filter.test(noiseBiome)) {
                    counter.increment();
                    return biome;

                } else {
                    return noiseBiome;
                }
            }, level.getChunkSource().randomState().sampler());
            chunk.setUnsaved(true);
        }

        level.getChunkSource().chunkMap.resendBiomesForChunks(chunks);

        final boolean singleChange = counter.getValue() == 1;

        EssentialsUtil.sendSourceSuccess(source, Component.text("Es %s ".formatted(singleChange ? "wurde" : "wurden"), Colors.SUCCESS)
                .append(Component.text(counter.getValue(), Colors.VARIABLE_VALUE))
                .append(Component.text(" Biom-%s zwischen ".formatted(singleChange ? "Eintrag" : "Einträge"), Colors.SUCCESS))
                .append(EssentialsUtil.formatLocation(Colors.SUCCESS, boundingBox))
                .append(Component.text(" geändert.", Colors.SUCCESS)));
        return counter.getValue();
    }

    private int quantize(int coordinate) {
        return QuartPos.toBlock(QuartPos.fromBlock(coordinate));
    }

    private BlockPos quantize(BlockPos pos) {
        return new BlockPos(quantize(pos.getX()), quantize(pos.getY()), quantize(pos.getZ()));
    }

    public static final SimpleCommandExceptionType ERROR_NOT_LOADED = new SimpleCommandExceptionType(PaperAdventure.asVanilla(
            Component.translatable("argument.pos.unloaded")));
    private static final Dynamic2CommandExceptionType ERROR_VOLUME_TOO_LARGE = new Dynamic2CommandExceptionType((maximum, specified) ->
            PaperAdventure.asVanilla(Component.translatable("commands.fillbiome.toobig",
                    Component.text((int) maximum, Colors.VARIABLE_VALUE),
                    Component.text(((int) specified), Colors.VARIABLE_VALUE))));
}

 */
