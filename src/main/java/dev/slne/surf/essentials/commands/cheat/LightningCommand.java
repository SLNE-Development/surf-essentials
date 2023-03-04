package dev.slne.surf.essentials.commands.cheat;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class LightningCommand {
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("lighting", LightningCommand::literal);
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(stack -> stack.getBukkitSender().hasPermission(Permissions.LIGHTING_PERMISSION));

        literal.then(Commands.argument("players", EntityArgument.players())
                .executes(context -> lightingCustom(context, EntityArgument.getPlayers(context, "players"), new AtomicInteger(1)))

                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 20))
                        .executes(context -> lightingCustom(context, EntityArgument.getPlayers(context, "players"), new AtomicInteger(IntegerArgumentType.getInteger(context, "amount"))))));
    }

    private static int lightingCustom(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targetsUnchecked, AtomicInteger power) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(context.getSource(), targetsUnchecked);

        for (ServerPlayer serverPlayer : targets) {
            Player player = serverPlayer.getBukkitEntity();
            player.setPlayerWeather(WeatherType.DOWNFALL);

            Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
                if (power.get() < 1) bukkitTask.cancel();
                player.getWorld().strikeLightning(player.getLocation());
                power.getAndDecrement();
            }, 20, 5);

            Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), bukkitTask ->
                    player.resetPlayerWeather(), 20L * power.get() + 40);
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
