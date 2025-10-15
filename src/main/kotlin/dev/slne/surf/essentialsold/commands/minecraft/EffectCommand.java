package dev.slne.surf.essentialsold.commands.minecraft;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.jorel.commandapi.executors.ResultingCommandExecutor;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.brigadier.Exceptions;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EffectCommand extends EssentialsCommand {
    private static final String ARG_TARGETS = "targets";
    private static final String ARG_EFFECT = "effect";
    private static final String ARG_DURATION = "duration";
    private static final String ARG_AMPLIFIER = "amplifier";
    private static final String ARG_HIDE_PARTICLES = "hideParticles";

    public EffectCommand() {
        super("effect", "effect <clear [<targets [<effect>]>] | give <targets> <effect> [<infinitive | duration >] [<amplifier [<hideParticles>]>]>", "Add or remove status effects on players and other entities");

        withPermission(Permissions.EFFECT_PERMISSION);

        then(literal("give")
                .then(entitiesArgument(ARG_TARGETS)
                        .then(potionEffectArgument(ARG_EFFECT)
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> giveEffect(
                                        sender.getCallee(),
                                        args.getUnchecked(ARG_TARGETS),
                                        Objects.requireNonNull(args.getUnchecked(ARG_EFFECT)),
                                        Optional.empty(),
                                        Optional.empty(),
                                        Optional.empty()
                                ))
                                .then(timeArgument(ARG_DURATION)
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> giveEffect(
                                                sender.getCallee(),
                                                args.getUnchecked(ARG_TARGETS),
                                                Objects.requireNonNull(args.getUnchecked(ARG_EFFECT)),
                                                Optional.ofNullable(args.getUnchecked(ARG_DURATION)),
                                                Optional.empty(),
                                                Optional.empty()
                                        ))
                                        .then(amplifierAndParticleBuilder(false))
                                )
                                .then(literal("infinitive")
                                        .executes((ResultingCommandExecutor) (sender, args) -> giveEffect(
                                                sender,
                                                args.getUnchecked(ARG_TARGETS),
                                                Objects.requireNonNull(args.getUnchecked(ARG_EFFECT)),
                                                Optional.of(-1),
                                                Optional.empty(),
                                                Optional.empty()
                                        ))
                                        .then(amplifierAndParticleBuilder(true))
                                )
                        )
                )
        );

        then(literal("clear")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> clearAllEffects(
                        sender.getCallee(),
                        List.of(getSpecialEntityOrException(sender, LivingEntity.class))
                ))
                .then(entitiesArgument(ARG_TARGETS)
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> clearAllEffects(
                                sender.getCallee(),
                                args.getUnchecked(ARG_TARGETS)
                        ))
                        .then(potionEffectArgument(ARG_EFFECT)
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> clearSingleEffect(
                                        sender.getCallee(),
                                        args.getUnchecked(ARG_TARGETS),
                                        Objects.requireNonNull(args.getUnchecked(ARG_EFFECT))
                                ))
                        )
                )
        );
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private int giveEffect(CommandSender source, Collection<Entity> targetsUnchecked, PotionEffectType effect, Optional<Integer> ticks, Optional<Integer> amplifier, Optional<Boolean> hideParticles) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkEntitySuggestion(source, targetsUnchecked);
        val potionEffect = effect
                .createEffect(ticks.orElse(600), amplifier.orElse(0))
                .withParticles(!hideParticles.orElse(true))
                .withAmbient(false);

        int successfulApplies = 0;


        for (Entity entity : targets) {
            if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity.addPotionEffect(potionEffect)) {
                    ++successfulApplies;
                }
            }
        }

        if (successfulApplies == 0) throw Exceptions.ERROR_EFFECT_GIVE_FAIL;

        EssentialsUtil.sendSuccess(source, Component.text("Der Effect ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(potionEffect))
                .append(Component.text(" wurde ", Colors.SUCCESS))
                .append(targets.size() == 1 ? EssentialsUtil.getDisplayName(targets.iterator().next()) : Component.text(targets.size(), Colors.TERTIARY).append(Component.text(" Entities ", Colors.SUCCESS)))
                .append(Component.text(" gegeben.", Colors.SUCCESS)));

        return successfulApplies;
    }

    private int clearSingleEffect(CommandSender source, Collection<Entity> targetsUnchecked, PotionEffectType effectType) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkEntitySuggestion(source, targetsUnchecked);
        int successfullyRemoves = 0;

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity livingEntity && livingEntity.hasPotionEffect(effectType)) {
                livingEntity.removePotionEffect(effectType);
                ++successfullyRemoves;
            }
        }

        if (successfullyRemoves == 0) throw Exceptions.ERROR_EFFECT_CLEAR_SPECIFIC_FAIL;

        EssentialsUtil.sendSuccess(source, (Component.text("Der Effect ", Colors.SUCCESS))
                .append(EssentialsUtil.getDisplayName(effectType))
                .append(Component.text(" wurde von ", Colors.SUCCESS))
                .append(targets.size() == 1 ? EssentialsUtil.getDisplayName(targets.iterator().next()) : Component.text(targets.size(), Colors.TERTIARY).append(Component.text(" Entities ", Colors.SUCCESS)))
                .append(Component.text(" entfernt.", Colors.SUCCESS)));

        return successfullyRemoves;
    }

    private int clearAllEffects(CommandSender source, Collection<Entity> targetsUnchecked) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkEntitySuggestion(source, targetsUnchecked);
        int successfullyRemoves = 0;

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity livingEntity && livingEntity.clearActivePotionEffects()) {
                ++successfullyRemoves;
            }
        }

        if (successfullyRemoves == 0) throw Exceptions.ERROR_EFFECT_CLEAR_EVERYTHING_FAIL;

        EssentialsUtil.sendSuccess(source, Component.text("Es wurden alle Effekte von ", Colors.SUCCESS)
                .append(targets.size() == 1 ? EssentialsUtil.getDisplayName(targets.iterator().next()) : Component.text(targets.size(), Colors.TERTIARY).append(Component.text(" Entities ", Colors.SUCCESS)))
                .append(Component.text(" entfernt.", Colors.SUCCESS)));


        return successfullyRemoves;
    }

    private Argument<?> amplifierAndParticleBuilder(boolean isInfinitive) {
        return integerArgument(ARG_AMPLIFIER, 0, 255)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> giveEffect(
                        sender.getCallee(),
                        args.getUnchecked(ARG_TARGETS),
                        Objects.requireNonNull(args.getUnchecked(ARG_EFFECT)),
                        Optional.ofNullable(isInfinitive ? -1 : args.getUnchecked(ARG_DURATION)),
                        Optional.ofNullable(args.getUnchecked(ARG_AMPLIFIER)),
                        Optional.empty()
                ))
                .then(booleanArgument(ARG_HIDE_PARTICLES)
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> giveEffect(
                                sender.getCallee(),
                                args.getUnchecked(ARG_TARGETS),
                                Objects.requireNonNull(args.getUnchecked(ARG_EFFECT)),
                                Optional.ofNullable(isInfinitive ? -1 : args.getUnchecked(ARG_DURATION)),
                                Optional.ofNullable(args.getUnchecked(ARG_AMPLIFIER)),
                                Optional.ofNullable(args.getUnchecked(ARG_HIDE_PARTICLES))
                        ))
                );
    }

}
