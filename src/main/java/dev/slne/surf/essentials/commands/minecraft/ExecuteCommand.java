package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.slne.surf.essentials.annontations.UpdateRequired;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.*;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

@UpdateRequired(minVersion = "1.21", updateReason = "Maybe Mojang will add more options")
public class ExecuteCommand extends BrigadierCommand {

    @UpdateRequired
    private static final int MAX_TEST_AREA = 32768;

    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE;

    private static final SimpleCommandExceptionType ERROR_CONDITIONAL_FAILED;

    private static final DynamicCommandExceptionType ERROR_CONDITIONAL_FAILED_COUNT;

    private static final BinaryOperator<ResultConsumer<CommandSourceStack>> CALLBACK_CHAINER;

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_PREDICATE;

    @Override
    public String[] names() {
        return new String[]{"execute"};
    }

    @Override
    public String usage() {
        return "/execute <...>";
    }

    @Override
    public String description() {
        return "Execute commands";
    }

    @Override
    public void literal(@NotNull LiteralArgumentBuilder<CommandSourceStack> literal) {
        final var dispatcher = EssentialsUtil.getDispatcher();

        final var literalCommandNode = dispatcher.register(Commands.literal("execute")
                .requires(EssentialsUtil.checkPermissions(Permissions.EXECUTE_COMMAND_PERMISSION)));

        literal.requires(EssentialsUtil.checkPermissions(Permissions.EXECUTE_COMMAND_PERMISSION));

        literal.then(Commands.literal("run") // run a command
                .redirect(EssentialsUtil.getRoot()));

        literal.then(addConditionals(literalCommandNode, Commands.literal("if"), true, this.commandBuildContext)) // if cases
                .then(addConditionals(literalCommandNode, Commands.literal("unless"), false, this.commandBuildContext)) // unless cases - same as if cases but inverted
                .then(Commands.literal("as") // execute the command as someone else
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .fork(literalCommandNode, context -> {
                                    final var list = EssentialsUtil.make(new ArrayList<CommandSourceStack>());
                                    list.addAll(EntityArgument.getOptionalEntities(context, "targets")
                                            .stream()
                                            .map(entity -> context.getSource()
                                                    .withEntity(entity))
                                            .toList());
                                    return list;
                                })
                        )
                )
                .then(Commands.literal("asServer") // execute the command as the server
                        .fork(literalCommandNode, context ->
                                Collections.singletonList(context.getSource().getServer().createCommandSourceStack())))

                .then(Commands.literal("at") // execute the command at the give location
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .fork(literalCommandNode, context -> {
                                    final var list = EssentialsUtil.make(new ArrayList<CommandSourceStack>());
                                    list.addAll(EntityArgument.getOptionalEntities(context, "targets")
                                            .stream()
                                            .map(entity -> context.getSource()
                                                    .withLevel(((ServerLevel) entity.level()))
                                                    .withPosition(entity.position())
                                                    .withRotation(entity.getRotationVector()))
                                            .toList());
                                    return list;
                                })
                        )
                )

                .then(Commands.literal("store")
                        .then(wrapStores(literalCommandNode, Commands.literal("result"), true))
                        .then(wrapStores(literalCommandNode, Commands.literal("success"), false))
                )

                .then(Commands.literal("positioned")
                        .then(Commands.argument("pos", Vec3Argument.vec3())
                                .redirect(literalCommandNode, context -> context.getSource()
                                        .withPosition(Vec3Argument.getVec3(context, "pos"))
                                        .withAnchor(EntityAnchorArgument.Anchor.FEET))
                        )
                        .then(Commands.literal("as")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .fork(literalCommandNode, context -> {
                                            final var list = EssentialsUtil.make(new ArrayList<CommandSourceStack>());
                                            list.addAll(EntityArgument.getOptionalEntities(context, "targets")
                                                    .stream()
                                                    .map(entity -> context.getSource()
                                                            .withPosition(entity.position()))
                                                    .toList());
                                            return list;
                                        })
                                )
                        )
                        .then(Commands.literal("over")
                                .then(Commands.argument("heightmap", HeightmapTypeArgument.heightmap())
                                        .redirect(literalCommandNode, context -> {
                                            final var position = context.getSource().getPosition();
                                            final var serverLevel = context.getSource().getLevel();
                                            final double x = position.x();
                                            final double z = position.z();

                                            if (!serverLevel.hasChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z)))
                                                throw BlockPosArgument.ERROR_NOT_LOADED.create();

                                            final int height = serverLevel.getHeight(HeightmapTypeArgument.getHeightmap(context, "heightmap"), Mth.floor(x), Mth.floor(z));

                                            return context.getSource().withPosition(new Vec3(x, height, z));

                                        })
                                )
                        )
                )

                .then(Commands.literal("rotated")
                        .then(Commands.argument("rot", RotationArgument.rotation())
                                .redirect(literalCommandNode, context -> context.getSource()
                                        .withRotation(RotationArgument.getRotation(context, "rot").getRotation(context.getSource())))
                        )
                        .then(Commands.literal("as")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .fork(literalCommandNode, context -> {
                                            final var list = EssentialsUtil.make(new ArrayList<CommandSourceStack>());
                                            list.addAll(EntityArgument.getOptionalEntities(context, "targets")
                                                    .stream()
                                                    .map(entity -> context.getSource()
                                                            .withRotation(entity.getRotationVector()))
                                                    .toList());
                                            return list;
                                        })
                                )
                        )
                )

                .then(Commands.literal("facing")
                        .then(Commands.literal("entity")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .then(Commands.argument("anchor", EntityAnchorArgument.anchor())
                                                .fork(literalCommandNode, context -> {
                                                    final var list = EssentialsUtil.make(new ArrayList<CommandSourceStack>());
                                                    final var anchor = EntityAnchorArgument.getAnchor(context, "anchor");

                                                    list.addAll(EntityArgument.getOptionalEntities(context, "targets")
                                                            .stream()
                                                            .map(entity -> context.getSource()
                                                                    .facing(entity, anchor))
                                                            .toList());

                                                    return list;
                                                })
                                        )
                                )
                        )
                        .then(Commands.argument("pos", Vec3Argument.vec3())
                                .redirect(literalCommandNode, context -> context.getSource()
                                        .facing(Vec3Argument.getVec3(context, "pos")))
                        )
                )

                .then(Commands.literal("align")
                        .then(Commands.argument("axes", SwizzleArgument.swizzle())
                                .redirect(literalCommandNode, context -> context.getSource()
                                        .withPosition(context.getSource().getPosition().align(SwizzleArgument.getSwizzle(context, "axes"))))
                        )
                )

                .then(Commands.literal("anchored")
                        .then(Commands.argument("anchor", EntityAnchorArgument.anchor())
                                .redirect(literalCommandNode, context -> context.getSource()
                                        .withAnchor(EntityAnchorArgument.getAnchor(context, "anchor")))
                        )
                )

                .then(Commands.literal("in")
                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                                .redirect(literalCommandNode, context -> context.getSource()
                                        .withLevel(DimensionArgument.getDimension(context, "dimension")))
                        )
                )

                .then(Commands.literal("summon")
                        .then(Commands.argument("entity", ResourceArgument.resource(this.commandBuildContext, Registries.ENTITY_TYPE))
                                .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                .redirect(literalCommandNode, context -> spawnEntityAndRedirect(context.getSource(), ResourceArgument.getSummonableEntityType(context, "entity")))
                        )
                )

                .then(createRelationOperations(literalCommandNode, Commands.literal("on")));
    }

    @Contract("_, _, _ -> param2")
    private @NotNull ArgumentBuilder<CommandSourceStack, ?> wrapStores(LiteralCommandNode<CommandSourceStack> node, @NotNull LiteralArgumentBuilder<CommandSourceStack> builder, boolean requestResult) {
        builder.then(Commands.literal("score")
                .then(Commands.argument("targets", ScoreHolderArgument.scoreHolders())
                        .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                        .then(Commands.argument("objective", ObjectiveArgument.objective())
                                .redirect(node, context -> storeValue(
                                        context.getSource(),
                                        ScoreHolderArgument.getNamesWithDefaultWildcard(context, "targets"),
                                        ObjectiveArgument.getObjective(context, "objective"), requestResult)
                                )
                        )
                )
        );

        builder.then(Commands.literal("bossbar")
                .then(Commands.argument("id", ResourceLocationArgument.id())
                        .suggests(BossbarCommand.SUGGEST_BOSS_BAR)
                        .then(Commands.literal("value")
                                .redirect(node, context -> storeValue(
                                        context.getSource(),
                                        BossbarCommand.getBossBar(context),
                                        true,
                                        requestResult
                                ))
                        )
                        .then(Commands.literal("max")
                                .redirect(node, context -> storeValue(
                                        context.getSource(),
                                        BossbarCommand.getBossBar(context),
                                        false,
                                        requestResult
                                ))
                        )
                )
        );

        for (DataCommands.DataProvider dataProvider : DataCommands.TARGET_PROVIDERS) {
            dataProvider.wrap(builder, builderx ->
                    builderx.then(Commands.argument("path", NbtPathArgument.nbtPath())
                            .then(Commands.literal("int")
                                    .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                            .redirect(node, context -> storeData(
                                                    context.getSource(),
                                                    dataProvider.access(context),
                                                    NbtPathArgument.getPath(context, "path"),
                                                    result -> IntTag.valueOf((int) (result * DoubleArgumentType.getDouble(context, "scale"))),
                                                    requestResult
                                            ))
                                    )
                            )
                            .then(Commands.literal("float")
                                    .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                            .redirect(node, context -> storeData(
                                                    context.getSource(),
                                                    dataProvider.access(context),
                                                    NbtPathArgument.getPath(context, "path"),
                                                    result -> FloatTag.valueOf((float) (result * DoubleArgumentType.getDouble(context, "scale"))),
                                                    requestResult
                                            ))
                                    )
                            )
                            .then(Commands.literal("short")
                                    .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                            .redirect(node, context -> storeData(
                                                    context.getSource(),
                                                    dataProvider.access(context),
                                                    NbtPathArgument.getPath(context, "path"),
                                                    result -> ShortTag.valueOf((short) (result * DoubleArgumentType.getDouble(context, "scale"))),
                                                    requestResult
                                            ))
                                    )
                            )
                            .then(Commands.literal("long")
                                    .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                            .redirect(node, context -> storeData(
                                                    context.getSource(),
                                                    dataProvider.access(context),
                                                    NbtPathArgument.getPath(context, "path"),
                                                    result -> LongTag.valueOf((long) (result * DoubleArgumentType.getDouble(context, "scale"))),
                                                    requestResult
                                            ))
                                    )
                            )
                            .then(Commands.literal("double")
                                    .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                            .redirect(node, context -> storeData(
                                                    context.getSource(),
                                                    dataProvider.access(context),
                                                    NbtPathArgument.getPath(context, "path"),
                                                    result -> DoubleTag.valueOf(result * DoubleArgumentType.getDouble(context, "scale")),
                                                    requestResult)
                                            )
                                    )
                            )
                            .then(Commands.literal("byte")
                                    .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                            .redirect(node, context -> storeData(
                                                    context.getSource(),
                                                    dataProvider.access(context),
                                                    NbtPathArgument.getPath(context, "path"),
                                                    result -> ByteTag.valueOf((byte) (result * DoubleArgumentType.getDouble(context, "scale"))),
                                                    requestResult
                                            ))
                                    )
                            )
                    ));
        }

        return builder;
    }

    private @NotNull CommandSourceStack storeValue(@NotNull CommandSourceStack source, Collection<String> targets, Objective objective, boolean requestResult) {
        final var scoreboard = source.getServer().getScoreboard();
        return source.withCallback((context, success, result) -> {
            int scoreValue = requestResult ? result : (success ? 1 : 0);
            targets.forEach(target -> {
                final var score = scoreboard.getOrCreatePlayerScore(target, objective);
                score.setScore(scoreValue);
            });
        }, CALLBACK_CHAINER);
    }

    private @NotNull CommandSourceStack storeValue(@NotNull CommandSourceStack source, CustomBossEvent bossBar, boolean storeInValue, boolean requestResult) {
        return source.withCallback((context, success, result) -> {
            int value = requestResult ? result : (success ? 1 : 0);

            if (storeInValue) bossBar.setValue(value);
            else bossBar.setMax(value);
        }, CALLBACK_CHAINER);
    }


    private @NotNull CommandSourceStack storeData(@NotNull CommandSourceStack source, DataAccessor accessor, NbtPathArgument.NbtPath path, IntFunction<Tag> nbtSetter, boolean requestResult) {
        return source.withCallback((context, success, result) -> {
            try {
                final var data = accessor.getData();
                final int value = requestResult ? result : (success ? 1 : 0);

                path.set(data, nbtSetter.apply(value));
                accessor.setData(data);
            } catch (CommandSyntaxException ignored) {
            }

        }, CALLBACK_CHAINER);
    }

    private boolean isChunkLoaded(@NotNull ServerLevel level, @NotNull BlockPos blockPos) {
        final int sectionX = SectionPos.blockToSectionCoord(blockPos.getX());
        final int sectionZ = SectionPos.blockToSectionCoord(blockPos.getZ());
        final var levelChunk = level.getChunkSource().getChunkNow(sectionX, sectionZ);

        if (levelChunk != null) return levelChunk.getFullStatus() == FullChunkStatus.ENTITY_TICKING;

        return false;
    }

    private ArgumentBuilder<CommandSourceStack, ?> addConditionals(CommandNode<CommandSourceStack> root, LiteralArgumentBuilder<CommandSourceStack> argumentBuilder, boolean positive, CommandBuildContext commandRegistryAccess) {
        argumentBuilder.then(Commands.literal("randomchance") // executes with a random chance (e.g. /execute if randomchance 1 4 --> 1/4 chance)
                .then(Commands.argument("numerator", IntegerArgumentType.integer(0))
                        .then(addConditional(root, Commands.argument("denominator", IntegerArgumentType.integer(1)), positive, context -> {
                            final int numerator = IntegerArgumentType.getInteger(context, "numerator");
                            final int denominator = IntegerArgumentType.getInteger(context, "denominator");

                            return Math.ceil(Math.random() * denominator) <= numerator;
                        }))));

        argumentBuilder.then(Commands.literal("entityPredicate") // Why I did that is a mystery to me.Perhaps as a result of my boredom
                // executes if a condition is true (e.g. /execute if entityPredicate <entity> flying --> executes if entity is flying)
                .then(Commands.argument("entity", EntityArgument.entity())
                        .then(addConditional(root, Commands.literal("invulnerable"), positive, context -> checkEntity(context, "entity", Entity::isInvulnerable)))
                        .then(addConditional(root, Commands.literal("onGround"), positive, context -> checkEntity(context, "entity", Entity::onGround)))
                        .then(addConditional(root, Commands.literal("isOnPortalCooldown"), positive, context -> checkEntity(context, "entity", Entity::isOnPortalCooldown)))
                        .then(addConditional(root, Commands.literal("isSilent"), positive, context -> checkEntity(context, "entity", Entity::isSilent)))
                        .then(addConditional(root, Commands.literal("isNoGravity"), positive, context -> checkEntity(context, "entity", Entity::isNoGravity)))
                        .then(addConditional(root, Commands.literal("isInWater"), positive, context -> checkEntity(context, "entity", Entity::isInWater)))
                        .then(addConditional(root, Commands.literal("isInRain"), positive, context -> checkEntity(context, "entity", Entity::isInRain)))
                        .then(addConditional(root, Commands.literal("isInBubbleColumn"), positive, context -> checkEntity(context, "entity", Entity::isInBubbleColumn)))
                        .then(addConditional(root, Commands.literal("isWaterOrRain"), positive, context -> checkEntity(context, "entity", Entity::isInWaterOrRain)))
                        .then(addConditional(root, Commands.literal("isInWaterRainOrBubble"), positive, context -> checkEntity(context, "entity", Entity::isInWaterRainOrBubble)))
                        .then(addConditional(root, Commands.literal("isInWaterOrBubble"), positive, context -> checkEntity(context, "entity", Entity::isInWaterOrBubble)))
                        .then(addConditional(root, Commands.literal("isUnderWater"), positive, context -> checkEntity(context, "entity", Entity::isUnderWater)))
                        .then(addConditional(root, Commands.literal("isInLava"), positive, context -> checkEntity(context, "entity", Entity::isInLava)))
                        .then(addConditional(root, Commands.literal("canBeHitByProjectile"), positive, context -> checkEntity(context, "entity", Entity::canBeHitByProjectile)))
                        .then(addConditional(root, Commands.literal("isPickable"), positive, context -> checkEntity(context, "entity", Entity::isPickable)))
                        .then(addConditional(root, Commands.literal("isPushable"), positive, context -> checkEntity(context, "entity", Entity::isPushable)))
                        .then(addConditional(root, Commands.literal("isAlive"), positive, context -> checkEntity(context, "entity", Entity::isAlive)))
                        .then(addConditional(root, Commands.literal("isInWall"), positive, context -> checkEntity(context, "entity", Entity::isInWall)))
                        .then(addConditional(root, Commands.literal("isPassanger"), positive, context -> checkEntity(context, "entity", Entity::isPassenger)))
                        .then(addConditional(root, Commands.literal("isVehicle"), positive, context -> checkEntity(context, "entity", Entity::isVehicle)))
                        .then(addConditional(root, Commands.literal("isCrouching"), positive, context -> checkEntity(context, "entity", Entity::isCrouching)))
                        .then(addConditional(root, Commands.literal("isSprinting"), positive, context -> checkEntity(context, "entity", Entity::isSprinting)))
                        .then(addConditional(root, Commands.literal("isSwimming"), positive, context -> checkEntity(context, "entity", Entity::isSwimming)))
                        .then(addConditional(root, Commands.literal("isInvisible"), positive, context -> checkEntity(context, "entity", Entity::isInvisible)))
                        .then(addConditional(root, Commands.literal("isFullyFrozen"), positive, context -> checkEntity(context, "entity", Entity::isFullyFrozen)))
                        .then(addConditional(root, Commands.literal("canChangeDimensions"), positive, context -> checkEntity(context, "entity", Entity::canChangeDimensions)))
                        .then(addConditional(root, Commands.literal("isPushedByFluid"), positive, context -> checkEntity(context, "entity", Entity::isPushedByFluid)))
                        .then(addConditional(root, Commands.literal("hasAnyPlayerPassengers"), positive, context -> checkEntity(context, "entity", Entity::hasAnyPlayerPassengers)))
                        .then(addConditional(root, Commands.literal("hasExactlyOnePlayerPassenger"), positive, context -> checkEntity(context, "entity", Entity::hasExactlyOnePlayerPassenger)))
                        .then(addConditional(root, Commands.literal("canFreeze"), positive, context -> checkEntity(context, "entity", Entity::canFreeze)))
                        .then(addConditional(root, Commands.literal("isFreezing"), positive, context -> checkEntity(context, "entity", Entity::isFreezing)))
                        .then(addConditional(root, Commands.literal("canSprint"), positive, context -> checkEntity(context, "entity", Entity::canSprint)))

                        .then(addConditional(root, Commands.literal("isInsidePortal"), positive, context -> checkEntity(context, "entity", entity -> entity.isInsidePortal)))
                        .then(addConditional(root, Commands.literal("isInPowderSnow"), positive, context -> checkEntity(context, "entity", entity -> entity.isInPowderSnow)))
                        .then(addConditional(root, Commands.literal("wasInPowderSnow"), positive, context -> checkEntity(context, "entity", entity -> entity.wasInPowderSnow)))
                        .then(addConditional(root, Commands.literal("wasOnFire"), positive, context -> checkEntity(context, "entity", entity -> entity.wasOnFire)))
                        .then(addConditional(root, Commands.literal("hasVisualFire"), positive, context -> checkEntity(context, "entity", entity -> entity.hasVisualFire)))
                        .then(addConditional(root, Commands.literal("persist"), positive, context -> checkEntity(context, "entity", entity -> entity.persist)))
                        .then(addConditional(root, Commands.literal("visibleByDefault"), positive, context -> checkEntity(context, "entity", entity -> entity.visibleByDefault)))
                        .then(addConditional(root, Commands.literal("valid"), positive, context -> checkEntity(context, "entity", entity -> entity.valid)))
                        .then(addConditional(root, Commands.literal("lastDamageCancelled"), positive, context -> checkEntity(context, "entity", entity -> entity.lastDamageCancelled)))
                        .then(addConditional(root, Commands.literal("spawnedViaMobSpawner"), positive, context -> checkEntity(context, "entity", entity -> entity.spawnedViaMobSpawner)))
                        .then(addConditional(root, Commands.literal("collidingWithWorldBorder"), positive, context -> checkEntity(context, "entity", entity -> entity.collidingWithWorldBorder)))

                        .then(addConditional(root, Commands.literal("hasDisconnected"), positive, context -> checkIfPlayer(context, "entity", ServerPlayer::hasDisconnected)))
                        .then(addConditional(root, Commands.literal("isDeadOrDying"), positive, context -> checkIfPlayer(context, "entity", LivingEntity::isDeadOrDying)))
                        .then(addConditional(root, Commands.literal("isUsingItem"), positive, context -> checkIfPlayer(context, "entity", LivingEntity::isUsingItem)))
                        .then(addConditional(root, Commands.literal("canUseGameMasterBlocks"), positive, context -> checkIfPlayer(context, "entity", Player::canUseGameMasterBlocks)))
                        .then(addConditional(root, Commands.literal("canDisableShield"), positive, context -> checkIfPlayer(context, "entity", LivingEntity::canDisableShield)))
                        .then(addConditional(root, Commands.literal("flying"), positive, context -> checkIfPlayer(context, "entity", player -> player.getAbilities().flying)))
                        .then(addConditional(root, Commands.literal("mayBuild"), positive, context -> checkIfPlayer(context, "entity", player -> player.getAbilities().mayBuild)))
                        .then(addConditional(root, Commands.literal("mayFly"), positive, context -> checkIfPlayer(context, "entity", player -> player.getAbilities().mayfly)))
                        .then(addConditional(root, Commands.literal("instaBuild"), positive, context -> checkIfPlayer(context, "entity", player -> player.getAbilities().instabuild)))

                        .then(compareDouble(root, Commands.literal("flyingSpeed"), positive, context -> getValueFromPlayer(context, "entity", player -> player.getAbilities().flyingSpeed)))
                        .then(compareDouble(root, Commands.literal("walkingSpeed"), positive, context -> getValueFromPlayer(context, "entity", player -> player.getAbilities().walkingSpeed)))
                        .then(compareDouble(root, Commands.literal("entityId"), positive, context -> getValueFromEntity(context, "entity", Entity::getId)))
                ));

        argumentBuilder.then(Commands.literal("block") // if the block matches the predicate
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .then(addConditional(root, Commands.argument("block", BlockPredicateArgument.blockPredicate(commandRegistryAccess)), positive, context ->
                                BlockPredicateArgument.getBlockPredicate(context, "block").test(new BlockInWorld(context.getSource().getLevel(), BlockPosArgument.getLoadedBlockPos(context, "pos"), true))))));

        argumentBuilder.then(Commands.literal("biome") // if the biome matches the predicate
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .then(addConditional(root, Commands.argument("biome", ResourceOrTagArgument.resourceOrTag(commandRegistryAccess, Registries.BIOME)), positive, context ->
                                ResourceOrTagArgument.getResourceOrTag(context, "biome", Registries.BIOME).test(context.getSource().getLevel().getBiome(BlockPosArgument.getLoadedBlockPos(context, "pos")))))));

        argumentBuilder.then(Commands.literal("loaded") // if the pos is loaded
                .then(addConditional(root, Commands.argument("pos", BlockPosArgument.blockPos()), positive, commandContext ->
                        isChunkLoaded(commandContext.getSource().getLevel(), BlockPosArgument.getBlockPos(commandContext, "pos")))));

        argumentBuilder.then(Commands.literal("dimension") // if the dimension is the same as that of the source
                .then(addConditional(root, Commands.argument("dimension", DimensionArgument.dimension()), positive, context ->
                        DimensionArgument.getDimension(context, "dimension").equals(context.getSource().getLevel()))));

        argumentBuilder.then(Commands.literal("score") // check scores
                .then(Commands.argument("target", ScoreHolderArgument.scoreHolder())
                        .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)

                        .then(Commands.argument("targetObjective", ObjectiveArgument.objective())
                                .then(Commands.literal("=")
                                        .then(Commands.argument("source", ScoreHolderArgument.scoreHolder())
                                                .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                                .then(addConditional(root, Commands.argument("sourceObjective", ObjectiveArgument.objective()), positive, context ->
                                                        checkScore(context, Integer::equals)))))

                                .then(Commands.literal("<")
                                        .then(Commands.argument("source", ScoreHolderArgument.scoreHolder())
                                                .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                                .then(addConditional(root, Commands.argument("sourceObjective", ObjectiveArgument.objective()), positive, context ->
                                                        checkScore(context, (a, b) -> a < b)))))

                                .then(Commands.literal("<=")
                                        .then(Commands.argument("source", ScoreHolderArgument.scoreHolder())
                                                .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                                .then(addConditional(root, Commands.argument("sourceObjective", ObjectiveArgument.objective()), positive, context ->
                                                        checkScore(context, (a, b) -> a <= b)))))

                                .then(Commands.literal(">")
                                        .then(Commands.argument("source", ScoreHolderArgument.scoreHolder())
                                                .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                                .then(addConditional(root, Commands.argument("sourceObjective", ObjectiveArgument.objective()), positive, context ->
                                                        checkScore(context, (a, b) -> a > b)))))

                                .then(Commands.literal(">=")
                                        .then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                                .then(addConditional(root, Commands.argument("sourceObjective", ObjectiveArgument.objective()), positive, context ->
                                                        checkScore(context, (a, b) -> a >= b)))))

                                .then(Commands.literal("matches")
                                        .then(addConditional(root, Commands.argument("range", RangeArgument.intRange()), positive, context ->
                                                checkScore(context, RangeArgument.Ints.getRange(context, "range")))))
                        )
                )
        );

        argumentBuilder.then(Commands.literal("blocks")
                .then(Commands.argument("start", BlockPosArgument.blockPos())
                        .then(Commands.argument("end", BlockPosArgument.blockPos())
                                .then((Commands.argument("destination", BlockPosArgument.blockPos())
                                        .then(addIfBlocksConditional(root, Commands.literal("all"), positive, false)))
                                        .then(addIfBlocksConditional(root, Commands.literal("masked"), positive, true))))));

        argumentBuilder.then(Commands.literal("entity")
                .then((Commands.argument("entities", EntityArgument.entities())
                        .fork(root, context -> expect(context, positive, !EntityArgument.getOptionalEntities(context, "entities").isEmpty())))
                        .executes(createNumericConditionalHandler(positive, context ->
                                EntityArgument.getOptionalEntities(context, "entities").size()))));

        argumentBuilder.then(Commands.literal("predicate")
                .then(addConditional(root, Commands.argument("predicate", ResourceLocationArgument.id())
                        .suggests(SUGGEST_PREDICATE), positive, context ->
                        checkCustomPredicate(context.getSource(), ResourceLocationArgument.getPredicate(context, "predicate")))));

        DataCommands.SOURCE_PROVIDERS.forEach(dataProvider ->
                argumentBuilder.then(dataProvider.wrap(Commands.literal("data"), builder ->
                        builder.then((Commands.argument("path", NbtPathArgument.nbtPath())
                                .fork(root, context -> expect(context, positive, checkMatchingData(dataProvider.access(context), NbtPathArgument.getPath(context, "path")) > 0)))
                                .executes(createNumericConditionalHandler(positive, context ->
                                        checkMatchingData(dataProvider.access(context), NbtPathArgument.getPath(context, "path"))))))));

        return argumentBuilder;
    }

    private ArgumentBuilder<CommandSourceStack, ?> compareDouble(CommandNode<CommandSourceStack> root, @NotNull LiteralArgumentBuilder<CommandSourceStack> argumentBuilder, boolean positive, CommandDoublePredicate compare) {
        return argumentBuilder.then(Commands.literal("=")
                        .then(addConditional(root, Commands.argument("compareDouble", DoubleArgumentType.doubleArg()), positive, context ->
                                compare.test(context) == DoubleArgumentType.getDouble(context, "compareDouble"))))

                .then(Commands.literal("<")
                        .then(addConditional(root, Commands.argument("compareDouble", DoubleArgumentType.doubleArg()), positive, context ->
                                compare.test(context) < DoubleArgumentType.getDouble(context, "compareDouble"))))

                .then(Commands.literal("<=")
                        .then(addConditional(root, Commands.argument("compareDouble", DoubleArgumentType.doubleArg()), positive, context ->
                                compare.test(context) <= DoubleArgumentType.getDouble(context, "compareDouble"))))

                .then(Commands.literal(">")
                        .then(addConditional(root, Commands.argument("compareDouble", DoubleArgumentType.doubleArg()), positive, context ->
                                compare.test(context) > DoubleArgumentType.getDouble(context, "compareDouble"))))

                .then(Commands.literal(">=")
                        .then(addConditional(root, Commands.argument("compareDouble", DoubleArgumentType.doubleArg()), positive, context ->
                                compare.test(context) >= DoubleArgumentType.getDouble(context, "compareDouble"))))

                .then(Commands.literal("matches")
                        .then(addConditional(root, Commands.argument("range", RangeArgument.intRange()), positive, context ->
                                RangeArgument.Ints.getRange(context, "range").matches(((int) compare.test(context))))));
    }

    @SuppressWarnings("unused")
    private ArgumentBuilder<CommandSourceStack, ?> compareDouble(CommandNode<CommandSourceStack> root, LiteralArgumentBuilder<CommandSourceStack> argumentBuilder, boolean positive, double compare) {
        return compareDouble(root, argumentBuilder, positive, context -> compare);
    }


    @Contract(pure = true)
    private @NotNull Command<CommandSourceStack> createNumericConditionalHandler(boolean positive, CommandNumericPredicate condition) {
        return positive ? context -> {
            final int count = condition.test(context);
            if (count < 0) throw ERROR_CONDITIONAL_FAILED.create();

            EssentialsUtil.sendSourceSuccess(context.getSource(), Component.translatable("commands.execute.conditional.pass_count", Colors.SUCCESS, Component.text(count, Colors.VARIABLE_VALUE)));
            return count;

        } : context -> {
            final int count = condition.test(context);
            if (count != 0) throw ERROR_CONDITIONAL_FAILED_COUNT.create(count);

            EssentialsUtil.sendSourceSuccess(context.getSource(), Component.translatable("commands.execute.conditional.pass", Colors.SUCCESS));
            return 1;

        };
    }

    private int checkMatchingData(@NotNull DataAccessor dataAccessor, NbtPathArgument.@NotNull NbtPath path) throws CommandSyntaxException {
        return path.countMatching(dataAccessor.getData());
    }

    private boolean checkScore(CommandContext<CommandSourceStack> context, BiPredicate<Integer, Integer> condition) throws CommandSyntaxException {
        final var targetName = ScoreHolderArgument.getName(context, "target");
        final var targetObjective = ObjectiveArgument.getObjective(context, "targetObjective");
        final var sourceName = ScoreHolderArgument.getName(context, "source");
        final var sourceObjective = ObjectiveArgument.getObjective(context, "sourceObjective");
        final var scoreboard = context.getSource().getServer().getScoreboard();

        if (scoreboard.hasPlayerScore(targetName, targetObjective) && scoreboard.hasPlayerScore(sourceName, sourceObjective)) {
            final var targetScore = scoreboard.getOrCreatePlayerScore(targetName, targetObjective);
            final var sourceScore = scoreboard.getOrCreatePlayerScore(sourceName, sourceObjective);

            return condition.test(targetScore.getScore(), sourceScore.getScore());
        }

        return false;
    }

    private boolean checkScore(CommandContext<CommandSourceStack> context, MinMaxBounds.Ints range) throws CommandSyntaxException {
        final var targetName = ScoreHolderArgument.getName(context, "target");
        final var targetObjective = ObjectiveArgument.getObjective(context, "targetObjective");
        final var scoreboard = context.getSource().getServer().getScoreboard();

        if (!scoreboard.hasPlayerScore(targetName, targetObjective)) return false;

        final var targetScore = scoreboard.getOrCreatePlayerScore(targetName, targetObjective);
        return range.matches(targetScore.getScore());
    }

    private boolean checkCustomPredicate(@NotNull CommandSourceStack source, @NotNull LootItemCondition condition) {
        ServerLevel serverLevel = source.getLevel();
        LootParams lootParams = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, source.getPosition())
                .withOptionalParameter(LootContextParams.THIS_ENTITY, source.getEntity())
                .create(LootContextParamSets.COMMAND);
        LootContext lootContext = new LootContext.Builder(lootParams)
                .create(null);
        lootContext.pushVisitedElement(LootContext.createVisitedEntry(condition));

        return condition.test(lootContext);
    }

    private Collection<CommandSourceStack> expect(CommandContext<CommandSourceStack> context, boolean positive, boolean value) {
        return value == positive ? Collections.singleton(context.getSource()) : Collections.emptyList();
    }

    private ArgumentBuilder<CommandSourceStack, ?> addConditional(CommandNode<CommandSourceStack> root, @NotNull ArgumentBuilder<CommandSourceStack, ?> builder, boolean positive, CommandPredicate condition) {
        return builder.fork(root, context -> expect(context, positive, condition.test(context)))
                .executes(context -> {
                    if (positive != condition.test(context)) throw ERROR_CONDITIONAL_FAILED.create();

                    EssentialsUtil.sendSourceSuccess(context.getSource(), Component.translatable("commands.execute.conditional.pass", Colors.SUCCESS));
                    return 1;
                });
    }

    private ArgumentBuilder<CommandSourceStack, ?> addIfBlocksConditional(CommandNode<CommandSourceStack> root, @NotNull ArgumentBuilder<CommandSourceStack, ?> builder, boolean positive, boolean masked) {
        return builder.fork(root, (context) -> expect(context, positive, checkRegions(context, masked).isPresent()))
                .executes(positive ? (context) -> checkIfRegions(context, masked) : (context) -> checkUnlessRegions(context, masked));
    }

    private int checkIfRegions(CommandContext<CommandSourceStack> context, boolean masked) throws CommandSyntaxException {
        final var optionalInt = checkRegions(context, masked)
                .orElseThrow(ERROR_CONDITIONAL_FAILED::create);

        EssentialsUtil.sendSourceSuccess(context.getSource(), Component.translatable("commands.execute.conditional.pass_count", Colors.SUCCESS, Component.text(optionalInt, Colors.VARIABLE_VALUE)));
        return optionalInt;
    }

    private int checkUnlessRegions(CommandContext<CommandSourceStack> context, boolean masked) throws CommandSyntaxException {
        final var optionalInt = checkRegions(context, masked);
        if (optionalInt.isPresent()) throw ERROR_CONDITIONAL_FAILED_COUNT.create(optionalInt.getAsInt());

        EssentialsUtil.sendSourceSuccess(context.getSource(), Component.translatable("commands.execute.conditional.pass", Colors.SUCCESS));
        return 1;
    }


    private @NotNull OptionalInt checkRegions(@NotNull CommandContext<CommandSourceStack> context, boolean masked) throws CommandSyntaxException {
        return checkRegions(
                context.getSource().getLevel(),
                BlockPosArgument.getLoadedBlockPos(context, "start"),
                BlockPosArgument.getLoadedBlockPos(context, "end"),
                BlockPosArgument.getLoadedBlockPos(context, "destination"),
                masked
        );
    }

    private OptionalInt checkRegions(ServerLevel world, BlockPos start, BlockPos end, BlockPos destination, boolean masked) throws CommandSyntaxException {
        final var sourceBoundingBox = BoundingBox.fromCorners(start, end);
        final var destinationBoundingBox = BoundingBox.fromCorners(destination, destination.offset(sourceBoundingBox.getLength()));
        final var offset = new BlockPos(
                destinationBoundingBox.minX() - sourceBoundingBox.minX(),
                destinationBoundingBox.minY() - sourceBoundingBox.minY(),
                destinationBoundingBox.minZ() - sourceBoundingBox.minZ()
        );
        final int totalBlocks = sourceBoundingBox.getXSpan() * sourceBoundingBox.getYSpan() * sourceBoundingBox.getZSpan();


        if (totalBlocks > MAX_TEST_AREA) throw ERROR_AREA_TOO_LARGE.create(MAX_TEST_AREA, totalBlocks);

        int matchingBlocks = 0;

        for (int z = sourceBoundingBox.minZ(); z <= sourceBoundingBox.maxZ(); ++z) {
            for (int y = sourceBoundingBox.minY(); y <= sourceBoundingBox.maxY(); ++y) {
                for (int x = sourceBoundingBox.minX(); x <= sourceBoundingBox.maxX(); ++x) {

                    final var sourcePos = new BlockPos(x, y, z);
                    final var destinationPos = sourcePos.offset(offset);
                    final var sourceBlockState = world.getBlockState(sourcePos);

                    if (!masked || !sourceBlockState.is(Blocks.AIR)) {
                        if (sourceBlockState != world.getBlockState(destinationPos)) return OptionalInt.empty();

                        final var sourceBlockEntity = world.getBlockEntity(sourcePos);
                        final var destinationBlockEntity = world.getBlockEntity(destinationPos);

                        if (sourceBlockEntity != null) {
                            if (destinationBlockEntity == null) return OptionalInt.empty();
                            if (destinationBlockEntity.getType() != sourceBlockEntity.getType())
                                return OptionalInt.empty();

                            final var sourceBlockTag = sourceBlockEntity.saveWithoutMetadata();
                            final var destinationBlockTag = destinationBlockEntity.saveWithoutMetadata();

                            if (!sourceBlockTag.equals(destinationBlockTag)) return OptionalInt.empty();
                        }

                        ++matchingBlocks;
                    }
                }
            }
        }

        return OptionalInt.of(matchingBlocks);
    }

    @Contract(pure = true)
    private @NotNull RedirectModifier<CommandSourceStack> expandOneToOneEntityRelation(Function<Entity, Optional<Entity>> function) {
        return (context) -> {
            final var source = context.getSource();
            final var sourceEntity = source.getEntity();
            return sourceEntity == null ? List.of() :
                    function.apply(sourceEntity)
                            .filter(targetEntity -> !targetEntity.isRemoved())
                            .map(targetEntity -> List.of(source.withEntity(targetEntity)))
                            .orElse(List.of());
        };
    }

    @Contract(pure = true)
    private @NotNull RedirectModifier<CommandSourceStack> expandOneToManyEntityRelation(Function<Entity, Stream<Entity>> relationFunction) {
        return (context) -> {
            final var source = context.getSource();
            final var entity = source.getEntity();

            return entity == null ? List.of() :
                    relationFunction.apply(entity)
                            .filter(relatedEntity -> !relatedEntity.isRemoved())
                            .map(source::withEntity)
                            .toList();
        };
    }

    private LiteralArgumentBuilder<CommandSourceStack> createRelationOperations(CommandNode<CommandSourceStack> node, @NotNull LiteralArgumentBuilder<CommandSourceStack> builder) {
        return builder
                .then(Commands.literal("owner")
                        .fork(node, expandOneToOneEntityRelation((entity) ->
                                (entity instanceof OwnableEntity ownableEntity) ? Optional.ofNullable(ownableEntity.getOwner()) : Optional.empty())))

                .then(Commands.literal("leasher")
                        .fork(node, expandOneToOneEntityRelation((entity) ->
                                (entity instanceof Mob mob) ? Optional.ofNullable(mob.getLeashHolder()) : Optional.empty())))

                .then(Commands.literal("target")
                        .fork(node, expandOneToOneEntityRelation((entity) ->
                                (entity instanceof Targeting targeting) ? Optional.ofNullable(targeting.getTarget()) : Optional.empty())))

                .then(Commands.literal("attacker")
                        .fork(node, expandOneToOneEntityRelation((entity) ->
                                (entity instanceof Attackable attackable) ? Optional.ofNullable(attackable.getLastAttacker()) : Optional.empty())))

                .then(Commands.literal("vehicle")
                        .fork(node, expandOneToOneEntityRelation((entity) ->
                                Optional.ofNullable(entity.getVehicle()))))

                .then(Commands.literal("controller")
                        .fork(node, expandOneToOneEntityRelation((entity) ->
                                Optional.ofNullable(entity.getControllingPassenger()))))

                .then(Commands.literal("origin")
                        .fork(node, expandOneToOneEntityRelation((entity) ->
                                (entity instanceof TraceableEntity traceableEntity) ? Optional.ofNullable(traceableEntity.getOwner()) : Optional.empty())))

                .then(Commands.literal("passengers")
                        .fork(node, expandOneToManyEntityRelation((entity) ->
                                entity.getPassengers().stream())));
    }

    private @NotNull CommandSourceStack spawnEntityAndRedirect(@NotNull CommandSourceStack source, Holder.Reference<EntityType<?>> entityType) throws CommandSyntaxException {
        return source.withEntity(SummonCommand.createEntity(source, entityType, source.getPosition(), new CompoundTag(), true));
    }

    @FunctionalInterface
    private interface CommandPredicate {
        boolean test(CommandContext<CommandSourceStack> context) throws CommandSyntaxException;
    }

    @FunctionalInterface
    private interface CommandNumericPredicate {
        int test(CommandContext<CommandSourceStack> context) throws CommandSyntaxException;
    }

    @FunctionalInterface
    private interface CommandDoublePredicate {
        double test(CommandContext<CommandSourceStack> context) throws CommandSyntaxException;
    }

    @FunctionalInterface
    private interface EntityCommandNumberPredicate {
        double test(Entity entity);
    }

    @FunctionalInterface
    private interface PlayerCommandNumberPredicate {
        double test(ServerPlayer player);
    }


    private static double getNumber(Object from) {
        return ((Number) from).doubleValue();
    }

    @SuppressWarnings("SameParameterValue")
    private static boolean checkIfPlayer(CommandContext<CommandSourceStack> context, String name, Predicate<ServerPlayer> playerPredicate) throws CommandSyntaxException {
        return getEntity(context, name) instanceof ServerPlayer player && playerPredicate.test(player);
    }

    @SuppressWarnings("SameParameterValue")
    private static boolean checkEntity(CommandContext<CommandSourceStack> context, String name, @NotNull Predicate<Entity> entityPredicate) throws CommandSyntaxException {
        return entityPredicate.test(getEntity(context, name));
    }

    @SuppressWarnings("SameParameterValue")
    private static double getValueFromEntity(CommandContext<CommandSourceStack> context, String name, @NotNull EntityCommandNumberPredicate predicate) throws CommandSyntaxException {
        return predicate.test(EntityArgument.getEntity(context, name));
    }

    @SuppressWarnings("SameParameterValue")
    private static double getValueFromPlayer(CommandContext<CommandSourceStack> context, String name, @NotNull PlayerCommandNumberPredicate predicate) throws CommandSyntaxException {
        return predicate.test(EntityArgument.getPlayer(context, name));
    }


    private static @NotNull Entity getEntity(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        return EntityArgument.getEntity(context, name);
    }


    static {
        ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((maxCount, count) ->
                PaperAdventure.asVanilla(Component.translatable(
                        "commands.execute.blocks.toobig",
                        Colors.ERROR,
                        Component.text(getNumber(maxCount), Colors.VARIABLE_VALUE),
                        Component.text(getNumber(count), Colors.VARIABLE_VALUE))
                ));

        ERROR_CONDITIONAL_FAILED = new SimpleCommandExceptionType(PaperAdventure.asVanilla(Component.translatable("commands.execute.conditional.fail", Colors.ERROR)));

        ERROR_CONDITIONAL_FAILED_COUNT = new DynamicCommandExceptionType(count ->
                PaperAdventure.asVanilla(Component.translatable(
                        "commands.execute.conditional.fail_count",
                        Colors.ERROR,
                        Component.text(getNumber(count), Colors.VARIABLE_VALUE))
                ));

        CALLBACK_CHAINER = (consumer, consumer2) -> (context, success, result) -> {
            consumer.onCommandComplete(context, success, result);
            consumer2.onCommandComplete(context, success, result);
        };

        SUGGEST_PREDICATE = (context, builder) -> SharedSuggestionProvider.suggestResource(context.getSource().getServer().getLootData().getKeys(LootDataType.PREDICATE), builder);
    }
}
