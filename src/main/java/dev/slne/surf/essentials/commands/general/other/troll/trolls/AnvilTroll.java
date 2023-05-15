package dev.slne.surf.essentials.commands.general.other.troll.trolls;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.commands.general.other.troll.Troll;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.abtract.CraftUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

@DefaultQualifier(NotNull.class)
public class AnvilTroll extends Troll {
    @Override
    public String name() {
        return "anvil";
    }

    @Override
    public String permission() {
        return Permissions.TROLL_ANVIL_PERMISSION;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> troll() {
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> dropAnvil(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(), 60))
                .then(Commands.argument("time", IntegerArgumentType.integer(1, 3600))
                        .executes(context -> dropAnvil(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(),
                                IntegerArgumentType.getInteger(context, "time"))));
    }

    @SuppressWarnings("SameReturnValue")
    private int dropAnvil(CommandContext<CommandSourceStack> context, Player target, int timeInSeconds) throws CommandSyntaxException {
        EssentialsUtil.checkPlayerSuggestion(context.getSource(), CraftUtil.toServerPlayer(target));
        CommandSourceStack source = context.getSource();

        if (!getAndToggleTroll(target)){
            AtomicInteger timeLeft = new AtomicInteger(timeInSeconds*2);

            Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
                if (timeLeft.get() < 1) bukkitTask.cancel();
                Location location = target.getLocation();
                Location blockPosition = new Location(location.getWorld(), location.getX(), location.getY() + 20, location.getZ());

                if (blockPosition.getBlock().getType() == Material.AIR){
                    EssentialsUtil.spawnFakeFallingBlock(target, Blocks.ANVIL, blockPosition);
                }
                timeLeft.getAndDecrement();
                TASK_IDS.put(target.getUniqueId(), bukkitTask.getTaskId());
            },1,10);

            Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), bukkitTask -> PLAYER_IN_TROLL.remove(target.getUniqueId()), 20L * timeInSeconds);

        }else {
            stopTroll(target);

            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(target)
                        .append(Component.text(" wird nun nicht mehr mit Ambossen beworfen", Colors.INFO)));

            }else {
                source.sendSuccess(EntityArgument.getPlayer(context, "player").getDisplayName()
                        .copy().append(net.minecraft.network.chat.Component.literal(" is no longer thrown with anvils!")
                                .withStyle(ChatFormatting.GREEN)), false);
            }
            return 1;
        }

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, (Component.text("Bei ", Colors.SUCCESS))
                    .append(EssentialsUtil.getDisplayName(target))
                    .append(Component.text(" regnet es jetzt Ambosse!", Colors.SUCCESS)));
        }else{
            source.sendSuccess(EntityArgument.getPlayer(context, "player").getDisplayName()
                    .copy().append(net.minecraft.network.chat.Component.literal(" is thrown with anvils!")
                            .withStyle(ChatFormatting.GREEN)), false);
        }
        return 1;
    }
}
