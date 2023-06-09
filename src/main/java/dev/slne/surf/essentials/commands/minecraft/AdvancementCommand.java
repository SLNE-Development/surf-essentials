package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdvancementCommand extends BrigadierCommand {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_ADVANCEMENTS;

    @Override
    public String[] names() {
        return new String[]{"advancement"};
    }

    @Override
    public String usage() {
        return "/advancement";
    }

    @Override
    public String description() {
        return "Manage advancements";
    }

    @Override
    public void literal(@NotNull LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.ADVANCEMENT_PERMISSION));

        literal.then(Commands.literal("grant")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.literal("only")
                                        .then(Commands.argument("advancement", ResourceLocationArgument.id())
                                                .suggests(SUGGEST_ADVANCEMENTS)
                                                .executes(context -> perform(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        Action.GRANT,
                                                        getAdvancements(ResourceLocationArgument.getAdvancement(context, "advancement"), Mode.ONLY)
                                                ))
                                                .then(Commands.argument("criterion", StringArgumentType.greedyString())
                                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                                ResourceLocationArgument.getAdvancement(context, "advancement").getCriteria().keySet(), builder))
                                                        .executes(context -> performCriterion(
                                                                context.getSource(),
                                                                EntityArgument.getPlayers(context, "targets"),
                                                                Action.GRANT,
                                                                ResourceLocationArgument.getAdvancement(context, "advancement"),
                                                                StringArgumentType.getString(context, "criterion")
                                                        ))
                                                )
                                        )
                                )
                                .then(Commands.literal("from")
                                        .then(Commands.argument("advancement", ResourceLocationArgument.id())
                                                .suggests(SUGGEST_ADVANCEMENTS)
                                                .executes(context -> perform(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        Action.GRANT,
                                                        getAdvancements(ResourceLocationArgument.getAdvancement(context, "advancement"), Mode.FROM)
                                                ))
                                        )
                                )
                                .then(Commands.literal("until")
                                        .then(Commands.argument("advancement", ResourceLocationArgument.id())
                                                .suggests(SUGGEST_ADVANCEMENTS)
                                                .executes(context -> perform(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        Action.GRANT,
                                                        getAdvancements(ResourceLocationArgument.getAdvancement(context, "advancement"), Mode.UNTIL)
                                                ))
                                        )
                                )
                                .then(Commands.literal("through")
                                        .then(Commands.argument("advancement", ResourceLocationArgument.id())
                                                .suggests(SUGGEST_ADVANCEMENTS)
                                                .executes(context -> perform(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        Action.GRANT,
                                                        getAdvancements(ResourceLocationArgument.getAdvancement(context, "advancement"), Mode.THROUGH)
                                                ))
                                        )
                                )
                                .then(Commands.literal("everything")
                                        .executes(context -> perform(
                                                context.getSource(),
                                                EntityArgument.getPlayers(context, "targets"),
                                                Action.GRANT,
                                                context.getSource().getServer().getAdvancements().getAllAdvancements()
                                        ))
                                )
                        )
                )
                .then(Commands.literal("revoke")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.literal("only")
                                        .then(Commands.argument("advancement", ResourceLocationArgument.id())
                                                .suggests(SUGGEST_ADVANCEMENTS)
                                                .executes(context -> perform(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        Action.REVOKE,
                                                        getAdvancements(ResourceLocationArgument.getAdvancement(context, "advancement"), Mode.ONLY)
                                                ))
                                                .then(Commands.argument("criterion", StringArgumentType.greedyString())
                                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                                ResourceLocationArgument.getAdvancement(context, "advancement").getCriteria().keySet(), builder))
                                                        .executes(context -> performCriterion(
                                                                context.getSource(),
                                                                EntityArgument.getPlayers(context, "targets"),
                                                                Action.REVOKE,
                                                                ResourceLocationArgument.getAdvancement(context, "advancement"),
                                                                StringArgumentType.getString(context, "criterion")
                                                        ))
                                                )
                                        )
                                )
                                .then(Commands.literal("from")
                                        .then(Commands.argument("advancement", ResourceLocationArgument.id())
                                                .suggests(SUGGEST_ADVANCEMENTS)
                                                .executes(context -> perform(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        Action.REVOKE,
                                                        getAdvancements(ResourceLocationArgument.getAdvancement(context, "advancement"), Mode.FROM)
                                                ))
                                        )
                                )
                                .then(Commands.literal("until")
                                        .then(Commands.argument("advancement", ResourceLocationArgument.id())
                                                .suggests(SUGGEST_ADVANCEMENTS)
                                                .executes(context -> perform(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        Action.REVOKE,
                                                        getAdvancements(ResourceLocationArgument.getAdvancement(context, "advancement"), Mode.UNTIL)
                                                ))
                                        )
                                )
                                .then(Commands.literal("through")
                                        .then(Commands.argument("advancement", ResourceLocationArgument.id())
                                                .suggests(SUGGEST_ADVANCEMENTS)
                                                .executes(context -> perform(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        Action.REVOKE,
                                                        getAdvancements(ResourceLocationArgument.getAdvancement(context, "advancement"), Mode.THROUGH)
                                                ))
                                        )
                                )
                                .then(Commands.literal("everything")
                                        .executes(context -> perform(
                                                context.getSource(),
                                                EntityArgument.getPlayers(context, "targets"),
                                                Action.REVOKE,
                                                context.getSource().getServer().getAdvancements().getAllAdvancements()
                                        ))
                                )
                        )
                );
    }


    private int perform(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked, Action operation, Collection<Advancement> selection) throws CommandSyntaxException {
        final var targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int success = 0;

        for (ServerPlayer target : targets) {
            success += operation.perform(target, selection);
        }

        if (success == 0) {
            if (selection.size() == 1) {
                if (targets.size() == 1) throw new SimpleCommandExceptionType(PaperAdventure.asVanilla(
                        Component.translatable(
                                operation.getKey() + ".one.to.one.failure",
                                Colors.ERROR,
                                getChatComponent(selection.iterator().next()),
                                EssentialsUtil.getDisplayName(targets.iterator().next())
                        )
                )).create();
                throw new SimpleCommandExceptionType(PaperAdventure.asVanilla(
                        Component.translatable(
                                operation.getKey() + ".one.to.many.failure",
                                Colors.ERROR,
                                getChatComponent(selection.iterator().next()),
                                Component.text(targets.size(), Colors.VARIABLE_VALUE)
                        )
                )).create();
            } else if (targets.size() == 1) throw new SimpleCommandExceptionType(PaperAdventure.asVanilla(
                    Component.translatable(
                            operation.getKey() + ".many.to.one.failure",
                            Colors.ERROR,
                            Component.text(selection.size(), Colors.VARIABLE_VALUE),
                            EssentialsUtil.getDisplayName(targets.iterator().next())
                    )
            )).create();
            throw new SimpleCommandExceptionType(PaperAdventure.asVanilla(
                    Component.translatable(
                            operation.getKey() + ".many.to.many.failure",
                            Colors.ERROR,
                            Component.text(selection.size(), Colors.VARIABLE_VALUE),
                            Component.text(targets.size(), Colors.VARIABLE_VALUE)
                    )
            )).create();
        }

        if (selection.size() == 1) {
            if (targets.size() == 1) {
                EssentialsUtil.sendSourceSuccess(
                        source,
                        Component.translatable(
                                operation.getKey() + ".one.to.one.success",
                                Colors.SUCCESS,
                                getChatComponent(selection.iterator().next()),
                                EssentialsUtil.getDisplayName(targets.iterator().next())
                        )
                );
            } else {
                EssentialsUtil.sendSourceSuccess(
                        source,
                        Component.translatable(
                                operation.getKey() + ".one.to.many.success",
                                Colors.SUCCESS,
                                getChatComponent(selection.iterator().next()),
                                Component.text(targets.size(), Colors.VARIABLE_VALUE)
                        )
                );
            }
        } else if (targets.size() == 1) {
            EssentialsUtil.sendSourceSuccess(
                    source,
                    Component.translatable(
                            operation.getKey() + ".many.to.one.success",
                            Colors.SUCCESS,
                            Component.text(selection.size(), Colors.VARIABLE_VALUE),
                            EssentialsUtil.getDisplayName(targets.iterator().next())
                    )
            );
        } else {
            EssentialsUtil.sendSourceSuccess(
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


    private int performCriterion(CommandSourceStack source, Collection<ServerPlayer> targets, Action operation, Advancement advancement, String criterion) throws CommandSyntaxException {
        int success = 0;
        if (!advancement.getCriteria().containsKey(criterion))
            throw new SimpleCommandExceptionType(PaperAdventure.asVanilla(
                    Component.translatable(
                            "commands.advancement.criterionNotFound",
                            Colors.ERROR,
                            getChatComponent(advancement),
                            Component.text(criterion, Colors.VARIABLE_VALUE)
                    )
            )).create();


        for (ServerPlayer target : targets) {
            if (operation.performCriterion(target, advancement, criterion)) success++;
        }

        if (success == 0) {
            if (targets.size() == 1) {
                throw new SimpleCommandExceptionType(PaperAdventure.asVanilla(
                        Component.translatable(
                                operation.getKey() + ".criterion.to.one.failure",
                                Colors.ERROR,
                                Component.text(criterion, Colors.VARIABLE_VALUE),
                                getChatComponent(advancement),
                                EssentialsUtil.getDisplayName(targets.iterator().next())
                        )
                )).create();
            }
            throw new SimpleCommandExceptionType(PaperAdventure.asVanilla(
                    Component.translatable(
                            operation.getKey() + ".criterion.to.many.failure",
                            Colors.ERROR,
                            Component.text(criterion, Colors.VARIABLE_VALUE),
                            getChatComponent(advancement),
                            Component.text(targets.size(), Colors.VARIABLE_VALUE)
                    )
            )).create();
        }


        if (targets.size() == 1) {
            EssentialsUtil.sendSourceSuccess(source, Component.translatable(
                    operation.getKey() + ".criterion.to.one.success",
                    Component.text(criterion, Colors.VARIABLE_VALUE),
                    getChatComponent(advancement),
                    EssentialsUtil.getDisplayName(targets.iterator().next())

            ));
        } else {
            EssentialsUtil.sendSourceSuccess(source, Component.translatable(
                    operation.getKey() + ".criterion.to.many.success",
                    Component.text(criterion, Colors.VARIABLE_VALUE),
                    getChatComponent(advancement),
                    Component.text(targets.size(), Colors.VARIABLE_VALUE)
            ));
        }

        return success;
    }


    private @NotNull Component getChatComponent(@NotNull Advancement advancement) {
        return PaperAdventure.asAdventure(advancement.getChatComponent()).colorIfAbsent(Colors.VARIABLE_VALUE);
    }

    @Contract("_, _ -> new")
    private List<Advancement> getAdvancements(Advancement advancement, Mode selection) {
        return EssentialsUtil.make(new ArrayList<>(), advancements -> {
            if (selection.isParents()) {
                var parent = advancement.getParent();

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
            protected boolean perform(ServerPlayer player, Advancement advancement) {
                final var advancementProgress = player.getAdvancements().getOrStartProgress(advancement);
                if (advancementProgress.isDone()) return false;

                for (String string : advancementProgress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, string);
                }

                return true;
            }

            @Override
            protected boolean performCriterion(ServerPlayer player, Advancement advancement, String criterion) {
                return player.getAdvancements().award(advancement, criterion);
            }
        },
        REVOKE("revoke") {
            @Override
            protected boolean perform(ServerPlayer player, Advancement advancement) {
                final var advancementProgress = player.getAdvancements().getOrStartProgress(advancement);
                if (!advancementProgress.hasProgress()) return false;

                for (String string : advancementProgress.getCompletedCriteria()) {
                    player.getAdvancements().revoke(advancement, string);
                }

                return true;
            }

            @Override
            protected boolean performCriterion(ServerPlayer player, Advancement advancement, String criterion) {
                return player.getAdvancements().revoke(advancement, criterion);
            }
        };


        private final String key;

        @Contract(pure = true)
        Action(String name) {
            this.key = "commands.advancement." + name;
        }

        public int perform(ServerPlayer player, @NotNull Iterable<Advancement> advancements) {
            int success = 0;

            for (Advancement advancement : advancements) {
                if (this.perform(player, advancement)) ++success;
            }

            return success;
        }

        protected abstract boolean perform(ServerPlayer player, Advancement advancement);

        protected abstract boolean performCriterion(ServerPlayer player, Advancement advancement, String criterion);

        protected String getKey() {
            return this.key;
        }
    }

    private enum Mode {
        ONLY(false, false),
        THROUGH(true, true),
        FROM(false, true),
        UNTIL(true, false),
        EVERYTHING(true, true);

        private final boolean parents;
        private final boolean children;

        @Contract(pure = true)
        Mode(boolean before, boolean after) {
            this.parents = before;
            this.children = after;
        }

        @Contract(pure = true)
        public boolean isParents() {
            return parents;
        }

        @Contract(pure = true)
        public boolean isChildren() {
            return children;
        }
    }


    static {
        SUGGEST_ADVANCEMENTS = (context, builder) -> SharedSuggestionProvider.suggestResource(
                context.getSource()
                        .getServer()
                        .getAdvancements()
                        .getAllAdvancements()
                        .stream()
                        .map(Advancement::getId),
                builder
        );
    }
}
