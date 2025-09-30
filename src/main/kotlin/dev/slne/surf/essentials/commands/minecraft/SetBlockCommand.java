package dev.slne.surf.essentials.commands.minecraft;

import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.blocks.BlockStatePos;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlockInventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

public class SetBlockCommand extends EssentialsCommand { // TODO test filter

    private static final Map<UUID, BlockStatePos> BLOCK = new HashMap<>();

    public SetBlockCommand() {
        super("setblock", "setblock <undo | location <block> [destroy | keep | replace]>", "Change the block at the given location");

        withPermission(Permissions.SET_BLOCK_PERMISSION);


        then(locationArgument("location", LocationType.BLOCK_POSITION)
                .then(blockStateArgument("block")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> setBlock(
                                sender.getCallee(),
                                Objects.requireNonNull(args.getUnchecked("location")),
                                Objects.requireNonNull(args.getUnchecked("block")),
                                Mode.REPLACE,
                                Optional.empty()
                        ))
                        .then(literal("keep")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> setBlock(
                                        sender.getCallee(),
                                        Objects.requireNonNull(args.getUnchecked("location")),
                                        Objects.requireNonNull(args.getUnchecked("block")),
                                        Mode.REPLACE,
                                        Optional.empty()
                                ))
                                .then(blockPredicateArgument("filter")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> setBlock(
                                                sender.getCallee(),
                                                Objects.requireNonNull(args.getUnchecked("location")),
                                                Objects.requireNonNull(args.getUnchecked("block")),
                                                Mode.REPLACE,
                                                Optional.ofNullable(args.getUnchecked("filter"))
                                        ))
                                )
                        )
                        .then(literal("destroy")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> setBlock(
                                        sender.getCallee(),
                                        Objects.requireNonNull(args.getUnchecked("location")),
                                        Objects.requireNonNull(args.getUnchecked("block")),
                                        Mode.DESTROY,
                                        Optional.empty()
                                ))
                                .then(blockPredicateArgument("filter")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> setBlock(
                                                sender.getCallee(),
                                                Objects.requireNonNull(args.getUnchecked("location")),
                                                Objects.requireNonNull(args.getUnchecked("block")),
                                                Mode.DESTROY,
                                                Optional.ofNullable(args.getUnchecked("filter"))
                                        ))
                                )
                        )
                        .then(literal("replace")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> setBlock(
                                        sender.getCallee(),
                                        Objects.requireNonNull(args.getUnchecked("location")),
                                        Objects.requireNonNull(args.getUnchecked("block")),
                                        Mode.REPLACE,
                                        Optional.empty()
                                ))
                                .then(blockPredicateArgument("filter")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> setBlock(
                                                sender.getCallee(),
                                                Objects.requireNonNull(args.getUnchecked("location")),
                                                Objects.requireNonNull(args.getUnchecked("block")),
                                                Mode.REPLACE,
                                                Optional.ofNullable(args.getUnchecked("filter"))
                                        ))
                                )
                        )
                        .then(literal("filter")
                                .then(blockPredicateArgument("filter")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> setBlock(
                                                sender.getCallee(),
                                                Objects.requireNonNull(args.getUnchecked("location")),
                                                Objects.requireNonNull(args.getUnchecked("block")),
                                                Mode.REPLACE,
                                                Optional.ofNullable(args.getUnchecked("filter"))
                                        ))
                                )
                        )
                )
        );

        then(literal("undo")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> undo(getPlayerOrException(sender))));
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "UnstableApiUsage"})
    private int setBlock(@NotNull CommandSender source, @NotNull Location blockPos, @NotNull BlockData blockInput, @NotNull Mode mode, Optional<Predicate<Block>> condition) throws WrapperCommandSyntaxException {
        val world = blockPos.getWorld();
        val newBlockState = blockInput.createBlockState();

        if (condition.isPresent() && !condition.get().test(blockPos.getBlock())) throw Exceptions.ERROR_BLOCK_NOT_SET;

        boolean success;
        val oldBlock = world.getBlockAt(blockPos);
        val oldBlockState = oldBlock.getState();
        val oldBlockMaterial = oldBlock.getType();

        if (mode == Mode.DESTROY) {
            oldBlock.breakNaturally();
            success = !newBlockState.getType().isAir() || !world.getBlockState(blockPos).getType().isAir();
        } else {
            if (oldBlock instanceof BlockInventoryHolder blockInventoryHolder) { // TODO: Check if this works
                blockInventoryHolder.getInventory().clear();
            }
            success = true;
        }

        if (!success) throw Exceptions.ERROR_BLOCK_NOT_SET;


        oldBlock.setBlockData(blockInput);
        EssentialsUtil.logBlockChange(
                source,
                world,
                blockPos,
                newBlockState
        );

        // TODO: maybe update block state?

        if (source instanceof Player player) {

            BLOCK.put(player.getUniqueId(), new BlockStatePos(oldBlockState, blockPos.toBlock(), world));
        }

        EssentialsUtil.sendSuccess(source, Component.text("Der Block ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(oldBlockMaterial))
                .append(Component.text(" bei ", Colors.SUCCESS))
                .append(Component.text("%s %s %s".formatted(blockPos.getX(), blockPos.getY(), blockPos.getZ())))
                .append(Component.text(" wurde zu ", Colors.SUCCESS))
                .append(EssentialsUtil.getDisplayName(newBlockState.getType()))
                .append(Component.text(" geändert.", Colors.SUCCESS)));
        return 1;
    }

    @SuppressWarnings("UnstableApiUsage")
    private int undo(@NotNull Player player) throws WrapperCommandSyntaxException {
        val uuid = player.getUniqueId();
        val blockStatePos = BLOCK.get(uuid);

        if (blockStatePos == null) throw Exceptions.ERROR_NOTHING_TO_UNDO;

        val level = blockStatePos.world();
        val pos = blockStatePos.blockPos().toLocation(level);
        val blockState = level.getBlockState(pos);
        val newBlockState = blockStatePos.blockState();

        BLOCK.put(uuid, new BlockStatePos(blockState, pos.toBlock(), level));
        pos.getBlock().setBlockData(newBlockState.getBlockData());
        EssentialsUtil.logBlockChange(
                player,
                level,
                pos,
                newBlockState
        );

        EssentialsUtil.sendSuccess(player, "Der Block wurde rückgängig gemacht");
        return 1;
    }

    private enum Mode {
        REPLACE,
        DESTROY
    }
}
