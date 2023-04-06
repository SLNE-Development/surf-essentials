package dev.slne.surf.essentials.utils.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public record BlockStatePos(BlockState blockState, BlockPos blockPos, ServerLevel serverLevel) {}