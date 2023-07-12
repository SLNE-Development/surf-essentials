package dev.slne.surf.essentials.utils.blocks;

import io.papermc.paper.math.BlockPosition;
import org.bukkit.World;
import org.bukkit.block.BlockState;

/**
 * BlockStatePos is a record that contains a BlockState, a BlockPosition and a World.
 *
 * @param blockState  The BlockState
 * @param blockPos    The BlockPosition
 * @param world The World
 */
public record BlockStatePos(BlockState blockState, BlockPosition blockPos, World world) {

}