package dev.slne.surf.essentials.commands.minecraft;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.color.EffectColors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.event.entity.EntityPotionEffectEvent;

import javax.annotation.Nullable;
import java.util.Collection;

public class EffectCommand {
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("effect", EffectCommand::literal).setUsage("/effect <give | clear>")
                .setDescription("Give or clear the effects of the entities");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.EFFECT_PERMISSION));

        literal.then(Commands.literal("give")
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("effect", ResourceArgument.resource(EssentialsUtil.buildContext(), Registries.MOB_EFFECT))
                                .executes(context -> giveEffect(context.getSource(), EntityArgument.getEntities(context, "targets"),
                                        ResourceArgument.getMobEffect(context, "effect"), null, 0, true))

                                .then(Commands.argument("duration", IntegerArgumentType.integer(1, 1000000))
                                        .executes(context -> giveEffect(context.getSource(), EntityArgument.getEntities(context, "targets"),
                                                ResourceArgument.getMobEffect(context, "effect"), IntegerArgumentType.getInteger(context, "duration"), 0, true))
                                        .then(amplifierAndParticleBuilder(false)))

                                .then(Commands.literal("infinitive")
                                        .executes(context -> giveEffect(context.getSource(), EntityArgument.getEntities(context, "targets"),
                                                ResourceArgument.getMobEffect(context, "effect"), -1, 0, true))
                                        .then(amplifierAndParticleBuilder(true))))));

        literal.then(Commands.literal("clear")
                .executes(context -> clearAllEffects(context.getSource(), ImmutableList.of(context.getSource().getEntityOrException())))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .executes(context -> clearAllEffects(context.getSource(), EntityArgument.getEntities(context, "targets")))
                        .then(Commands.argument("effect", ResourceArgument.resource(EssentialsUtil.buildContext(), Registries.MOB_EFFECT))
                                .executes(context -> clearSingleEffect(context.getSource(), EntityArgument.getEntities(context, "targets"), ResourceArgument.getMobEffect(context, "effect"))))));
    }

    private static int giveEffect(CommandSourceStack source, Collection<? extends Entity> targetsUnchecked, Holder<MobEffect> statusEffect, @Nullable Integer seconds, int amplifier, boolean showParticles) throws CommandSyntaxException{
        Collection<? extends Entity> targets = EssentialsUtil.checkEntitySuggestion(source, targetsUnchecked);
        MobEffect mobEffectList = statusEffect.value();
        int successfulApplies = 0;
        int durationInTicks = calculateDurationInTicks(mobEffectList, seconds);

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity livingEntity) {
                MobEffectInstance mobEffect = new MobEffectInstance(mobEffectList, durationInTicks, amplifier, false, showParticles);
                if (livingEntity.addEffect(mobEffect, source.getEntity(), EntityPotionEffectEvent.Cause.COMMAND)) {
                    ++successfulApplies;
                }
            }
        }

        if (successfulApplies == 0) {
            if (source.isPlayer()){
                EssentialsUtil.sendError(source, "Die Ziele sind entweder Immun gegen Effekte oder haben bereits einen stärkeren");
            }else {
                throw ERROR_GIVE_FAILED.create();
            }
        } else {
            if (targets.size() == 1) {
                if (source.isPlayer()){
                    EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Der Effect ", Colors.SUCCESS))
                            .append(net.kyori.adventure.text.Component.text("[%s]".formatted(mobEffectList.getDisplayName().getString()), EffectColors.getEffectColor(mobEffectList))
                                    .hoverEvent(HoverEvent.showText(net.kyori.adventure.text.Component.text("Zeit: ", Colors.INFO)
                                            .append(net.kyori.adventure.text.Component.text(EssentialsUtil.ticksToString(durationInTicks), Colors.TERTIARY)))))
                            .append(net.kyori.adventure.text.Component.text(" wurde ", Colors.SUCCESS))
                            .append(PaperAdventure.asAdventure(targets.iterator().next().getDisplayName()).colorIfAbsent(Colors.TERTIARY))
                            .append(net.kyori.adventure.text.Component.text(" gegeben!", Colors.SUCCESS)));
                }else {
                    source.sendSuccess(Component.translatable("commands.effect.give.success.single", mobEffectList.getDisplayName(), targets.iterator().next().getDisplayName(), durationInTicks / 20), false);
                }

            } else {
                if (source.isPlayer()){
                    EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Der Effect ", Colors.SUCCESS))
                            .append(net.kyori.adventure.text.Component.text("[%s]".formatted(mobEffectList.getDisplayName().getString()), EffectColors.getEffectColor(mobEffectList))
                                    .hoverEvent(HoverEvent.showText(net.kyori.adventure.text.Component.text("Zeit: ", Colors.INFO)
                                            .append(net.kyori.adventure.text.Component.text(EssentialsUtil.ticksToString(durationInTicks), Colors.TERTIARY)))))
                            .append(net.kyori.adventure.text.Component.text(" wurde ", Colors.SUCCESS))
                            .append(net.kyori.adventure.text.Component.text(targets.size(), Colors.TERTIARY))
                            .append(net.kyori.adventure.text.Component.text(" Entities gegeben!", Colors.SUCCESS)));
                }else {
                    source.sendSuccess(Component.translatable("commands.effect.give.success.multiple", mobEffectList.getDisplayName(), targets.size(), durationInTicks / 20), false);
                }
            }
            return successfulApplies;
        }
        return 0;
    }

    private static int clearSingleEffect(CommandSourceStack source, Collection<? extends Entity> targetsUnchecked, Holder<MobEffect> statusEffect)throws CommandSyntaxException{
        Collection<? extends Entity> targets = EssentialsUtil.checkEntitySuggestion(source, targetsUnchecked);
        MobEffect mobEffectList = statusEffect.value();
        int successfullyRemoves = 0;

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity && ((LivingEntity) entity).removeEffect(mobEffectList, EntityPotionEffectEvent.Cause.COMMAND)) {
                ++successfullyRemoves;
            }
        }

        if (successfullyRemoves == 0) {
            if (source.isPlayer()) {
                EssentialsUtil.sendError(source, "Die Ziele haben diesen Effekt nicht!");
            } else {
                throw ERROR_CLEAR_SPECIFIC_FAILED.create();
            }
        } else {
            if (targets.size() == 1) {
                if (source.isPlayer()){
                    EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Der Effect ", Colors.SUCCESS))
                            .append(net.kyori.adventure.text.Component.text("[%s]".formatted(mobEffectList.getDisplayName().getString()), EffectColors.getEffectColor(mobEffectList)))
                            .append(net.kyori.adventure.text.Component.text(" wurde von ", Colors.SUCCESS))
                            .append(PaperAdventure.asAdventure(targets.iterator().next().getDisplayName()).colorIfAbsent(Colors.TERTIARY))
                            .append(net.kyori.adventure.text.Component.text(" entfernt!", Colors.SUCCESS)));
                }else {
                    source.sendSuccess(Component.translatable("commands.effect.clear.specific.success.single", mobEffectList.getDisplayName(), targets.iterator().next().getDisplayName()), false);
                }

            } else {
                if (source.isPlayer()){
                    EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Der Effect ", Colors.SUCCESS))
                            .append(net.kyori.adventure.text.Component.text("[%s]".formatted(mobEffectList.getDisplayName().getString()), EffectColors.getEffectColor(mobEffectList)))
                            .append(net.kyori.adventure.text.Component.text(" wurde von ", Colors.SUCCESS))
                            .append(net.kyori.adventure.text.Component.text(targets.size(), Colors.TERTIARY))
                            .append(net.kyori.adventure.text.Component.text(" Entities entfernt!", Colors.SUCCESS)));
                }else {
                    source.sendSuccess(Component.translatable("commands.effect.clear.specific.success.multiple", mobEffectList.getDisplayName(), targets.size()), false);
                }
            }
        }
        return successfullyRemoves;
    }

    private static int clearAllEffects(CommandSourceStack source, Collection<? extends Entity> targetsUnchecked) throws CommandSyntaxException{
        Collection<? extends Entity> targets = EssentialsUtil.checkEntitySuggestion(source, targetsUnchecked);
        int successfullyRemoves = 0;

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity && ((LivingEntity) entity).removeAllEffects(EntityPotionEffectEvent.Cause.COMMAND)) {
                ++successfullyRemoves;
            }
        }

        if (successfullyRemoves == 0) {
            if (source.isPlayer()){
                EssentialsUtil.sendError(source, "Die Ziele besitzen keine Effekte");
            }else {
                throw ERROR_CLEAR_EVERYTHING_FAILED.create();
            }
        } else {
            if (targets.size() == 1) {
                if (source.isPlayer()){
                    EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Es wurden alle Effekte von ", Colors.SUCCESS)
                            .append(PaperAdventure.asAdventure(targets.iterator().next().getDisplayName()).colorIfAbsent(Colors.TERTIARY))
                            .append(net.kyori.adventure.text.Component.text(" entfernt!", Colors.SUCCESS)));
                }else {
                    source.sendSuccess(Component.translatable("commands.effect.clear.everything.success.single", targets.iterator().next().getDisplayName()), false);
                }
            } else {
                if (source.isPlayer()){
                    EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Es wurden alle Effekte von ", Colors.SUCCESS)
                            .append(net.kyori.adventure.text.Component.text(targets.size(), Colors.TERTIARY))
                            .append(net.kyori.adventure.text.Component.text(" Entities entfernt!", Colors.SUCCESS)));
                }else {
                    source.sendSuccess(Component.translatable("commands.effect.clear.everything.success.multiple", targets.size()), false);
                }
            }
        }
        return successfullyRemoves;
    }

    private static int calculateDurationInTicks(MobEffect mobEffect, @Nullable Integer durationSeconds) {
        int ticks;
        if (durationSeconds != null) {
            if (mobEffect.isInstantenous()) {
                ticks = durationSeconds;
            } else if (durationSeconds == -1) {
                ticks = -1;
            } else {
                ticks = durationSeconds * 20;
            }
        } else if (mobEffect.isInstantenous()) {
            ticks = 1;
        } else {
            ticks = 600;
        }
        return ticks;
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Integer> amplifierAndParticleBuilder(boolean isInfinitive){
        return Commands.argument("amplifier", IntegerArgumentType.integer(0, 255))
                .executes(context -> giveEffect(context.getSource(), EntityArgument.getEntities(context, "targets"),
                        ResourceArgument.getMobEffect(context, "effect"), isInfinitive ? -1 : IntegerArgumentType.getInteger(context, "duration"),
                        IntegerArgumentType.getInteger(context, "amplifier"), true))

                .then(Commands.argument("hideParticles", BoolArgumentType.bool())
                        .executes(context -> giveEffect(context.getSource(), EntityArgument.getEntities(context, "targets"),
                                ResourceArgument.getMobEffect(context, "effect"), isInfinitive ? -1 : IntegerArgumentType.getInteger(context, "duration"),
                                IntegerArgumentType.getInteger(context, "amplifier"), !BoolArgumentType.getBool(context, "hideParticles"))));
    }

    private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.effect.give.failed"));
    private static final SimpleCommandExceptionType ERROR_CLEAR_EVERYTHING_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.effect.clear.everything.failed"));
    private static final SimpleCommandExceptionType ERROR_CLEAR_SPECIFIC_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.effect.clear.specific.failed"));
}
