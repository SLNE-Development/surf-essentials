package dev.slne.surf.essentials.commands.cheat;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;

public class SpeedCommand {

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("speed", SpeedCommand::literal).setUsage("/speed [<target>]")
                .setDescription("changes the walk and fly speed of the target");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.SPEED_SELF_PERMISSION));

        literal.then(Commands.argument("speed", FloatArgumentType.floatArg(-1, 1))
                .executes(context -> speed(context.getSource(), context.getSource().getPlayerOrException(), FloatArgumentType.getFloat(context, "speed")))
                .then(Commands.argument("player", EntityArgument.player())
                        .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.SPEED_OTHER_PERMISSION))
                        .executes(context -> speed(context.getSource(), EntityArgument.getPlayer(context, "player"), FloatArgumentType.getFloat(context, "speed")))))
                .then(Commands.literal("default")
                        .executes(context -> speed(context.getSource(), context.getSource().getPlayerOrException(), null)));
    }

    private static int speed(CommandSourceStack source, ServerPlayer targetUnchecked, Float speed) throws CommandSyntaxException {
        ServerPlayer target = EssentialsUtil.checkSinglePlayerSuggestion(source, targetUnchecked);
        Player bukkitTarget = target.getBukkitEntity();
        if (speed == null){ //reset the speed to default
            bukkitTarget.setFlySpeed(0.1f);
            bukkitTarget.setWalkSpeed(0.2f);

            if (source.isPlayer()){
                if (source.getPlayerOrException() == target){
                    EssentialsUtil.sendSuccess(source, "Deine Geh- und Fluggeschwindigkeit wurde zurückgesetzt!");
                }else {
                    SurfApi.getUser(target.getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Deine Geh- und Fluggeschwindigkeit wurde zurückgesetzt!", SurfColors.INFO))));

                    EssentialsUtil.sendSuccess(source, Component.text("Die Geh- und Fluggeschwindigkeit von ", SurfColors.SUCCESS)
                            .append(target.adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                            .append(Component.text(" wurde zurückgesetzt!", SurfColors.SUCCESS)));
                }
            }else {
                source.sendSuccess(target.getDisplayName()
                        .copy().append(net.minecraft.network.chat.Component.literal("´s walking and flying speed has been reset")), false);
            }
            return 1;
        }

        bukkitTarget.setFlySpeed(speed);
        bukkitTarget.setWalkSpeed(speed);

        if (source.isPlayer()){
            if (source.getPlayerOrException() == target){
                EssentialsUtil.sendSuccess(source, Component.text("Deine Geh- und Fluggeschwindigkeit wurde auf ", SurfColors.SUCCESS)
                        .append(Component.text(speed, SurfColors.TERTIARY))
                        .append(Component.text(" gesetzt!", SurfColors.SUCCESS)));
            }else {
                SurfApi.getUser(target.getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Deine Geh- und Fluggeschwindigkeit wurde auf ", SurfColors.INFO))
                        .append(Component.text(speed, SurfColors.TERTIARY))
                        .append(Component.text(" gesetzt!", SurfColors.INFO))));

                EssentialsUtil.sendSuccess(source, Component.text("Die Geh- und Fluggeschwindigkeit von ", SurfColors.SUCCESS)
                        .append(target.adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                        .append(Component.text(" wurde auf ", SurfColors.SUCCESS))
                        .append(Component.text(speed, SurfColors.TERTIARY))
                        .append(Component.text(" gesetzt!", SurfColors.SUCCESS)));
            }
        }else {
            source.sendSuccess(target.getDisplayName()
                    .copy().append(net.minecraft.network.chat.Component.literal("´s walking and flying speed has been set to " + speed)), false);
        }
        return 1;
    }
}
