package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.blocks.BlockStatePos;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class FillCommand extends BrigadierCommand {
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((maxCount, count) ->
            Component.translatable("commands.fill.toobig", maxCount, count));
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.fill.failed"));
    public static final SimpleCommandExceptionType ERROR_NOTHING_TO_UNDO = new SimpleCommandExceptionType(Component.literal("Nothing to undo")
            .withStyle(ChatFormatting.RED));
    private static final BlockInput HOLLOW_CORE = new BlockInput(Blocks.AIR.defaultBlockState(), Collections.emptySet(), null);

    private static final Map<UUID, List<BlockStatePos>> BLOCKS = new HashMap<>();

    @Override
    public String[] names() {
        return new String[]{"fill"};
    }

    @Override
    public String usage() {
        return "/fill <undo | fromLocation <toLocation <material> [<destroy|hollow|keep|outline|replace|filter>]>>";
    }

    @Override
    public String description() {
        return "Fills an area with blocks or undoes it";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.FILL_PERMISSION));

        literal.then(Commands.literal("undo")
                .executes(context -> undo(context.getSource())));

        literal.then(Commands.argument("fromLocation", BlockPosArgument.blockPos())
                .then(Commands.argument("toLocation", BlockPosArgument.blockPos())
                        .then(Commands.argument("material", BlockStateArgument.block(EssentialsUtil.buildContext()))
                                .executes(context -> fill(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "fromLocation"),
                                        BlockPosArgument.getLoadedBlockPos(context, "toLocation"), BlockStateArgument.getBlock(context, "material"),
                                        Mode.REPLACE, null))

                                .then(Commands.literal("replace")
                                        .executes(context -> fill(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "fromLocation"),
                                                BlockPosArgument.getLoadedBlockPos(context, "toLocation"), BlockStateArgument.getBlock(context, "material"),
                                                Mode.REPLACE, null))
                                        .then(Commands.argument("filter", BlockPredicateArgument.blockPredicate(EssentialsUtil.buildContext()))
                                                .executes(context -> fill(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "fromLocation"),
                                                        BlockPosArgument.getLoadedBlockPos(context, "toLocation"), BlockStateArgument.getBlock(context, "material"),
                                                        Mode.REPLACE, BlockPredicateArgument.getBlockPredicate(context, "filter")))))

                                .then(Commands.literal("outline")
                                        .executes(context -> fill(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "fromLocation"),
                                                BlockPosArgument.getLoadedBlockPos(context, "toLocation"), BlockStateArgument.getBlock(context, "material"),
                                                Mode.OUTLINE, null))
                                        .then(Commands.argument("filter", BlockPredicateArgument.blockPredicate(EssentialsUtil.buildContext()))
                                                .executes(context -> fill(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "fromLocation"),
                                                        BlockPosArgument.getLoadedBlockPos(context, "toLocation"), BlockStateArgument.getBlock(context, "material"),
                                                        Mode.OUTLINE, BlockPredicateArgument.getBlockPredicate(context, "filter")))))

                                .then(Commands.literal("hollow")
                                        .executes(context -> fill(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "fromLocation"),
                                                BlockPosArgument.getLoadedBlockPos(context, "toLocation"), BlockStateArgument.getBlock(context, "material"),
                                                Mode.HOLLOW, null))
                                        .then(Commands.argument("filter", BlockPredicateArgument.blockPredicate(EssentialsUtil.buildContext()))
                                                .executes(context -> fill(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "fromLocation"),
                                                        BlockPosArgument.getLoadedBlockPos(context, "toLocation"), BlockStateArgument.getBlock(context, "material"),
                                                        Mode.HOLLOW, BlockPredicateArgument.getBlockPredicate(context, "filter")))))

                                .then(Commands.literal("destroy")
                                        .executes(context -> fill(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "fromLocation"),
                                                BlockPosArgument.getLoadedBlockPos(context, "toLocation"), BlockStateArgument.getBlock(context, "material"),
                                                Mode.DESTROY, null))
                                        .then(Commands.argument("filter", BlockPredicateArgument.blockPredicate(EssentialsUtil.buildContext()))
                                                .executes(context -> fill(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "fromLocation"),
                                                        BlockPosArgument.getLoadedBlockPos(context, "toLocation"), BlockStateArgument.getBlock(context, "material"),
                                                        Mode.DESTROY, BlockPredicateArgument.getBlockPredicate(context, "filter")))))

                                .then(Commands.literal("keep")
                                        .executes(context -> fill(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "fromLocation"),
                                                BlockPosArgument.getLoadedBlockPos(context, "toLocation"), BlockStateArgument.getBlock(context, "material"),
                                                Mode.REPLACE, blockInWorld -> blockInWorld.getLevel().isEmptyBlock(blockInWorld.getPos()))))

                                .then(Commands.literal("filter")
                                        .then(Commands.argument("filter", BlockPredicateArgument.blockPredicate(EssentialsUtil.buildContext()))
                                                .executes(context -> fill(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "fromLocation"),
                                                        BlockPosArgument.getLoadedBlockPos(context, "toLocation"), BlockStateArgument.getBlock(context, "material"),
                                                        Mode.REPLACE, BlockPredicateArgument.getBlockPredicate(context, "filter"))))))));
    }

    private int fill(@NotNull CommandSourceStack source, @NotNull BlockPos from, @NotNull BlockPos to, @NotNull BlockInput block, @NotNull Mode fillMode, @Nullable Predicate<BlockInWorld> filter) throws CommandSyntaxException {
        BoundingBox boundingBox = BoundingBox.fromCorners(from, to);
        int totalBlocks = boundingBox.getXSpan() * boundingBox.getYSpan() * boundingBox.getZSpan();
        int maxFillArea = source.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);

        if (totalBlocks > maxFillArea) throw ERROR_AREA_TOO_LARGE.create(maxFillArea, totalBlocks);

        List<BlockPos> blockPosList = new ArrayList<>();
        ServerLevel serverLevel = source.getLevel();
        AtomicInteger filledBlocks = new AtomicInteger();
        Iterator<BlockPos> blockPosIterator = BlockPos.betweenClosed(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ(), boundingBox.maxX(), boundingBox.maxY(), boundingBox.maxZ()).iterator();
        List<BlockStatePos> blockStatePos = new ArrayList<>();

        blockPosIterator.forEachRemaining(currentBlockPos -> {
            if (filter != null && !filter.test(new BlockInWorld(serverLevel, currentBlockPos, true))) return;

            BlockInput filteredBlockInput = fillMode.filter.filter(boundingBox, currentBlockPos, block, serverLevel);

            if (filteredBlockInput == null) return;

            BlockEntity blockEntity = serverLevel.getBlockEntity(currentBlockPos);
            Clearable.tryClear(blockEntity);

            BlockState currentBlockState = serverLevel.getBlockState(currentBlockPos);

            if (filteredBlockInput.place(serverLevel, currentBlockPos, 2)) {
                blockStatePos.add(new BlockStatePos(currentBlockState, currentBlockPos.immutable(), serverLevel));

                blockPosList.add(currentBlockPos.immutable());
                filledBlocks.getAndIncrement();
            }
        });

        for (BlockPos currentBlockPos : blockPosList) {
            Block currentBlock = serverLevel.getBlockState(currentBlockPos).getBlock();
            serverLevel.blockUpdated(currentBlockPos, currentBlock);
        }

        if (filledBlocks.get() == 0) throw ERROR_FAILED.create();

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Es " + ((filledBlocks.get() == 1) ? "wurde " : "wurden "), Colors.SUCCESS)
                    .append(net.kyori.adventure.text.Component.text(filledBlocks.get(), Colors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(((filledBlocks.get() == 1) ? " Block " : " Blöcke ") + "platziert.", Colors.SUCCESS)));
            BLOCKS.put(source.getPlayerOrException().getUUID(), blockStatePos);
        }else {
            source.sendSuccess(Component.translatable("commands.fill.success", filledBlocks.get()), false);
        }

        return filledBlocks.get();
    }

    private int undo(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        UUID uuid = player.getUUID();
        List<BlockStatePos> blockStatePos = BLOCKS.get(uuid);
        List<BlockStatePos> newBlockStatePos = new ArrayList<>();

        if (blockStatePos == null || blockStatePos.isEmpty()) throw ERROR_NOTHING_TO_UNDO.create();

        AtomicInteger undoneBlocks = new AtomicInteger();

        for (BlockStatePos statePos : blockStatePos) {
            ServerLevel level = statePos.serverLevel();
            BlockState blockState = level.getBlockState(statePos.blockPos());
            newBlockStatePos.add(new BlockStatePos(blockState, statePos.blockPos(), level));

            level.setBlockAndUpdate(statePos.blockPos().immutable(), statePos.blockState());
            level.blockUpdated(statePos.blockPos(), statePos.blockState().getBlock());
            undoneBlocks.getAndIncrement();
        }

        BLOCKS.put(uuid, newBlockStatePos);

        if (undoneBlocks.get() == 0) throw ERROR_NOTHING_TO_UNDO.create();

        EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Es " + ((undoneBlocks.get() == 1) ? "wurde " : "wurden "), Colors.SUCCESS)
                .append(net.kyori.adventure.text.Component.text(undoneBlocks.get(), Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text(((undoneBlocks.get() == 1) ? " Block " : " Blöcke ") + "zurückgesetzt.", Colors.SUCCESS)));

        return undoneBlocks.get();
    }


    enum Mode {
        REPLACE((range, pos, block, world) -> block),
        OUTLINE((range, pos, block, world) -> pos.getX() != range.minX() && pos.getX() != range.maxX() && pos.getY() != range.minY() && pos.getY() != range.maxY() && pos.getZ() != range.minZ() && pos.getZ() != range.maxZ() ? null : block),
        HOLLOW((range, pos, block, world) -> pos.getX() != range.minX() && pos.getX() != range.maxX() && pos.getY() != range.minY() && pos.getY() != range.maxY() && pos.getZ() != range.minZ() && pos.getZ() != range.maxZ() ? HOLLOW_CORE : block),
        DESTROY((range, pos, block, world) -> {
            world.destroyBlock(pos, true);
            return block;
        });

        public final SetBlockCommand.Filter filter;

        Mode(SetBlockCommand.Filter filter) {
            this.filter = filter;
        }
    }
}
