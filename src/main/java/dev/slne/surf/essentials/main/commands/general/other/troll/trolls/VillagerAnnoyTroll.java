package dev.slne.surf.essentials.main.commands.general.other.troll.trolls;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@DefaultQualifier(NotNull.class)
public class VillagerAnnoyTroll {
    private static final HashMap<UUID, Boolean> playersInTroll = new HashMap<>();
    private static int villagerTaskID;

    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> villager(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(stack -> stack.getBukkitSender().hasPermission("surf.essentials.commands.troll.villager"));
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> annoyVillager(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(), 60))
                .then(Commands.argument("time", IntegerArgumentType.integer(1, 3600))
                        .executes(context -> annoyVillager(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(),
                                IntegerArgumentType.getInteger(context, "time"))));
    }

    private static int annoyVillager(CommandContext<CommandSourceStack> context, Player target, int timeInSeconds) throws CommandSyntaxException {
        EssentialsUtil.checkSinglePlayerSuggestion(context.getSource(), ((CraftPlayer) target).getHandle());
        CommandSourceStack source = context.getSource();

        boolean isInTroll = playersInTroll.get(target.getUniqueId()) != null ? playersInTroll.get(target.getUniqueId()) : false;

        if (!isInTroll){
            playersInTroll.put(target.getUniqueId(), true);

            AtomicInteger timeLeft = new AtomicInteger(timeInSeconds*4);

            Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
                if (timeLeft.get() < 1) bukkitTask.cancel();
                SurfApi.getUser(target).thenAcceptAsync(user -> {
                    user.playSound(Sound.ENTITY_VILLAGER_NO, 100.F, 1.0F);
                    user.playSound(Sound.ENTITY_VILLAGER_CELEBRATE, 100.F, 1.0F);
                    user.playSound(Sound.ENTITY_VILLAGER_AMBIENT, 100.F, 1.0F);
                });
                timeLeft.getAndDecrement();
                villagerTaskID = bukkitTask.getTaskId();
            },1,5);

            Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), bukkitTask -> playersInTroll.put(target.getUniqueId(), false), 20L * timeInSeconds);

        }else {
            cancelVillagerTroll(target);

            if (source.isPlayer()){
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(target.displayName().colorIfAbsent(SurfColors.YELLOW))
                        .append(Component.text(" wird nun nicht mehr mit Dorfbewohner-geräuschen gestört", SurfColors.INFO))));
            }else {
                source.sendSuccess(EntityArgument.getPlayer(context, "player").getDisplayName()
                        .copy().append(net.minecraft.network.chat.Component.literal(" is no longer disturbed with villager noises!")
                                .withStyle(ChatFormatting.GREEN)), false);
            }
            return 1;
        }

        if (source.isPlayer()){
            SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(target.displayName().colorIfAbsent(SurfColors.YELLOW))
                    .append(Component.text(" wird nun mit Dorfbewohner-geräuschen gestört!", SurfColors.SUCCESS))));
        }else{
            source.sendSuccess(EntityArgument.getPlayer(context, "player").getDisplayName()
                    .copy().append(net.minecraft.network.chat.Component.literal(" is now disturbed with villager noises!")
                            .withStyle(ChatFormatting.GREEN)), false);
        }
        return 1;
    }

    public static void cancelVillagerTroll(Player target){
        Bukkit.getScheduler().cancelTask(villagerTaskID);
        playersInTroll.put(target.getUniqueId(), false);
    }
}
