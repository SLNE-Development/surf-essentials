package dev.slne.surf.essentials.commands.cheat;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Range;

import java.util.Collection;
import java.util.List;

public class SpeedCommand extends EssentialsCommand {
    public SpeedCommand() {
        super("speed", "speed <speed> <walk | fly> [<players>]", "Change walk / fly speed", "flyspeed", "walkspeed");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.SPEED_PERMISSION_SELF, Permissions.SPEED_PERMISSION_OTHER));

        then(floatArgument("speed", -10, 10)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> detect(
                        sender.getCallee(),
                        List.of(getPlayerOrException(sender)),
                        args.getUnchecked("speed")
                ))
                .then(literal("walk")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> changeWalkSpeed(
                                sender.getCallee(),
                                List.of(getPlayerOrException(sender)),
                                args.getUnchecked("speed")
                        ))
                        .then(playersArgument("players")
                                .withPermission(Permissions.SPEED_PERMISSION_OTHER)
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> changeWalkSpeed(
                                        sender.getCallee(),
                                        args.getUnchecked("players"),
                                        args.getUnchecked("speed")
                                ))))

                .then(literal("fly")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> changeFlySpeed(
                                sender.getCallee(),
                                List.of(getPlayerOrException(sender)),
                                args.getUnchecked("speed")
                        ))
                        .then(playersArgument("players")
                                .withPermission(Permissions.SPEED_PERMISSION_OTHER)
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> changeFlySpeed(
                                        sender.getCallee(),
                                        args.getUnchecked("players"),
                                        args.getUnchecked("speed")
                                ))
                        )
                )
        )
                .then(literal("reset")
                        .executesNative((sender, args) -> changeWalkSpeed(
                                sender.getCallee(),
                                List.of(getPlayerOrException(sender)),
                                2.0f
                        ) + changeFlySpeed(
                                sender.getCallee(),
                                List.of(getPlayerOrException(sender)),
                                2.0f
                        ))
                        .then(playersArgument("players")
                                .withPermission(Permissions.SPEED_PERMISSION_OTHER)
                                .executesNative((sender, args) -> changeWalkSpeed(
                                        sender.getCallee(),
                                        args.getUnchecked("players"),
                                        2.0f
                                ) + changeFlySpeed(
                                        sender.getCallee(),
                                        args.getUnchecked("players"),
                                        2.0f
                                ))
                        )
                )

                .then(literal("resetwalk")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> changeWalkSpeed(
                                sender.getCallee(),
                                List.of(getPlayerOrException(sender)),
                                2.0f
                        ))
                        .then(playersArgument("players")
                                .withPermission(Permissions.SPEED_PERMISSION_OTHER)
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> changeWalkSpeed(
                                        sender,
                                        args.getUnchecked("players"),
                                        2.0f
                                ))
                        )
                )

                .then(literal("resetfly")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> changeFlySpeed(
                                sender.getCallee(),
                                List.of(getPlayerOrException(sender)),
                                2.0f
                        ))
                        .then(playersArgument("players")
                                .withPermission(Permissions.SPEED_PERMISSION_OTHER)
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> changeFlySpeed(
                                        sender.getCallee(),
                                        args.getUnchecked("players"),
                                        2.0f
                                ))
                        )
                );
    }

    private int detect(CommandSender source, Collection<Player> playersUnchecked, @Range(from = -10, to = 10) Float speed) throws WrapperCommandSyntaxException {
        val players = EssentialsUtil.checkPlayerSuggestion(source, playersUnchecked);
        val calculatedSpeed = speed / 10f;
        int successes = 0;

        for (Player player : players) {
            if (player.isFlying()) {
                player.setFlySpeed(calculatedSpeed);
                successPlayer(player, "Fluggeschwindigkeit", speed);
            } else {
                player.setWalkSpeed(calculatedSpeed);
                successPlayer(player, "Gehgeschwindigkeit", speed);
            }
            successes++;
        }
        successSource(source, true, successes, speed, players);
        return successes;
    }

    private int changeFlySpeed(CommandSender source, Collection<Player> playersUnchecked, @Range(from = -10, to = 10) Float speed) throws WrapperCommandSyntaxException {
        return changeSpeed(source, EssentialsUtil.checkPlayerSuggestion(source, playersUnchecked), speed, false);
    }

    private int changeWalkSpeed(CommandSender source, Collection<Player> playersUnchecked, @Range(from = -10, to = 10) Float speed) throws WrapperCommandSyntaxException {
        return changeSpeed(source, EssentialsUtil.checkPlayerSuggestion(source, playersUnchecked), speed, true);
    }

    private int changeSpeed(CommandSender source, Collection<Player> players, @Range(from = -10, to = 10) float speed, boolean isWalkSpeed) throws WrapperCommandSyntaxException {
        val calculatedSpeed = speed / 10f;
        int successes = 0;

        for (Player player : players) {
            if (isWalkSpeed) {
                player.setWalkSpeed(calculatedSpeed);
                successPlayer(player, "Gehgeschwindigkeit", speed);
            } else {
                player.setFlySpeed(calculatedSpeed);
                successPlayer(player, "Fluggeschwindigkeit", speed);
            }
            successes++;
        }

        successSource(source, isWalkSpeed, successes, speed, players);
        return successes;
    }

    private void successPlayer(Player player, String mode, @Range(from = -10, to = 10) float speed) {
        EssentialsUtil.sendSuccess(player, Component.text("Deine %s wurde auf ".formatted(mode), Colors.SUCCESS)
                .append(Component.text(speed, Colors.TERTIARY))
                .append(Component.text(" gesetzt", Colors.SUCCESS)));
    }

    private void successSource(CommandSender source, boolean isWalkSpeed, int successes, @Range(from = -10, to = 10) float speed, Collection<Player> players) throws WrapperCommandSyntaxException {
        val mode = (isWalkSpeed) ? "Gehgeschwindigkeit" : "Fluggeschwindigkeit";
        if (successes == 1 && source instanceof Player player && player.getUniqueId() != players.iterator().next().getUniqueId()) {
            EssentialsUtil.sendSuccess(source, Component.text("Die %s von ".formatted(mode), Colors.SUCCESS)
                    .append(EssentialsUtil.getDisplayName(players.iterator().next()))
                    .append(Component.text(" wurde auf ", Colors.SUCCESS))
                    .append(Component.text(speed, Colors.TERTIARY))
                    .append(Component.text(" gesetzt", Colors.SUCCESS)));
        } else if (source instanceof Player player && player.getUniqueId() != players.iterator().next().getUniqueId()) {
            EssentialsUtil.sendSuccess(source, Component.text("Die %s von ".formatted(mode), Colors.SUCCESS)
                    .append(Component.text(successes, Colors.TERTIARY))
                    .append(Component.text("  Spielern wurde auf ", Colors.SUCCESS))
                    .append(Component.text(speed, Colors.TERTIARY))
                    .append(Component.text(" gesetzt", Colors.SUCCESS)));
        }
    }
}
