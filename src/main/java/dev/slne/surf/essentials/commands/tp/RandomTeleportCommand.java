package dev.slne.surf.essentials.commands.tp;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import dev.slne.surf.essentials.utils.brigadier.BrigadierCommand;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

@PermissionTag(name = Permissions. TELEPORT_RANDOM_PERMISSION, desc = "Allows you to teleport to a random location")
public class RandomTeleportCommand extends BrigadierCommand {

    private static final HashSet<Material> unsafeMaterials = new HashSet<>(List.of(Material.LAVA, Material.FIRE, Material.CACTUS,
            Material.MAGMA_BLOCK, Material.POWDER_SNOW, Material.CAMPFIRE, Material.SOUL_CAMPFIRE, Material.SPAWNER, Material.SCULK_SHRIEKER,
            Material.SCULK_SENSOR, Material.COBWEB, Material.LAVA_CAULDRON, Material.OAK_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATE, Material.BIRCH_PRESSURE_PLATE,
            Material.JUNGLE_PRESSURE_PLATE, Material.ACACIA_PRESSURE_PLATE, Material.DARK_OAK_PRESSURE_PLATE, Material.MANGROVE_PRESSURE_PLATE, Material.CRIMSON_PRESSURE_PLATE,
            Material.WARPED_PRESSURE_PLATE, Material.STONE_PRESSURE_PLATE, Material.POLISHED_BLACKSTONE_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
            Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.TNT));

    @Override
    public String[] names() {
        return new String[]{"rtp", "tpr", "wild"};
    }

    @Override
    public String usage() {
        return "/tpr [<maxRadius>]";
    }

    @Override
    public String description() {
        return "Teleports you to a random location";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.TELEPORT_RANDOM_PERMISSION));

        literal.executes(context -> teleportRandom(context.getSource(), null));

        literal.then(Commands.argument("maxRadius", IntegerArgumentType.integer(2, 20000))
                .executes(context -> teleportRandom(context.getSource(), IntegerArgumentType.getInteger(context, "maxRadius"))));
    }

    private int teleportRandom(CommandSourceStack source, Integer maxRadius) throws CommandSyntaxException {
        ServerPlayer playerToTeleport = source.getPlayerOrException();
        Player bukkitPlayer = playerToTeleport.getBukkitEntity();
        maxRadius = (maxRadius == null) ? 5000 : maxRadius;

        SurfApi.getUser(playerToTeleport.getUUID()).thenAccept(user -> user.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Suche Ort...", SurfColors.INFO))));

        Location randomLocation = generateLocation(bukkitPlayer, maxRadius);

        if (randomLocation == null){
            EssentialsUtil.sendError(source, "Es wurde kein Sicherer Ort gefunden!");
            return 0;
        }

        randomLocation.setX(randomLocation.getX() + 0.5);
        randomLocation.setZ(randomLocation.getZ() + 0.5);

        bukkitPlayer.teleportAsync(randomLocation).thenAccept(aBoolean -> {
            double playerX = EssentialsUtil.makeDoubleReadable(randomLocation.getX());
            double playerY = EssentialsUtil.makeDoubleReadable(randomLocation.getY());
            double playerZ = EssentialsUtil.makeDoubleReadable(randomLocation.getZ());

            try {
                EssentialsUtil.sendSuccess(source, Component.text("Du wurdest zu ", SurfColors.SUCCESS)
                        .append(Component.text("%s %s %s".formatted(playerX, playerY, playerZ), SurfColors.TERTIARY))
                        .append(Component.text(" teleportiert!", SurfColors.SUCCESS)));
            } catch (CommandSyntaxException ignored) {}
        });

        return 1;
    }

    // TODO: Check the performance impact
    int trys = 0;
    private Location generateLocation(Player player, int radius) {
        Random random = new Random();

        int x = random.nextInt(player.getLocation().getBlockX() - (radius / 2), player.getLocation().getBlockX() + (radius / 2)),
                z = random.nextInt(player.getLocation().getBlockZ() - (radius / 2), player.getLocation().getBlockZ() + (radius / 2)),
                y = player.getWorld().getHighestBlockYAt(x, z) + 1;

        Location randomLocation = new Location(player.getWorld(), x, y, z);

        while (!isLocationSafe(randomLocation)){
            if (trys > 10) return null;
            trys ++;
            generateLocation(player, radius);
        }

        return randomLocation;
    }

    private boolean isLocationSafe(Location location){
        int x = location.getBlockX(),
                y = location.getBlockY(),
                z = location.getBlockZ();

        Block block = location.getWorld().getBlockAt(x, y, z);
        Block below = location.getWorld().getBlockAt(x, y - 1, z);
        Block above = location.getWorld().getBlockAt(x, y + 1, z);

        if ((location.getWorld() == Bukkit.getWorlds().get(2)) && (location.getBlockY() == 0)) return false;
        if (!location.getWorld().getWorldBorder().isInside(location)) return false;

        return !(unsafeMaterials.contains(below.getType())) || (block.getType().isSolid()) || (above.getType().isSolid());
    }
}
