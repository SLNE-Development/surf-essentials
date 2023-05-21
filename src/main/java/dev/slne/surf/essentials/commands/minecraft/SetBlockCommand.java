package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.blocks.BlockStatePos;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class SetBlockCommand extends BrigadierCommand {
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.setblock.failed"));
    private static final Map<UUID, BlockStatePos> BLOCK = new HashMap<>();
    @Override
    public String[] names() {
        return new String[]{"setblock"};
    }

    @Override
    public String usage() {
        return "/setblock <undo | location <block> [destroy | keep | replace]>";
    }

    @Override
    public String description() {
        return "Change the block at the given location";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.SET_BLOCK_PERMISSION));

        literal.then(Commands.argument("location", BlockPosArgument.blockPos())
                .then(Commands.argument("block", BlockStateArgument.block(this.commandBuildContext))
                        .executes(context -> setBlock(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "location"),
                                BlockStateArgument.getBlock(context, "block"), Mode.REPLACE, null))

                        .then(Commands.literal("keep")
                                .executes(context -> setBlock(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "location"),
                                        BlockStateArgument.getBlock(context, "block"), Mode.REPLACE, (pos) -> pos.getLevel().isEmptyBlock(pos.getPos())))
                                .then(Commands.argument("filter", BlockPredicateArgument.blockPredicate(this.commandBuildContext))
                                        .executes(context -> setBlock(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "location"),
                                                BlockStateArgument.getBlock(context, "block"), Mode.REPLACE,
                                                BlockPredicateArgument.getBlockPredicate(context, "filter")))))

                        .then(Commands.literal("replace")
                                .executes(context -> setBlock(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "location"),
                                        BlockStateArgument.getBlock(context, "block"), Mode.REPLACE, null))
                                .then(Commands.argument("filter", BlockPredicateArgument.blockPredicate(this.commandBuildContext))
                                        .executes(context -> setBlock(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "location"),
                                                BlockStateArgument.getBlock(context, "block"), Mode.REPLACE,
                                                BlockPredicateArgument.getBlockPredicate(context, "filter")))))

                        .then(Commands.literal("destroy")
                                .executes(context -> setBlock(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "location"),
                                        BlockStateArgument.getBlock(context, "block"), Mode.DESTROY, null))
                                .then(Commands.argument("filter", BlockPredicateArgument.blockPredicate(this.commandBuildContext))
                                        .executes(context -> setBlock(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "location"),
                                                BlockStateArgument.getBlock(context, "block"), Mode.DESTROY,
                                                BlockPredicateArgument.getBlockPredicate(context, "filter")))))

                        .then(Commands.literal("filter")
                                .then(Commands.argument("filter", BlockPredicateArgument.blockPredicate(this.commandBuildContext))
                                        .executes(context -> setBlock(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "location"),
                                                BlockStateArgument.getBlock(context, "block"), Mode.DESTROY,
                                                BlockPredicateArgument.getBlockPredicate(context, "filter")))))));



        literal.then(Commands.literal("undo")
                .executes(context -> undo(context.getSource())));
    }

    private int setBlock(@NotNull CommandSourceStack source, @NotNull BlockPos blockPos, @NotNull BlockInput blockInput, @NotNull Mode mode, @Nullable Predicate<BlockInWorld> condition) throws CommandSyntaxException {
        ServerLevel serverLevel = source.getLevel();

        if (condition != null && !condition.test(new BlockInWorld(serverLevel, blockPos, true)))
            throw ERROR_FAILED.create();

        boolean bl;

        BlockState oldBlockState = serverLevel.getBlockState(blockPos);

        if (mode == Mode.DESTROY) {
            serverLevel.destroyBlock(blockPos, true);
            bl = !blockInput.getState().isAir() || !serverLevel.getBlockState(blockPos).isAir();
        } else {
            BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
            Clearable.tryClear(blockEntity);
            bl = true;
        }

        if (bl && !blockInput.place(serverLevel, blockPos, 2)) throw ERROR_FAILED.create();

        EssentialsUtil.logBlockChange(source, serverLevel, blockPos, blockInput.getState());

        serverLevel.blockUpdated(blockPos, blockInput.getState().getBlock());

        if (source.isPlayer()){
            BLOCK.put(source.getPlayerOrException().getUUID(), new BlockStatePos(oldBlockState, blockPos, serverLevel));

            EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Der Block ", Colors.SUCCESS)
                    .append(PaperAdventure.asAdventure(oldBlockState.getBlock().getName()).colorIfAbsent(Colors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(" bei ", Colors.SUCCESS))
                    .append(net.kyori.adventure.text.Component.text("%s %s %s".formatted(blockPos.getX(), blockPos.getY(), blockPos.getZ())))
                    .append(net.kyori.adventure.text.Component.text(" wurde zu ", Colors.SUCCESS))
                    .append(PaperAdventure.asAdventure(blockInput.getState().getBlock().getName()).colorIfAbsent(Colors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(" geändert.", Colors.SUCCESS)));
        } else {
            source.sendSuccess(Component.translatable("commands.setblock.success", blockPos.getX(), blockPos.getY(), blockPos.getZ()), false);
        }
        return 1;
    }

    private int undo(@NotNull CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        UUID uuid = player.getUUID();
        BlockStatePos blockStatePos = BLOCK.get(uuid);

        if (blockStatePos == null) throw FillCommand.ERROR_NOTHING_TO_UNDO.create();

        ServerLevel level = blockStatePos.serverLevel();
        BlockPos pos = blockStatePos.blockPos();

        BlockState blockState = level.getBlockState(pos);
        BLOCK.put(uuid, new BlockStatePos(blockState, pos, level));

        if (level.setBlockAndUpdate(pos.immutable(), blockStatePos.blockState())) {
            EssentialsUtil.logBlockChange(source, level, pos, blockStatePos.blockState());
        }
        level.blockUpdated(blockStatePos.blockPos(), blockStatePos.blockState().getBlock());

        EssentialsUtil.sendSuccess(source, "Der Block wurde rückgängig gemacht");
        return 1;
    }


    public enum Mode {
        REPLACE,
        DESTROY;

        Mode(){
        }
    }

    public interface Filter{
        @Nullable
        BlockInput filter(BoundingBox box, BlockPos pos, BlockInput block, ServerLevel world);
    }
}
