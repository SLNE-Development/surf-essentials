package dev.slne.surf.essentials.commands.minecraft;

import com.google.common.collect.ImmutableList;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class AdvancementCommand extends EssentialsCommand {
    private static final String ADVANCEMENT_ARG = "advancement";
    private static final String CRITERION_ARG = "criterion";
    private static final String PLAYERS_ARG = "players";

    private static final ArgumentSuggestions<CommandSender> CRITERION_SUGGESTION;

    public AdvancementCommand() {
        super("advancement", "advancement <grant | revoke> ", "Manage advancements");

        withPermission(Permissions.ADVANCEMENT_PERMISSION);

        then(literal("grant")
                .then(playersArgument(PLAYERS_ARG)
                        .then(literal("only")
                                .then(advancementArgument(ADVANCEMENT_ARG)
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> perform(
                                                sender.getCallee(),
                                                args.getUnchecked(PLAYERS_ARG),
                                                Action.GRANT,
                                                getAdvancements(Objects.requireNonNull(args.getUnchecked(ADVANCEMENT_ARG)), Mode.ONLY)
                                        ))
                                        .then(greedyStringArgument(CRITERION_ARG)
                                                .replaceSuggestions(CRITERION_SUGGESTION)
                                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> performCriterion(
                                                        sender.getCallee(),
                                                        args.getUnchecked(PLAYERS_ARG),
                                                        Action.GRANT,
                                                        Objects.requireNonNull(args.getUnchecked(ADVANCEMENT_ARG)),
                                                        args.getUnchecked(CRITERION_ARG)
                                                ))
                                        )
                                )
                        )
                        .then(literal("from")
                                .then(advancementArgument(ADVANCEMENT_ARG)
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> perform(
                                                sender.getCallee(),
                                                args.getUnchecked(PLAYERS_ARG),
                                                Action.GRANT,
                                                getAdvancements(args.getUnchecked(ADVANCEMENT_ARG), Mode.FROM)
                                        ))
                                )
                        )
                        .then(literal("until")
                                .then(advancementArgument(ADVANCEMENT_ARG)
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> perform(
                                                sender.getCallee(),
                                                args.getUnchecked(PLAYERS_ARG),
                                                Action.GRANT,
                                                getAdvancements(args.getUnchecked(ADVANCEMENT_ARG), Mode.UNTIL)
                                        ))
                                )
                        )
                        .then(literal("through")
                                .then(advancementArgument(ADVANCEMENT_ARG)
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> perform(
                                                sender.getCallee(),
                                                args.getUnchecked(PLAYERS_ARG),
                                                Action.GRANT,
                                                getAdvancements(args.getUnchecked(ADVANCEMENT_ARG), Mode.THROUGH)
                                        ))
                                )
                        )
                        .then(literal("everything")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> perform(
                                        sender.getCallee(),
                                        args.getUnchecked(PLAYERS_ARG),
                                        Action.GRANT,
                                        ImmutableList.copyOf(sender.getCallee().getServer().advancementIterator())
                                ))
                        )
                )
        );

        then(literal("revoke")
                .then(playersArgument(PLAYERS_ARG)
                        .then(literal("only")
                                .then(advancementArgument(ADVANCEMENT_ARG)
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> perform(
                                                sender.getCallee(),
                                                args.getUnchecked(PLAYERS_ARG),
                                                Action.REVOKE,
                                                getAdvancements(Objects.requireNonNull(args.getUnchecked(ADVANCEMENT_ARG)), Mode.ONLY)
                                        ))
                                        .then(greedyStringArgument(CRITERION_ARG)
                                                .replaceSuggestions(CRITERION_SUGGESTION)
                                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> performCriterion(
                                                        sender.getCallee(),
                                                        args.getUnchecked(PLAYERS_ARG),
                                                        Action.REVOKE,
                                                        Objects.requireNonNull(args.getUnchecked(ADVANCEMENT_ARG)),
                                                        args.getUnchecked(CRITERION_ARG)
                                                ))
                                        )
                                )
                        )
                        .then(literal("from")
                                .then(advancementArgument(ADVANCEMENT_ARG)
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> perform(
                                                sender.getCallee(),
                                                args.getUnchecked(PLAYERS_ARG),
                                                Action.REVOKE,
                                                getAdvancements(args.getUnchecked(ADVANCEMENT_ARG), Mode.FROM)
                                        ))
                                )
                        )
                        .then(literal("until")
                                .then(advancementArgument(ADVANCEMENT_ARG)
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> perform(
                                                sender.getCallee(),
                                                args.getUnchecked(PLAYERS_ARG),
                                                Action.REVOKE,
                                                getAdvancements(args.getUnchecked(ADVANCEMENT_ARG), Mode.UNTIL)
                                        ))
                                )
                        )
                        .then(literal("through")
                                .then(advancementArgument(ADVANCEMENT_ARG)
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> perform(
                                                sender.getCallee(),
                                                args.getUnchecked(PLAYERS_ARG),
                                                Action.REVOKE,
                                                getAdvancements(args.getUnchecked(ADVANCEMENT_ARG), Mode.THROUGH)
                                        ))
                                )

                        )
                        .then(literal("everything")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> perform(
                                        sender.getCallee(),
                                        args.getUnchecked(PLAYERS_ARG),
                                        Action.REVOKE,
                                        ImmutableList.copyOf(sender.getCallee().getServer().advancementIterator())
                                ))
                        )
                )
        );
    }

    private int perform(CommandSender source, Collection<Player> targetsUnchecked, Action operation, Collection<Advancement> selection) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int success = 0;

        for (Player target : targets) {
            success += operation.perform(target, selection);
        }

        if (success == 0) {
            if (selection.size() == 1) {
                if (targets.size() == 1) throw Exceptions.ADVANCEMENT_ONE_TO_ONE_FAILURE.create(
                        operation.getKey(),
                        EssentialsUtil.getDisplayName(selection.iterator().next()),
                        EssentialsUtil.getDisplayName(targets.iterator().next())
                );

                throw Exceptions.ADVANCEMENT_ONE_TO_MANY_FAILURE.create(
                        operation.getKey(),
                        EssentialsUtil.getDisplayName(selection.iterator().next()),
                        targets.size()
                );
            } else if (targets.size() == 1) throw Exceptions.ADVANCEMENT_MANY_TO_ONE_FAILURE.create(
                    operation.getKey(),
                    selection.size(),
                    EssentialsUtil.getDisplayName(targets.iterator().next())
            );

            throw Exceptions.ADVANCEMENT_MANY_TO_MANY_FAILURE.create(
                    operation.getKey(),
                    selection.size(),
                    targets.size()
            );
        }

        if (selection.size() == 1) {
            if (targets.size() == 1) {
                EssentialsUtil.sendSuccess(
                        source,
                        Component.translatable(
                                operation.getKey() + ".one.to.one.success",
                                Colors.SUCCESS,
                                EssentialsUtil.getDisplayName(selection.iterator().next()),
                                EssentialsUtil.getDisplayName(targets.iterator().next())
                        )
                );
            } else {
                EssentialsUtil.sendSuccess(
                        source,
                        Component.translatable(
                                operation.getKey() + ".one.to.many.success",
                                Colors.SUCCESS,
                                EssentialsUtil.getDisplayName(selection.iterator().next()),
                                Component.text(targets.size(), Colors.VARIABLE_VALUE)
                        )
                );
            }
        } else if (targets.size() == 1) {
            EssentialsUtil.sendSuccess(
                    source,
                    Component.translatable(
                            operation.getKey() + ".many.to.one.success",
                            Colors.SUCCESS,
                            Component.text(selection.size(), Colors.VARIABLE_VALUE),
                            EssentialsUtil.getDisplayName(targets.iterator().next())
                    )
            );
        } else {
            EssentialsUtil.sendSuccess(
                    source,
                    Component.translatable(
                            operation.getKey() + ".many.to.many.success",
                            Colors.SUCCESS,
                            Component.text(selection.size(), Colors.VARIABLE_VALUE),
                            Component.text(targets.size(), Colors.VARIABLE_VALUE)
                    )
            );
        }

        return success;
    }

    private int performCriterion(CommandSender source, Collection<Player> targets, Action operation, @NotNull Advancement advancement, String criterion) throws WrapperCommandSyntaxException {
        int success = 0;
        if (!advancement.getCriteria().contains(criterion))
            throw Exceptions.ADVANCEMENT_CRITERION_NOT_FOUND.create(
                    EssentialsUtil.getDisplayName(advancement),
                    criterion
            );


        for (Player target : targets) {
            if (operation.performCriterion(target, advancement, criterion)) success++;
        }

        if (success == 0) {
            if (targets.size() == 1) {
                throw Exceptions.ADVANCEMENT_CRITERION_TO_ONE_FAILURE.create(
                        operation.getKey(),
                        criterion,
                        EssentialsUtil.getDisplayName(advancement),
                        EssentialsUtil.getDisplayName(targets.iterator().next())
                );
            }
            throw Exceptions.ADVANCEMENT_CRITERION_TO_MANY_FAILURE.create(
                    operation.getKey(),
                    criterion,
                    EssentialsUtil.getDisplayName(advancement),
                    targets.size()
            );
        }


        if (targets.size() == 1) {
            EssentialsUtil.sendSuccess(source, Component.translatable(
                    operation.getKey() + ".criterion.to.one.success",
                    Component.text(criterion, Colors.VARIABLE_VALUE),
                    EssentialsUtil.getDisplayName(advancement),
                    EssentialsUtil.getDisplayName(targets.iterator().next())

            ));
        } else {
            EssentialsUtil.sendSuccess(source, Component.translatable(
                    operation.getKey() + ".criterion.to.many.success",
                    Component.text(criterion, Colors.VARIABLE_VALUE),
                    EssentialsUtil.getDisplayName(advancement),
                    Component.text(targets.size(), Colors.VARIABLE_VALUE)
            ));
        }

        return success;
    }

    private List<Advancement> getAdvancements(Advancement advancement, Mode selection) {
        return EssentialsUtil.make(new ArrayList<>(), advancements -> {
            if (selection.isParents()) {
                Advancement parent = advancement.getParent();

                while (parent != null) {
                    advancements.add(parent);
                    parent = parent.getParent();
                }
            }

            advancements.add(advancement);

            if (selection.isChildren()) addChildren(advancement, advancements);
        });
    }

    private void addChildren(Advancement parent, List<Advancement> childList) {
        for (Advancement advancement : parent.getChildren()) {
            childList.add(advancement);
            addChildren(advancement, childList);
        }
    }


    private enum Action {
        GRANT("grant") {
            @Override
            protected boolean perform(Player player, org.bukkit.advancement.Advancement advancement) {
                val progress = player.getAdvancementProgress(advancement);
                if (progress.isDone()) return false;

                for (String criteria : progress.getRemainingCriteria()) {
                    progress.awardCriteria(criteria);
                }

                return true;
            }

            @Override
            protected boolean performCriterion(Player player, org.bukkit.advancement.Advancement advancement, String criterion) {
                return player.getAdvancementProgress(advancement).awardCriteria(criterion);
            }
        },
        REVOKE("revoke") {
            @Override
            protected boolean perform(Player player, org.bukkit.advancement.Advancement advancement) {
                val progress = player.getAdvancementProgress(advancement);


                if (progress.getAwardedCriteria().isEmpty()) return false;

                for (String criteria : progress.getAwardedCriteria()) {
                    progress.revokeCriteria(criteria);
                }

                return true;
            }

            @Override
            protected boolean performCriterion(Player player, org.bukkit.advancement.Advancement advancement, String criterion) {
                return player.getAdvancementProgress(advancement).revokeCriteria(criterion);
            }
        };

        @Getter
        private final String key;

        @Contract(pure = true)
        Action(String name) {
            this.key = "commands.advancement." + name;
        }

        public int perform(Player player, @NotNull Iterable<org.bukkit.advancement.Advancement> advancements) {
            int success = 0;

            for (org.bukkit.advancement.Advancement advancement : advancements) {
                if (this.perform(player, advancement)) ++success;
            }

            return success;
        }

        protected abstract boolean perform(Player player, org.bukkit.advancement.Advancement advancement);

        protected abstract boolean performCriterion(Player player, org.bukkit.advancement.Advancement advancement, String criterion);
    }

    @Getter
    @RequiredArgsConstructor
    private enum Mode {
        ONLY(false, false),
        THROUGH(true, true),
        FROM(false, true),
        UNTIL(true, false),
        EVERYTHING(true, true);

        private final boolean parents;
        private final boolean children;
    }


    static {
        CRITERION_SUGGESTION = ArgumentSuggestions.stringCollection(info -> ((Advancement) Objects.requireNonNull(info.previousArgs().get(ADVANCEMENT_ARG))).getCriteria());
    }
}

