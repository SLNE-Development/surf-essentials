package dev.slne.surf.essentials.commands.cheat;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.event.weather.LightningStrikeEvent;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

@PermissionTag(name = Permissions.LIGHTING_PERMISSION, desc = "Allows you to summon lighting")
public class LightningCommand {
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("lighting", LightningCommand::literal);
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(stack -> stack.getBukkitSender().hasPermission(""));

        literal.then(Commands.argument("players", EntityArgument.players())
                .executes(context -> lightingCustom(context, EntityArgument.getPlayers(context, "players"), new AtomicInteger(1)))

                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 20))
                        .executes(context -> lightingCustom(context, EntityArgument.getPlayers(context, "players"), new AtomicInteger(IntegerArgumentType.getInteger(context, "amount"))))));
    }

    private static int lightingCustom(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targetsUnchecked, AtomicInteger power) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(context.getSource(), targetsUnchecked);

        for (ServerPlayer serverPlayer : targets) {

            serverPlayer.setPlayerWeather(WeatherType.DOWNFALL, true);

            Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
                if (power.get() < 1) bukkitTask.cancel();
                serverPlayer.getLevel().strikeLightning(serverPlayer, LightningStrikeEvent.Cause.COMMAND);
                power.getAndDecrement();
            }, 20, 5);

            Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), bukkitTask ->
                    serverPlayer.resetPlayerWeather(), 20L *power.get() + 40);
        }

        if (context.getSource().isPlayer()){

            if (targets.size() == 1){
                EssentialsUtil.sendSuccess(context.getSource(), Component.text("Der Blitz hat ", SurfColors.SUCCESS)
                        .append(targets.iterator().next().adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                        .append(Component.text(" getroffen!", SurfColors.SUCCESS)));
            }else {
                EssentialsUtil.sendSuccess(context.getSource(), Component.text("Der Blitz hat ", SurfColors.SUCCESS)
                        .append(Component.text(targets.size(), SurfColors.TERTIARY))
                        .append(Component.text(" Spieler getroffen.", SurfColors.SUCCESS)));
            }
        }

        return 1;
    }
}
