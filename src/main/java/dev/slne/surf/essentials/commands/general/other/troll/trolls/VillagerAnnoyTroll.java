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
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;


public class VillagerAnnoyTroll extends Troll {
    @Override
    public String name() {
        return "villager";
    }

    @Override
    public String permission() {
        return Permissions.TROLL_VILLAGER_PERMISSION;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> troll() {
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> annoyVillager(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(), 60))
                .then(Commands.argument("time", IntegerArgumentType.integer(1, 3600))
                        .executes(context -> annoyVillager(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(),
                                IntegerArgumentType.getInteger(context, "time"))));
    }

    private int annoyVillager(CommandContext<CommandSourceStack> context, Player target, int timeInSeconds) throws CommandSyntaxException {
        EssentialsUtil.checkPlayerSuggestion(context.getSource(), CraftUtil.toServerPlayer(target));
        CommandSourceStack source = context.getSource();

        if (!getAndToggleTroll(target)){
            AtomicInteger timeLeft = new AtomicInteger(timeInSeconds*4);

            Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
                if (timeLeft.get() < 1) bukkitTask.cancel();

                target.playSound(target, Sound.ENTITY_VILLAGER_NO, 100.F, 1.0F);
                target.playSound(target, Sound.ENTITY_VILLAGER_CELEBRATE, 100.F, 1.0F);
                target.playSound(target, Sound.ENTITY_VILLAGER_AMBIENT, 100.F, 1.0F);

                timeLeft.getAndDecrement();
                TASK_IDS.put(target.getUniqueId(), bukkitTask.getTaskId());
            },1,5);

            Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), bukkitTask -> stopTroll(target), 20L * timeInSeconds);

            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(target)
                        .append(Component.text(" wird nun mit Dorfbewohner-geräuschen gestört!", Colors.SUCCESS)));
            }else{
                source.sendSuccess(EssentialsUtil.getMinecraftDisplayName(target)
                        .copy().append(net.minecraft.network.chat.Component.literal(" is now disturbed with villager noises!")
                                .withStyle(ChatFormatting.GREEN)), false);
            }

        }else {
            stopTroll(target);

            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(target)
                        .append(Component.text(" wird nun nicht mehr mit Dorfbewohner-geräuschen gestört", Colors.INFO)));
            }else {
                source.sendSuccess(EssentialsUtil.getMinecraftDisplayName(target)
                        .copy().append(net.minecraft.network.chat.Component.literal(" is no longer disturbed with villager noises!")
                                .withStyle(ChatFormatting.GREEN)), false);
            }
        }
        return 1;
    }
}
