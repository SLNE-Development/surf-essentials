package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

public class SummonCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"summon"};
    }

    @Override
    public String usage() {
        return "/summon <entity> [<pos>] [<nbt]";
    }

    @Override
    public String description() {
        return "summon an entity";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.SUMMON_PERMISSION));

        literal.then(Commands.argument("entity", ResourceArgument.resource(this.commandBuildContext, Registries.ENTITY_TYPE))
                .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                .executes(context -> summon(context.getSource(), ResourceArgument.getSummonableEntityType(context, "entity"),
                        context.getSource().getPosition(), new CompoundTag(), true))
                .then(Commands.argument("pos", Vec3Argument.vec3(true))
                        .executes(context -> summon(context.getSource(), ResourceArgument.getSummonableEntityType(context, "entity"),
                                Vec3Argument.getVec3(context, "pos"), new CompoundTag(), true))
                        .then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
                                .executes(context -> summon(context.getSource(), ResourceArgument.getSummonableEntityType(context, "entity"),
                                        Vec3Argument.getVec3(context, "pos"), CompoundTagArgument.getCompoundTag(context, "nbt"), false)))));
    }

    private int summon(CommandSourceStack source, Holder.Reference<EntityType<?>> entityType, Vec3 pos, CompoundTag compoundTag, boolean initialize) throws CommandSyntaxException {
        final var entity = createEntity(source, entityType, pos, compoundTag, initialize);

        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(entity)
                .append(net.kyori.adventure.text.Component.text(" wurde gespawnt.", Colors.SUCCESS)));

        return 1;
    }

    public static @NotNull Entity createEntity(@NotNull CommandSourceStack source, Holder.Reference<EntityType<?>> entityType, Vec3 pos, @NotNull CompoundTag nbt, boolean initialize) throws CommandSyntaxException {
        final var blockPos = BlockPos.containing(pos);
        final var level = source.getLevel();
        final var modifiedTag = nbt.copy();

        if (!Level.isInSpawnableBounds(blockPos)) throw INVALID_POSITION.create();

        modifiedTag.putString("id", entityType.key().location().toString());

        final var entity = EntityType.loadEntityRecursive(modifiedTag, level, loadedEntity -> {
            loadedEntity.moveTo(pos.x(), pos.y(), pos.z(), loadedEntity.getYRot(), loadedEntity.getXRot());
            return loadedEntity;
        });

        if (entity == null) throw ERROR_FAILED.create();

        if (initialize && entity instanceof Mob mob) {
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, null, null);
        }

        if (!level.tryAddFreshEntityWithPassengers(entity, CreatureSpawnEvent.SpawnReason.COMMAND))
            throw ERROR_DUPLICATE_UUID.create();

        return entity;
    }

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed"));
    private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed.uuid"));
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.summon.invalidPosition"));
}
