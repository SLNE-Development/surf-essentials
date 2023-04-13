package dev.slne.surf.essentials.commands.cheat;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Range;

import java.util.Collection;
import java.util.Collections;

public class SpeedCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"speed", "flyspeed", "walkspeed"};
    }

    @Override
    public String usage() {
        return "/speed <speed> <walk | fly> [<players>]";
    }

    @Override
    public String description() {
        return "Change walk / fly speed";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.SPEED_PERMISSION_SELF, Permissions.SPEED_PERMISSION_OTHER));

        literal.then(Commands.argument("speed", IntegerArgumentType.integer(-10, 10))
                .executes(context -> detect(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()),
                        IntegerArgumentType.getInteger(context, "speed")))
                .then(Commands.literal("walk")
                        .executes(context -> changeWalkSpeed(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()),
                                IntegerArgumentType.getInteger(context, "speed")))
                        .then(Commands.argument("players", EntityArgument.players())
                                .requires(EssentialsUtil.checkPermissions(Permissions.SPEED_PERMISSION_OTHER))
                                .executes(context -> changeWalkSpeed(context.getSource(), EntityArgument.getPlayers(context, "players"),
                                        IntegerArgumentType.getInteger(context, "speed")))))
                .then(Commands.literal("fly")
                .executes(context -> changeFlySpeed(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()),
                        IntegerArgumentType.getInteger(context, "speed")))
                .then(Commands.argument("players", EntityArgument.players())
                        .requires(EssentialsUtil.checkPermissions(Permissions.SPEED_PERMISSION_OTHER))
                        .executes(context -> changeWalkSpeed(context.getSource(), EntityArgument.getPlayers(context, "players"),
                                IntegerArgumentType.getInteger(context, "speed"))))));

        literal.then(Commands.literal("default")
                .executes(context -> {
                    changeWalkSpeed(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), 2);
                    return changeFlySpeed(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), 2);
                })
                .then(Commands.literal("walk")
                        .executes(context -> changeWalkSpeed(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), 2)))
                .then(Commands.literal("fly")
                        .executes(context -> changeFlySpeed(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), 2))));
    }

    private int detect(CommandSourceStack source, Collection<ServerPlayer> playersUnchecked, @Range(from = -10, to = 10) int speed) throws CommandSyntaxException {
        var players = EssentialsUtil.checkPlayerSuggestion(source, playersUnchecked);
        var calculatedSpeed = (float) speed / 10;
        int successes = 0;

        for (ServerPlayer player : players) {
            if (player.getAbilities().flying){
                player.getBukkitEntity().setFlySpeed(calculatedSpeed);
                successPlayer(player, "Fluggeschwindigkeit", speed);
            }else {
                player.getBukkitEntity().setWalkSpeed(calculatedSpeed);
                successPlayer(player, "Gehgeschwindigkeit", speed);
            }
            successes++;
        }
        successSource(source, true, successes, speed, players);
        return successes;
    }

    private int changeFlySpeed(CommandSourceStack source, Collection<ServerPlayer> playersUnchecked, @Range(from = -10, to = 10) int speed) throws CommandSyntaxException {
        var players = EssentialsUtil.checkPlayerSuggestion(source, playersUnchecked);
        return changeSpeed(source, players, speed, false);
    }

    private int changeWalkSpeed(CommandSourceStack source, Collection<ServerPlayer> playersUnchecked, @Range(from = -10, to = 10) int speed) throws CommandSyntaxException {
        var players = EssentialsUtil.checkPlayerSuggestion(source, playersUnchecked);
        return changeSpeed(source, players, speed, true);
    }

    private int changeSpeed(CommandSourceStack source, Collection<ServerPlayer> players, @Range(from = -10, to = 10) int speed, boolean isWalkSpeed) throws CommandSyntaxException {
        var calculatedSpeed = (float) speed / 10;
        int successes = 0;

        for (ServerPlayer player : players) {
            if (isWalkSpeed){
                player.getBukkitEntity().setWalkSpeed(calculatedSpeed);
                successPlayer(player, "Gehgeschwindigkeit", speed);
            }else {
                player.getBukkitEntity().setFlySpeed(calculatedSpeed);
                successPlayer(player, "Fluggeschwindigkeit", speed);
            }
            successes++;
        }

       successSource(source, isWalkSpeed, successes, speed, players);
        return successes;
    }

    private void successPlayer(ServerPlayer player, String mode, @Range(from = -10, to = 10) int speed) {
        EssentialsUtil.sendSuccess(player, Component.text("Deine %s wurde auf ".formatted(mode), Colors.SUCCESS)
                .append(Component.text(speed, Colors.TERTIARY))
                .append(Component.text(" gesetzt", Colors.SUCCESS)));
    }

    private void successSource(CommandSourceStack source, boolean isWalkSpeed, int successes, @Range(from = -10, to = 10) int speed,  Collection<ServerPlayer> players) throws CommandSyntaxException {
        if (source.isPlayer()){
            String mode = (isWalkSpeed) ? "Gehgeschwindigkeit" : "Fluggeschwindigkeit";
            if (successes == 1 && source.getPlayerOrException().getUUID() != players.iterator().next().getUUID()){
                EssentialsUtil.sendSuccess(source, Component.text("Die %s von ".formatted(mode), Colors.SUCCESS)
                        .append(EssentialsUtil.getDisplayName(players.iterator().next()))
                        .append(Component.text(" wurde auf ", Colors.SUCCESS))
                        .append(Component.text(speed, Colors.TERTIARY))
                        .append(Component.text(" gesetzt", Colors.SUCCESS)));
            }else if (source.getPlayerOrException().getUUID() != players.iterator().next().getUUID()){
                EssentialsUtil.sendSuccess(source, Component.text("Die %s von ".formatted(mode), Colors.SUCCESS)
                        .append(Component.text(successes, Colors.TERTIARY))
                        .append(Component.text("  Spielern wurde auf ", Colors.SUCCESS))
                        .append(Component.text(speed, Colors.TERTIARY))
                        .append(Component.text(" gesetzt", Colors.SUCCESS)));
            }
        }else {
            String mode = (isWalkSpeed) ? "walk" : "fly";
            if (successes == 1){
                EssentialsUtil.sendSourceSuccess(source, EssentialsUtil.getDisplayName(players.iterator().next())
                        .append(Component.text("´s %s speed was set to ".formatted(mode), Colors.SUCCESS))
                        .append(Component.text(speed, Colors.TERTIARY)));
            }else {
                EssentialsUtil.sendSourceSuccess(source, Component.text("Set the %s speed to ".formatted(mode), Colors.SUCCESS)
                        .append(Component.text(speed, Colors.TERTIARY))
                        .append(Component.text(" for ", Colors.SUCCESS))
                        .append(Component.text(successes, Colors.TERTIARY))
                        .append(Component.text(" players", Colors.SUCCESS)));
            }
        }
    }
}
