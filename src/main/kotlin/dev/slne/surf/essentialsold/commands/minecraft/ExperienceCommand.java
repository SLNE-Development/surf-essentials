package dev.slne.surf.essentialsold.commands.minecraft;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.copy.Mth;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ExperienceCommand extends EssentialsCommand {
    public ExperienceCommand() {
        super("experience", "experience <query | add | set>", "Query, add or set the experience of the targets", "xp");

        withPermission(Permissions.EXPERIENCE_PERMISSION);

        then(literal("query")
                .then(playerArgument("player")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> query(
                                sender.getCallee(),
                                args.getUnchecked("player"),
                                Type.POINTS
                        ))
                        .then(literal("levels")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> query(
                                        sender.getCallee(),
                                        args.getUnchecked("player"),
                                        Type.LEVELS
                                ))
                        )
                        .then(literal("points")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> query(
                                        sender.getCallee(),
                                        args.getUnchecked("player"),
                                        Type.POINTS
                                ))
                        )
                )
        );

        then(literal("add")
                .then(playersArgument("players")
                        .then(integerArgument("amount")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> give(
                                        sender.getCallee(),
                                        args.getUnchecked("players"),
                                        args.getUnchecked("amount"),
                                        Type.POINTS
                                ))
                                .then(literal("levels")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> give(
                                                sender.getCallee(),
                                                args.getUnchecked("players"),
                                                args.getUnchecked("amount"),
                                                Type.LEVELS
                                        ))
                                )
                                .then(literal("points")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> give(
                                                sender.getCallee(),
                                                args.getUnchecked("players"),
                                                args.getUnchecked("amount"),
                                                Type.POINTS
                                        ))
                                )
                        )
                )
        );

        then(literal("set")
                .then(playersArgument("players")
                        .then(integerArgument("amount")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> set(
                                        sender.getCallee(),
                                        args.getUnchecked("players"),
                                        args.getUnchecked("amount"),
                                        Type.POINTS
                                ))
                                .then(literal("levels")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> set(
                                                sender.getCallee(),
                                                args.getUnchecked("players"),
                                                args.getUnchecked("amount"),
                                                Type.LEVELS
                                        ))
                                )
                                .then(literal("points")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> set(
                                                sender.getCallee(),
                                                args.getUnchecked("players"),
                                                args.getUnchecked("amount"),
                                                Type.POINTS
                                        ))
                                )
                        )
                )
        );

    }

    private int query(CommandSender source, Player targetUnchecked, Type whatToQuery) throws WrapperCommandSyntaxException {
        val target = EssentialsUtil.checkPlayerSuggestion(source, targetUnchecked);
        val result = (whatToQuery == Type.LEVELS) ? target.getLevel() : Math.round(target.getExp() * (float) target.getExpToLevel());

        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(target)
                .append(Component.text(" hat ", Colors.INFO))
                .append(Component.text(result + " ", Colors.VARIABLE_VALUE))
                .append((result == 0) ? whatToQuery.getSingularComponent() : whatToQuery.getPluralComponent())
                .append(Component.text("!", Colors.INFO)));

        return 1;
    }

    private int give(CommandSender source, Collection<Player> targetsUnchecked, Integer amount, Type whatToGive) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);

        for (Player player : targets) {
            if (whatToGive == Type.LEVELS) player.giveExpLevels(amount);
            else player.giveExp(amount);
        }

        if (targets.size() == 1) {
            val target = targets.iterator().next();
            val experience = (whatToGive == Type.LEVELS) ? target.getLevel() : Math.round(target.getExp() * (float) target.getExpToLevel());

            EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(targets.iterator().next())
                    .append(Component.text(" hat ", Colors.SUCCESS))
                    .append(Component.text(amount, Colors.VARIABLE_VALUE))
                    .append((amount == 0) ? whatToGive.getSingularComponent() : whatToGive.getPluralComponent())
                    .append(Component.text(" erhalten!", Colors.SUCCESS))
                    .hoverEvent(HoverEvent.showText(Component.text("Insgesamt: ", Colors.INFO)
                            .append(Component.text(experience, Colors.GREEN)))));
        } else {
            EssentialsUtil.sendSuccess(source, Component.text(targets.size(), Colors.VARIABLE_VALUE)
                    .append(Component.text(" Spieler haben ", Colors.SUCCESS))
                    .append(Component.text(amount, Colors.VARIABLE_VALUE))
                    .append((amount == 0) ? whatToGive.getSingularComponent() : whatToGive.getPluralComponent())
                    .append(Component.text(" erhalten!", Colors.SUCCESS)));
        }
        return 1;
    }

    private int set(CommandSender source, Collection<Player> targetsUnchecked, Integer amount, Type whatToSet) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);

        for (Player player : targets) {
            if ((whatToSet == Type.LEVELS)) player.setLevel(amount);
            else setExperiencePoints(player, amount);
        }

        EssentialsUtil.sendSuccess(source, Component.text("Die ", Colors.SUCCESS)
                .append(whatToSet.getPluralComponent())
                .append(Component.text(" von ", Colors.SUCCESS))
                .append((targets.size() == 1) ? EssentialsUtil.getDisplayName(targets.iterator().next()) : Component.text(targets.size(), Colors.TERTIARY))
                .append(Component.text(" %s auf ".formatted(targets.size() == 1 ? "wurden" : "Spielern wurden"), Colors.SUCCESS))
                .append(Component.text(amount, Colors.VARIABLE_VALUE))
                .append(Component.text(" gesetzt!", Colors.SUCCESS)));

        return 1;
    }

    /**
     * Sets the experience points of a player
     *
     * @param player The player to set the experience points of
     * @param points The amount of experience points to set
     */
    public void setExperiencePoints(Player player, int points) {
        final float expToLevel = player.getExpToLevel();
        player.setExp(Mth.clamp((points / expToLevel), 0.0F, ((expToLevel - 1.0F) / expToLevel)));
    }

    /**
     * The type of experience to query, add or set
     */
    @Getter
    @RequiredArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    private enum Type {
        LEVELS("Erfahrungslevel", "Erfahrungslevel"),
        POINTS("Erfahrungspunkt", "Erfahrungspunkte");

        String nameSingular;
        String namePlural;

        /**
         * Gets the singular component of this type of experience
         *
         * @return The singular component of this type of experience
         */
        public Component getSingularComponent() {
            return Component.text(getNameSingular(), Colors.VARIABLE_VALUE);
        }

        /**
         * Gets the plural component of this type of experience
         *
         * @return The plural component of this type of experience
         */
        public Component getPluralComponent() {
            return Component.text(getNamePlural(), Colors.VARIABLE_VALUE);
        }
    }
}
