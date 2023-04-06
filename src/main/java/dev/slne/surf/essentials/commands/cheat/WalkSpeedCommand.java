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

import java.util.Collection;
import java.util.Collections;

public class WalkSpeedCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"walkspeed", "speedwalk"};
    }

    @Override
    public String usage() {
        return "/walkspeed <speed | default> <players>";
    }

    @Override
    public String description() {
        return "Change the walk speed";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.WALK_SPEED_PERMISSION_SELF, Permissions.WALK_SPEED_PERMISSION_OTHER));
        literal.then(Commands.argument("speed", IntegerArgumentType.integer(-10, 10))
                        .executes(context -> speed(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()),
                                IntegerArgumentType.getInteger(context, "speed")))
                        .then(Commands.argument("players", EntityArgument.players())
                                .requires(EssentialsUtil.checkPermissions(Permissions.FLY_SPEED_PERMISSION_OTHER))
                                .executes(context -> speed(context.getSource(), EntityArgument.getPlayers(context, "players"),
                                        IntegerArgumentType.getInteger(context, "speed")))))

                .then(Commands.literal("default")
                        .executes(context -> speed(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), 2)));
    }

    private int speed(CommandSourceStack source, Collection<ServerPlayer> playersUnchecked, int speed) throws CommandSyntaxException {
        var players = EssentialsUtil.checkPlayerSuggestion(source, playersUnchecked);
        var calculatedSpeed = (float) speed / 10;
        int successes = 0;

        for (ServerPlayer player : players) {
            player.getBukkitEntity().setWalkSpeed(calculatedSpeed);
            EssentialsUtil.sendSuccess(player, Component.text("Deine Gehgeschwindigkeit wurde auf ", Colors.SUCCESS)
                    .append(Component.text(speed, Colors.TERTIARY))
                    .append(Component.text(" gesetzt", Colors.SUCCESS)));
            successes++;
        }

        if (source.isPlayer()){
            if (successes == 1 && source.getPlayerOrException().getUUID() != players.iterator().next().getUUID()){
                EssentialsUtil.sendSuccess(source, Component.text("Die Gehgeschwindigkeit von ", Colors.SUCCESS)
                        .append(EssentialsUtil.getDisplayName(players.iterator().next()))
                        .append(Component.text(" wurde auf ", Colors.SUCCESS))
                        .append(Component.text(speed, Colors.TERTIARY))
                        .append(Component.text(" gesetzt", Colors.SUCCESS)));
            }else if (source.getPlayerOrException().getUUID() != players.iterator().next().getUUID()){
                EssentialsUtil.sendSuccess(source, Component.text("Die Gehgeschwindigkeit von ", Colors.SUCCESS)
                        .append(Component.text(successes, Colors.TERTIARY))
                        .append(Component.text("  Spielern wurde auf ", Colors.SUCCESS))
                        .append(Component.text(speed, Colors.TERTIARY))
                        .append(Component.text(" gesetzt", Colors.SUCCESS)));
            }
        }else {
            if (successes == 1){
                EssentialsUtil.sendSourceSuccess(source, EssentialsUtil.getDisplayName(players.iterator().next())
                        .append(Component.text("´s walk speed was set to ", Colors.SUCCESS))
                        .append(Component.text(speed, Colors.TERTIARY)));
            }else {
                EssentialsUtil.sendSourceSuccess(source, Component.text("Set the walk speed to ", Colors.SUCCESS)
                        .append(Component.text(speed, Colors.TERTIARY))
                        .append(Component.text(" for ", Colors.SUCCESS))
                        .append(Component.text(successes, Colors.TERTIARY))
                        .append(Component.text(" players", Colors.SUCCESS)));
            }
        }
        return successes;
    }
}
