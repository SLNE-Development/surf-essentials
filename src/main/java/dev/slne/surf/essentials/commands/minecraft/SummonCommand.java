package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.CreatureSpawnEvent;

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

        literal.then(Commands.argument("entity", ResourceArgument.resource(EssentialsUtil.buildContext(), Registries.ENTITY_TYPE))
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

    private int summon(CommandSourceStack source, Holder.Reference<EntityType<?>> entityType, Vec3 pos, CompoundTag compoundTag, boolean initialize)throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos((int) pos.x(), (int) pos.y(), (int) pos.z());

        if (!Level.isInSpawnableBounds(blockPos)) throw INVALID_POSITION.create();

        CompoundTag compoundTag1 = compoundTag.copy();

        compoundTag1.putString("id", entityType.key().location().toString());

        ServerLevel serverLevel = source.getLevel();
        Entity entity = EntityType.loadEntityRecursive(compoundTag1, serverLevel, entity1 -> {
            entity1.moveTo(pos.x(), pos.y(), pos.z(), entity1.getYRot(), entity1.getXRot());
            return entity1;
        });

        if (entity == null) throw  ERROR_FAILED.create();

        if (initialize && entity instanceof Mob){
            ((Mob) entity).finalizeSpawn(source.getLevel(), source.getLevel().getCurrentDifficultyAt(entity.blockPosition()),
                    MobSpawnType.COMMAND, null, null);
        }

        if (!serverLevel.tryAddFreshEntityWithPassengers(entity, CreatureSpawnEvent.SpawnReason.COMMAND)) throw  ERROR_DUPLICATE_UUID.create();

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, PaperAdventure.asAdventure(entity.getDisplayName()).colorIfAbsent(Colors.TERTIARY)
                    .append(net.kyori.adventure.text.Component.text(" wurde gespawnt.", Colors.SUCCESS)));
        }else {
            source.sendSuccess(Component.translatable("commands.summon.success", entity.getDisplayName()), false);
        }

        return 1;
    }

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed"));
    private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed.uuid"));
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.summon.invalidPosition"));
}
