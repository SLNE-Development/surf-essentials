package dev.slne.surf.essentialsold.commands.general.other.troll.trolls;

/**
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.commands.general.other.troll.Troll;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class BellTroll extends Troll {
    @Override
    public String name() {
        return "bell";
    }

    @Override
    public String permission() {
        return Permissions.TROLL_BELL_PERMISSION;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> troll() {
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> executeTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(), 60))
                .then(Commands.argument("time", IntegerArgumentType.integer(1, 3600))
                        .executes(context -> executeTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(),
                                IntegerArgumentType.getInteger(context, "time"))));
    }

    @SuppressWarnings("SameReturnValue")
    private int executeTroll(CommandContext<CommandSourceStack> context, Player target, int timeInSeconds) throws CommandSyntaxException {
        EssentialsUtil.checkPlayerSuggestion(context.getSource(), EssentialsUtil.toServerPlayer(target));
        CommandSourceStack source = context.getSource();

        if (getAndToggleTroll(target)) {
            stopTroll(target);

            EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(target)
                    .append(Component.text(" wird nun nicht mehr mit Glockengeräuschen gestört!", Colors.SUCCESS)));
            return 1;
        }

        AtomicInteger timeLeft = new AtomicInteger(timeInSeconds * 4);
        UUID targetUUID = target.getUniqueId();

        Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
            if (timeLeft.get() < 0) {
                bukkitTask.cancel();
                stopTroll(target);
            }
            final var targetLocation = target.getLocation();

            EssentialsUtil.sendPackets(
                    target,
                    new ClientboundSoundPacket(
                            Holder.direct(SoundEvents.BELL_BLOCK),
                            SoundSource.MASTER,
                            targetLocation.x(),
                            targetLocation.y(),
                            targetLocation.z(),
                            10f, // volume
                            1, // pitch
                            0) // seed
            );

            timeLeft.getAndDecrement();
            TASK_IDS.put(targetUUID, bukkitTask.getTaskId());
        }, 1, 5);


        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(target)
                .append(Component.text(" wird nun mit Glockengeräuschen genervt!", Colors.SUCCESS)));

        return 1;
    }
}
 */

