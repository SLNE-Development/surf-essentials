package dev.slne.surf.essentials.commands.general.other.troll.trolls;

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
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class BoomTroll extends Troll {
    @Override
    public String name() {
        return "boom";
    }

    @Override
    public String permission() {
        return Permissions.TROLL_BOOM_PERMISSION;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> troll() {
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> makeBoom(context, EntityArgument.getPlayer(context, "player")));
    }

    private static int makeBoom(@NotNull CommandContext<CommandSourceStack> context, @NotNull ServerPlayer serverPlayer) throws CommandSyntaxException {
        EssentialsUtil.checkPlayerSuggestion(context.getSource(), serverPlayer);
        CommandSourceStack source = context.getSource();
        Player target = serverPlayer.getBukkitEntity();
        Location location = target.getLocation();
        ClientboundExplodePacket explodePacket = new ClientboundExplodePacket(location.getX(), location.getY(), location.getZ(), 2F, Collections.emptyList(), null);

        EssentialsUtil.sendPackets(serverPlayer, explodePacket);

        target.setInvulnerable(true);
        target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 127, false, false, false));

        Bukkit.getScheduler().runTaskLater(SurfEssentials.getInstance(), bukkitTask ->
                target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * 2, 128, false, false, false)), 20);

        Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), bukkitTask ->
                target.setInvulnerable(false), 20 * 2);


        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(target)
                .append(Component.text(" wurde gesprengt!", Colors.SUCCESS)));

        return 1;
    }
}
