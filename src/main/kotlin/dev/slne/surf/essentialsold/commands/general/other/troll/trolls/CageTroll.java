package dev.slne.surf.essentialsold.commands.general.other.troll.trolls;

/**
import com.mojang.brigadier.arguments.BoolArgumentType;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class CageTroll extends Troll {
    @Override
    public String name() {
        return "cage";
    }

    @Override
    public String permission() {
        return Permissions.TROLL_CAGE_PERMISSION;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> troll() {
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> executeCage(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(), 60, false))
                .then(Commands.argument("time", IntegerArgumentType.integer(1, 6400))
                        .executes(context -> executeCage(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(),
                                IntegerArgumentType.getInteger(context, "time"), false))
                        .then(Commands.argument("force", BoolArgumentType.bool())
                                .executes(context -> executeCage(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(),
                                        IntegerArgumentType.getInteger(context, "time"), BoolArgumentType.getBool(context, "force")))));
    }

    @SuppressWarnings("SameReturnValue")
    private int executeCage(CommandContext<CommandSourceStack> context, Player target, int timeInSeconds, boolean force) throws CommandSyntaxException {
        EssentialsUtil.checkPlayerSuggestion(context.getSource(), PacketUtil.toServerPlayer(target));
        CommandSourceStack source = context.getSource();

        if (getAndToggleTroll(target)) {
            stopTroll(target);

            EssentialsUtil.sendSourceSuccess(source, EssentialsUtil.getDisplayName(target)
                    .append(Component.text(" wurde befreit!", Colors.SUCCESS)));

            return 1;
        }

        Material cageMaterial = Material.GLASS;
        Location entityLocation = target.getLocation();

        int sideLength = 3;
        int height = 3;

        int delta = sideLength / 2;
        Location corner1 = new Location(entityLocation.getWorld(), entityLocation.getBlockX() + delta, entityLocation.getBlockY() + 1,
                entityLocation.getBlockZ() - delta);
        Location corner2 = new Location(entityLocation.getWorld(), entityLocation.getBlockX() - delta, entityLocation.getBlockY() + 1,
                entityLocation.getBlockZ() + delta);
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for (int x = minX; x <= maxX; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    Block block;
                    if (x == minX || x == maxX || z == minZ || z == maxZ) {
                        block = corner1.getWorld().getBlockAt(x, entityLocation.getBlockY() + y, z);
                        if (block.getType() != Material.AIR && !force) continue;
                        block.setType(cageMaterial);
                    }

                    if (y == height - 1) {
                        block = corner1.getWorld().getBlockAt(x, entityLocation.getBlockY() + y + 1, z);
                        if (block.getType() != Material.AIR && !force) continue;
                        block.setType(cageMaterial);
                    }
                }
            }
        }

        double middleX = Math.round((minX + maxX) / 2.0 * 2) / 2.0 + 0.5;
        double middleY = Math.round(entityLocation.getY() * 2) / 2.0;
        double middleZ = Math.round((minZ + maxZ) / 2.0 * 2) / 2.0 + 0.5;
        Location middle = new Location(entityLocation.getWorld(), middleX, middleY, middleZ, entityLocation.getYaw(), entityLocation.getPitch());

        target.teleportAsync(middle, PlayerTeleportEvent.TeleportCause.COMMAND);
        Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), () -> stopTroll(target), 20L * timeInSeconds);

        EssentialsUtil.sendSourceSuccess(source, target.displayName().colorIfAbsent(Colors.YELLOW)
                .append(Component.text(" sitzt jetzt in der Falle!", Colors.SUCCESS)));

        return 1;
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isInTroll(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (!isInTroll(event.getPlayer())) return;
        event.setCancelled(true);
    }
}
*/