package dev.slne.surf.essentials.commands.general.other.troll.trolls;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
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

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class BellTroll {
    private static final HashMap<UUID, Integer> tasksIds = new HashMap<>();

    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> bell(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(stack -> stack.getBukkitSender().hasPermission("surf.essentials.commands.troll.bell"));
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> executeTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(), 60))
                .then(Commands.argument("time", IntegerArgumentType.integer(1, 3600))
                        .executes(context -> executeTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(),
                                IntegerArgumentType.getInteger(context, "time"))));
    }

    private static int executeTroll(CommandContext<CommandSourceStack> context, Player target, int timeInSeconds) throws CommandSyntaxException {
        EssentialsUtil.checkSinglePlayerSuggestion(context.getSource(), ((CraftPlayer) target).getHandle());
        CommandSourceStack source = context.getSource();

        //cancel troll if target is already in troll
        if (tasksIds.containsKey(target.getUniqueId())){
            stopBellTroll(target);

            if (source.isPlayer()){
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(target.displayName().colorIfAbsent(SurfColors.YELLOW))
                        .append(Component.text(" wird nun nicht mehr mit Glockengeräuschen gestört!", SurfColors.SUCCESS))));
            }else {
                source.sendSuccess(EntityArgument.getPlayer(context, "player").getDisplayName()
                        .copy().append(net.minecraft.network.chat.Component.literal(" will now no longer be annoyed with bell sounds!")
                                .withStyle(ChatFormatting.GREEN)), false);
            }
            return 1;
        }

        AtomicInteger timeLeft = new AtomicInteger(timeInSeconds * 4);
        UUID targetUUID = target.getUniqueId();

        Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
            if (timeLeft.get() < 0) {
                bukkitTask.cancel();
                tasksIds.remove(target.getUniqueId(), bukkitTask.getTaskId());
            }
            SurfApi.getUser(target).thenAcceptAsync(user -> user.playSound(Sound.BLOCK_BELL_USE, 10.0F, 1));

            timeLeft.getAndDecrement();
            tasksIds.put(targetUUID, bukkitTask.getTaskId());
        }, 1, 5);

        //success message
        if (source.isPlayer()){
            SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(target.displayName().colorIfAbsent(SurfColors.YELLOW))
                    .append(Component.text(" wird nun mit Glockengeräuschen genervt!", SurfColors.SUCCESS))));
        }else{
            source.sendSuccess(EntityArgument.getPlayer(context, "player").getDisplayName()
                    .copy().append(net.minecraft.network.chat.Component.literal(" is now annoyed with bell noises!")
                            .withStyle(ChatFormatting.GREEN)), false);
        }
        return 1;
    }

    public static void stopBellTroll(Player player){
        Bukkit.getScheduler().cancelTask(tasksIds.get(player.getUniqueId()));
        tasksIds.remove(player.getUniqueId(), tasksIds.get(player.getUniqueId()));
    }

    public static void stopAllBellTrolls(){
        tasksIds.forEach((uuid, integer) -> {
            Bukkit.getScheduler().cancelTask(integer);
            tasksIds.remove(uuid, integer);
        });
    }
}

