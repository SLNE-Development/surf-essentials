package dev.slne.surf.essentials.commands.general.other.troll.trolls;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@DefaultQualifier(NotNull.class)
public class AnvilTroll implements Listener {
    private static final HashMap<UUID, Boolean> playersInTroll = new HashMap<>();
    private static final HashMap<Double, Double> anvilLocation = new HashMap<>();
    private static int anvilTaskID;

    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> anvil(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(stack -> stack.getBukkitSender().hasPermission("surf.essentials.commands.troll.anvil"));
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> dropAnvil(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(), 60))
                .then(Commands.argument("time", IntegerArgumentType.integer(1, 3600))
                        .executes(context -> dropAnvil(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(),
                                IntegerArgumentType.getInteger(context, "time"))));
    }

    private static int dropAnvil(CommandContext<CommandSourceStack> context, Player target, int timeInSeconds) throws CommandSyntaxException {
        EssentialsUtil.checkSinglePlayerSuggestion(context.getSource(), ((CraftPlayer) target).getHandle());
        CommandSourceStack source = context.getSource();

        boolean isInTroll = playersInTroll.get(target.getUniqueId()) != null ? playersInTroll.get(target.getUniqueId()) : false;

        if (!isInTroll){
            playersInTroll.put(target.getUniqueId(), true);

            Material anvil = Material.DAMAGED_ANVIL;
            AtomicInteger timeLeft = new AtomicInteger(timeInSeconds*2);

            Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
                if (timeLeft.get() < 1) bukkitTask.cancel();
                Location location = target.getLocation();
                Location blockPosition = new Location(location.getWorld(), location.getX(), location.getY() + 20, location.getZ());

                if (blockPosition.getBlock().getType() == Material.AIR){
                    blockPosition.getBlock().setType(anvil, true);
                    anvilLocation.put(blockPosition.getX(), blockPosition.getY());
                }
                timeLeft.getAndDecrement();
                anvilTaskID = bukkitTask.getTaskId();
            },1,10);

            Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), bukkitTask -> playersInTroll.put(target.getUniqueId(), false), 20L * timeInSeconds);

        }else {
            cancelAnvilTroll(target);

            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, (target.displayName().colorIfAbsent(Colors.YELLOW))
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
                    .append(target.displayName().colorIfAbsent(Colors.YELLOW))
                    .append(Component.text(" regnet es jetzt Ambosse!", Colors.SUCCESS)));
        }else{
            source.sendSuccess(EntityArgument.getPlayer(context, "player").getDisplayName()
                    .copy().append(net.minecraft.network.chat.Component.literal(" is thrown with anvils!")
                            .withStyle(ChatFormatting.GREEN)), false);
        }
        return 1;
    }

    public static void cancelAnvilTroll(Player target){
        Bukkit.getScheduler().cancelTask(anvilTaskID);
        playersInTroll.put(target.getUniqueId(), false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (event.getBlock().getType() != Material.DAMAGED_ANVIL) return;
        Location blockLocation = event.getBlock().getLocation();
        if (anvilLocation.get(blockLocation.getX()) != null && anvilLocation.get(blockLocation.getZ()) == blockLocation.getZ()){
            anvilLocation.remove(blockLocation.getX(), blockLocation.getZ());
            event.getBlock().setType(Material.AIR);
            System.out.println("yes");
        }

    }
}
