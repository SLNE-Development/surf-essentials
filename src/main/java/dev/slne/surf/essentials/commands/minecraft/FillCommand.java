package dev.slne.surf.essentials.commands.minecraft;

import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.blocks.BlockStatePos;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.math.BlockPosition;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class FillCommand extends EssentialsCommand { // TODO: Fill with fast async world edit if installed
    private static final BlockData HOLLOW_CORE = Material.AIR.createBlockData();
    private static final Map<UUID, List<BlockStatePos>> BLOCKS = new HashMap<>();

    public FillCommand() {
        super("fill", "fill <undo | fromLocation <toLocation <material> [<destroy|hollow|keep|outline|replace|filter>]>>", "Fill a region with a specific block");

        withPermission(Permissions.FILL_PERMISSION);

        then(literal("undo")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> undo(getPlayerOrException(sender))));

        then(locationArgument("from", LocationType.BLOCK_POSITION)
                .then(locationArgument("to", LocationType.BLOCK_POSITION)
                        .then(blockStateArgument("material")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> fill(
                                        sender,
                                        args.getUnchecked("from"),
                                        args.getUnchecked("to"),
                                        args.getUnchecked("material"),
                                        Mode.REPLACE,
                                        Optional.empty()
                                ))
                                .then(literal("replace")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> fill(
                                                sender,
                                                args.getUnchecked("from"),
                                                args.getUnchecked("to"),
                                                args.getUnchecked("material"),
                                                Mode.REPLACE,
                                                Optional.empty()
                                        ))
                                )
                                .then(literal("filter")
                                        .then(blockPredicateArgument("filter")
                                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> fill(
                                                        sender,
                                                        args.getUnchecked("from"),
                                                        args.getUnchecked("to"),
                                                        args.getUnchecked("material"),
                                                        Mode.REPLACE,
                                                        Optional.ofNullable(args.getUnchecked("filter"))
                                                ))
                                        )
                                )
                                .then(literal("outline")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> fill(
                                                sender,
                                                args.getUnchecked("from"),
                                                args.getUnchecked("to"),
                                                args.getUnchecked("material"),
                                                Mode.OUTLINE,
                                                Optional.empty()
                                        ))
                                        .then(literal("filter")
                                                .then(blockPredicateArgument("filter")
                                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> fill(
                                                                sender,
                                                                args.getUnchecked("from"),
                                                                args.getUnchecked("to"),
                                                                args.getUnchecked("material"),
                                                                Mode.OUTLINE,
                                                                Optional.ofNullable(args.getUnchecked("filter"))
                                                        ))
                                                )
                                        )
                                )
                                .then(literal("hollow")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> fill(
                                                sender,
                                                args.getUnchecked("from"),
                                                args.getUnchecked("to"),
                                                args.getUnchecked("material"),
                                                Mode.HOLLOW,
                                                Optional.empty()
                                        ))
                                        .then(literal("filter")
                                                .then(blockPredicateArgument("filter")

                                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> fill(
                                                                sender,
                                                                args.getUnchecked("from"),
                                                                args.getUnchecked("to"),
                                                                args.getUnchecked("material"),
                                                                Mode.HOLLOW,
                                                                Optional.ofNullable(args.getUnchecked("filter"))
                                                        ))
                                                )
                                        )
                                )
                                .then(literal("destroy")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> fill(
                                                sender,
                                                args.getUnchecked("from"),
                                                args.getUnchecked("to"),
                                                args.getUnchecked("material"),
                                                Mode.DESTROY,
                                                Optional.empty()
                                        ))
                                        .then(literal("filter")
                                                .then(blockPredicateArgument("filter")
                                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> fill(
                                                                sender,
                                                                args.getUnchecked("from"),
                                                                args.getUnchecked("to"),
                                                                args.getUnchecked("material"),
                                                                Mode.DESTROY,
                                                                Optional.ofNullable(args.getUnchecked("filter"))
                                                        ))
                                                )
                                        )
                                )
                                .then(literal("keep")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> fill(
                                                sender,
                                                args.getUnchecked("from"),
                                                args.getUnchecked("to"),
                                                args.getUnchecked("material"),
                                                Mode.REPLACE,
                                                Optional.empty()
                                        ))
                                        .then(literal("filter")
                                                .then(blockPredicateArgument("filter")
                                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> fill(
                                                                sender,
                                                                args.getUnchecked("from"),
                                                                args.getUnchecked("to"),
                                                                args.getUnchecked("material"),
                                                                Mode.REPLACE,
                                                                Optional.ofNullable(args.getUnchecked("filter"))
                                                        ))
                                                )
                                        )
                                )
                                .then(literal("filter")
                                        .then(blockPredicateArgument("filter")
                                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> fill(
                                                        sender,
                                                        args.getUnchecked("from"),
                                                        args.getUnchecked("to"),
                                                        args.getUnchecked("material"),
                                                        Mode.REPLACE,
                                                        Optional.ofNullable(args.getUnchecked("filter"))
                                                ))
                                        )
                                )
                        )
                )
        );
    }


    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private int fill(@NotNull NativeProxyCommandSender source, @NotNull Location from, @NotNull Location to, @NotNull BlockData block, @NotNull Mode fillMode, @NotNull Optional<Predicate<org.bukkit.block.Block>> filter) throws WrapperCommandSyntaxException {
        val boundingBox = BoundingBox.of(from, to);
        val totalBlocks = (int) boundingBox.getVolume();
        val maxFillArea = EssentialsUtil.modificationBlockLimit(source.getWorld());

        if (totalBlocks > maxFillArea) throw Exceptions.ERROR_FILL_AREA_TOO_LARGE.create(maxFillArea, totalBlocks);

        val blockPosList = new ArrayList<BlockPosition>();
        val blockStatePos = new ArrayList<BlockStatePos>();
        val world = source.getWorld();
        int filledBlocks = 0;

        for (Block currentBlock : EssentialsUtil.getBlocksFromBoundingBox(world, boundingBox)) {
            if (filter.isPresent() && !filter.get().test(currentBlock)) continue;

            val currentBlockState = currentBlock.getState();
            val filteredBlockData = fillMode.filter.filter(boundingBox, currentBlock.getLocation().toBlock(), block, world);

            if (filteredBlockData == null) continue;

            if (currentBlock instanceof BlockInventoryHolder blockInventoryHolder) {
                blockInventoryHolder.getInventory().clear();
            }

            world.setBlockData(currentBlock.getLocation(), filteredBlockData);
            blockStatePos.add(new BlockStatePos(fillMode == Mode.DESTROY ? currentBlockState : currentBlock.getState(), currentBlock.getLocation().toBlock(), world));
            blockPosList.add(currentBlock.getLocation().toBlock());
            filledBlocks++;

            EssentialsUtil.logBlockChange(
                    source.getCallee(),
                    world,
                    currentBlock.getLocation().toBlock(),
                    currentBlock.getState()
            );
        }

        for (BlockPosition currentBlockPos : blockPosList) {
            world.getBlockState(currentBlockPos.toLocation(world)).update(true, true);
        }

        if (filledBlocks == 0) throw Exceptions.ERROR_FILL_FAILED;


        EssentialsUtil.sendSuccess(source.getCallee(), Component.text("Es " + ((filledBlocks == 1) ? "wurde " : "wurden "), Colors.SUCCESS)
                .append(Component.text(filledBlocks, Colors.TERTIARY))
                .append(Component.text(((filledBlocks == 1) ? " Block " : " Blöcke ") + "platziert.", Colors.SUCCESS)));

        if (source.getCallee() instanceof Player player) {
            BLOCKS.put(player.getUniqueId(), blockStatePos);
        }

        return filledBlocks;
    }

    private int undo(Player source) throws WrapperCommandSyntaxException {
        val uuid = source.getUniqueId();
        val oldBlockStatePos = BLOCKS.get(uuid);
        val newBlockStatePos = new ArrayList<BlockStatePos>();
        int undoneBlocks = 0;

        if (oldBlockStatePos == null || oldBlockStatePos.isEmpty()) throw Exceptions.ERROR_NOTHING_TO_UNDO;

        for (BlockStatePos statePos : oldBlockStatePos) {
            val level = statePos.world();
            val pos = statePos.blockPos().toLocation(level);
            val blockState = level.getBlockState(pos.toLocation(level));

            newBlockStatePos.add(new BlockStatePos(blockState, pos.toBlock(), level));
            level.setBlockData(pos, statePos.blockState().getBlockData());
            pos.getBlock().getState().update(true, true);

            EssentialsUtil.logBlockChange(
                    source,
                    level,
                    pos.toBlock(),
                    statePos.blockState()
            );

            undoneBlocks++;
        }

        BLOCKS.put(uuid, newBlockStatePos);

        if (undoneBlocks == 0) throw Exceptions.ERROR_NOTHING_TO_UNDO;

        EssentialsUtil.sendSuccess(source, Component.text("Es " + ((undoneBlocks == 1) ? "wurde " : "wurden "), Colors.SUCCESS)
                .append(Component.text(undoneBlocks, Colors.TERTIARY))
                .append(Component.text(((undoneBlocks == 1) ? " Block " : " Blöcke ") + "zurückgesetzt.", Colors.SUCCESS)));

        return undoneBlocks;
    }


    @SuppressWarnings("UnstableApiUsage")
    @RequiredArgsConstructor
    @Getter
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    enum Mode {
        REPLACE((range, pos, block, world) -> block),
        OUTLINE((range, pos, block, world) -> pos.x() != range.getMinX() && pos.x() != range.getMinX() && pos.y() != range.getMinY() && pos.y() != range.getMaxY() && pos.z() != range.getMinZ() && pos.z() != range.getMaxZ() ? null : block),
        HOLLOW((range, pos, block, world) -> pos.x() != range.getMinX() && pos.x() != range.getMinX() && pos.y() != range.getMinY() && pos.y() != range.getMaxY() && pos.z() != range.getMinZ() && pos.z() != range.getMaxZ() ? HOLLOW_CORE : block),
        DESTROY((range, pos, block, world) -> {
            world.getBlockAt(pos.toLocation(world)).breakNaturally();
            return block;
        });

        Filter filter;
    }

    @FunctionalInterface
    public interface Filter {
        @SuppressWarnings("UnstableApiUsage")
        @Nullable
        BlockData filter(BoundingBox box, BlockPosition pos, BlockData block, World world);
    }
}
