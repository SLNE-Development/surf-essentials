package dev.slne.surf.essentials.utils.nms.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A record that represents a cloned block with its position, state, block entity and server level.
 */
public record CloneBlock(BlockPos pos, BlockState state, BlockEntity blockEntity, ServerLevel level) {}