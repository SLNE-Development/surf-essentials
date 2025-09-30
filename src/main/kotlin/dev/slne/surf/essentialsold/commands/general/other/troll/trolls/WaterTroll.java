package dev.slne.surf.essentialsold.commands.general.other.troll.trolls;

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
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class WaterTroll extends Troll {
    @Override
    public String name() {
        return "water";
    }

    @Override
    public String permission() {
        return Permissions.TROLL_WATER_PERMISSION;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> troll() {
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> waterTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity().getPlayer(), 60))
                .then(Commands.argument("time", IntegerArgumentType.integer(1, 3600))
                        .executes(context -> waterTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity().getPlayer(),
                                IntegerArgumentType.getInteger(context, "time"))));
    }

    @SuppressWarnings("SameReturnValue")
    private int waterTroll(CommandContext<CommandSourceStack> context, Player target, int timeInSeconds) throws CommandSyntaxException {
        EssentialsUtil.checkPlayerSuggestion(context.getSource(), PacketUtil.toServerPlayer(target));
        CommandSourceStack source = context.getSource();

        if (getAndToggleTroll(target)) {
            stopTroll(target);

            EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(target)
                    .append(Component.text(" hat jetzt keine Wasserphobie mehr", Colors.SUCCESS)));

            return 1;
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), () -> stopTroll(target), 20L * timeInSeconds);


        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(target)
                .append(Component.text(" hat nun Wasserphobie!", Colors.SUCCESS)));

        return 1;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!isInTroll(player)) return;
        if (player.getLocation().getBlock().getType() != Material.WATER) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 4, 1, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 20, 1, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20, 15, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20, 15, false, false, false));
    }
}
 */
