package dev.slne.surf.essentials.commands.general.other.troll.trolls;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
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
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class BoomTroll {
    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> boom(@NotNull LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(stack -> stack.getBukkitSender().hasPermission("surf.essentials.commands.troll.boom"));
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> makeBoom(context, EntityArgument.getPlayer(context, "player").getBukkitEntity()));
    }

    private static int makeBoom(@NotNull CommandContext<CommandSourceStack> context, @NotNull Player target) throws CommandSyntaxException {
        EssentialsUtil.checkSinglePlayerSuggestion(context.getSource(), ((CraftPlayer) target).getHandle());
        CommandSourceStack source = context.getSource();
        ProtocolManager manager = SurfEssentials.manager();
        Location location = target.getLocation();
        PacketContainer packet = manager.createPacket(PacketType.Play.Server.EXPLOSION);

        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY());
        packet.getDoubles().write(2, location.getZ());

        packet.getFloat().write(0, 2F);


        try {
            manager.sendServerPacket(target, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        target.setInvulnerable(true);
        target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 127, false, false, false));

        Bukkit.getScheduler().runTaskLater(SurfEssentials.getInstance(), bukkitTask ->
                target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20*2, 128, false, false, false)), 20);

        Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), bukkitTask ->
                target.setInvulnerable(false), 20*2);

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, (target.displayName().colorIfAbsent(Colors.YELLOW))
                    .append(Component.text(" wurde gesprengt!", Colors.SUCCESS)));
        }else{
            source.sendSuccess(EntityArgument.getPlayer(context, "player").getDisplayName()
                    .copy().append(net.minecraft.network.chat.Component.literal(" was blown up!")
                            .withStyle(ChatFormatting.GREEN)), false);
        }
        return 1;
    }
}
