package dev.slne.surf.essentials.utils.nms.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A record that represents the combination of a block state, a block position, and a server level.
 */
public record BlockStatePos(BlockState blockState, BlockPos blockPos, ServerLevel serverLevel) {
}