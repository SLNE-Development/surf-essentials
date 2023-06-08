package dev.slne.surf.essentials.commands.cheat;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.event.weather.LightningStrikeEvent;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class LightningCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"lighting"};
    }

    @Override
    public String usage() {
        return "/lighting <players> [<amount>]";
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(stack -> stack.getBukkitSender().hasPermission(Permissions.LIGHTING_PERMISSION));

        literal.then(Commands.argument("players", EntityArgument.players())
                .executes(context -> lightingCustom(context, EntityArgument.getPlayers(context, "players"), new AtomicInteger(1), true))

                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 20))
                        .executes(context -> lightingCustom(context, EntityArgument.getPlayers(context, "players"), new AtomicInteger(IntegerArgumentType.getInteger(context, "amount")), true))

                        .then(Commands.literal("realLighting")
                                .executes(context -> lightingCustom(context, EntityArgument.getPlayers(context, "players"), new AtomicInteger(IntegerArgumentType.getInteger(context, "amount")), false)))));
    }


    private int lightingCustom(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targetsUnchecked, AtomicInteger power, boolean visual) throws CommandSyntaxException {
        final var targets = EssentialsUtil.checkPlayerSuggestion(context.getSource(), targetsUnchecked);

        for (ServerPlayer serverPlayer : targets) {
            serverPlayer.setPlayerWeather(WeatherType.DOWNFALL, true);

            Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
                if (power.get() < 1) bukkitTask.cancel();
                strikeLighting(serverPlayer, power.get(), visual);
                power.getAndDecrement();
            }, 20, 5);


            Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), bukkitTask ->
                    serverPlayer.resetPlayerWeather(), 20L * power.get() + 40);
        }

        if (targets.size() == 1) {
            EssentialsUtil.sendSuccess(context.getSource(), Component.text("Der Blitz hat ", Colors.SUCCESS)
                    .append(EssentialsUtil.getDisplayName(targets.iterator().next()))
                    .append(Component.text(" getroffen!", Colors.SUCCESS)));
        } else {
            EssentialsUtil.sendSuccess(context.getSource(), Component.text("Der Blitz hat ", Colors.SUCCESS)
                    .append(Component.text(targets.size(), Colors.TERTIARY))
                    .append(Component.text(" Spieler getroffen.", Colors.SUCCESS)));
        }

        return 1;
    }

    private void strikeLighting(final ServerPlayer player, int flashes, boolean visual) {
        final var world = player.serverLevel();
        final var playerLocation = player.position();
        final var lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, world);

        lightning.setCause(player);
        lightning.flashes = EssentialsUtil.getRandomInt(3) + flashes;
        lightning.teleportTo(playerLocation.x(), playerLocation.y(), playerLocation.z());

        if (visual) {
            EssentialsUtil.sendPackets(
                    player,
                    new ClientboundAddEntityPacket(lightning),
                    new ClientboundTeleportEntityPacket(lightning)
            );
        } else {
            world.strikeLightning(lightning, LightningStrikeEvent.Cause.CUSTOM);
        }
    }
}
