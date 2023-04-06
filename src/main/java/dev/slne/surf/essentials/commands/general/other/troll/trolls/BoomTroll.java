package dev.slne.surf.essentials.commands.general.other.troll.trolls;

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
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class BoomTroll {
    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> boom(@NotNull LiteralArgumentBuilder<CommandSourceStack> literal){
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> makeBoom(context, EntityArgument.getPlayer(context, "player")));
    }

    private static int makeBoom(@NotNull CommandContext<CommandSourceStack> context, @NotNull ServerPlayer serverPlayer) throws CommandSyntaxException {
        EssentialsUtil.checkPlayerSuggestion(context.getSource(), serverPlayer);
        CommandSourceStack source = context.getSource();
        Player target = serverPlayer.getBukkitEntity();
        Location location = target.getLocation();
        ClientboundExplodePacket explodePacket = new ClientboundExplodePacket(location.getX(), location.getY(), location.getZ(), 2F, Collections.emptyList(), null);

        serverPlayer.connection.send(explodePacket);

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
