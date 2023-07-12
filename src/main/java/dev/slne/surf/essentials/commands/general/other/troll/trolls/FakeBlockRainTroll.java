package dev.slne.surf.essentials.commands.general.other.troll.trolls;

/**
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.commands.general.other.troll.Troll;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.abtract.PacketUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class FakeBlockRainTroll extends Troll {
    @Override
    public String name() {
        return "fakeBlock";
    }

    @Override
    public String permission() {
        return Permissions.TROLL_FAKE_BLOCK_PERMISSION;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> troll() {
        return Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("fakeBlock", ResourceArgument.resource(EssentialsUtil.buildContext(), Registries.BLOCK))
                        .executes(context -> spawnBlock(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(),
                                ResourceArgument.getResource(context, "fakeBlock", Registries.BLOCK), 60))

                        .then(Commands.argument("time", IntegerArgumentType.integer(1, 3600))
                                .executes(context -> spawnBlock(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(),
                                        ResourceArgument.getResource(context, "fakeBlock", Registries.BLOCK),
                                        IntegerArgumentType.getInteger(context, "time")))));
    }

    @SuppressWarnings("SameReturnValue")
    private int spawnBlock(CommandContext<CommandSourceStack> context, Player target, Holder.Reference<Block> blockReference, int timeInSeconds) throws CommandSyntaxException {
        EssentialsUtil.checkPlayerSuggestion(context.getSource(), PacketUtil.toServerPlayer(target));
        CommandSourceStack source = context.getSource();

        if (!getAndToggleTroll(target)) {
            AtomicInteger timeLeft = new AtomicInteger(timeInSeconds * 2);

            Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
                if (timeLeft.get() < 1) {
                    bukkitTask.cancel();
                    stopTroll(target);
                }
                Location location = target.getLocation();
                Location blockPosition = new Location(location.getWorld(), location.getX(), location.getY() + 5, location.getZ());

                if (blockPosition.getBlock().getType() == Material.AIR) {
                    try {
                        EssentialsUtil.spawnFakeFallingBlock(target, blockReference.value(), blockPosition, Duration.ofSeconds(timeInSeconds));
                    } catch (CommandSyntaxException ignored) {
                    }
                }
                timeLeft.getAndDecrement();
                TASK_IDS.put(target.getUniqueId(), bukkitTask.getTaskId());
            }, 1, 10);

            Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), bukkitTask -> PLAYER_IN_TROLL.remove(target.getUniqueId()), 20L * timeInSeconds);

        } else {
            stopTroll(target);

            EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(target)
                    .append(Component.text(" wird nun nicht mehr mit ", Colors.INFO))
                    .append(PaperAdventure.asAdventure(blockReference.value().getName()))
                    .append(Component.text(" beworfen!", Colors.INFO)));

            return 1;
        }

        EssentialsUtil.sendSuccess(source, (Component.text("Bei ", Colors.SUCCESS))
                .append(EssentialsUtil.getDisplayName(target))
                .append(Component.text(" regnet es jetzt ", Colors.SUCCESS)
                        .append(PaperAdventure.asAdventure(blockReference.value().getName()))));

        return 1;

    }
}
 */
