package dev.slne.surf.essentials.utils.nms.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * A record representing a dimension and a block position in that dimension.
 */
public record DimensionAndPosition(ServerLevel dimension, BlockPos position) {
}
