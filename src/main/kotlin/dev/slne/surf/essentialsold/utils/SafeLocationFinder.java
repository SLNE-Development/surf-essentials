package dev.slne.surf.essentialsold.utils;

import dev.slne.surf.essentialsold.SurfEssentials;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public final class SafeLocationFinder {
    @Getter(lazy = true)
    private static final HashSet<Material> saveMaterials = generateSafeMaterials();

    private static @NotNull HashSet<Material> generateSafeMaterials() {
        HashSet<Material> materials = new HashSet<>();
        Arrays.stream(Material.values()).filter(material -> !material.isEmpty() && !material.isBurnable() && material.isBlock() && material.isSolid()).forEach(materials::add);
        return materials;
    }

    private final Plugin plugin;
    private final World world;
    private final int maxAttempts;
    private final Location fromLocation;
    private final int radius;

    public SafeLocationFinder(Location fromLocation, int radius, int maxAttempts) {
        this.fromLocation = fromLocation;
        this.radius = radius;
        this.plugin = SurfEssentials.getInstance();
        this.world = fromLocation.getWorld();
        this.maxAttempts = maxAttempts;
    }

    public SafeLocationFinder(Location fromLocation, int radius) {
        this(fromLocation, radius, 10);
    }

    public SafeLocationFinder(Location fromLocation) {
        this(fromLocation, 5000);
    }

    public SafeLocationFinder(World world, int radius, int maxAttempts) {
        this(world.getSpawnLocation(), radius, maxAttempts);
    }

    public SafeLocationFinder(World world, int radius) {
        this(world, radius, 10);
    }

    public SafeLocationFinder(World world) {
        this(world, 5000);
    }

    public SafeLocationFinder() {
        this(Bukkit.getWorlds().get(0));
    }

    public CompletableFuture<Optional<Location>> findSafeLocationAsync() {
        CompletableFuture<Optional<Location>> future = new CompletableFuture<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                Random random = EssentialsUtil.random();
                int attempts = 0;

                while (attempts < maxAttempts) {
                    int randomX = fromLocation.getBlockX() + random.nextInt(radius) - (radius / 2);
                    int randomZ = fromLocation.getBlockZ() + random.nextInt(radius) - (radius / 2);
                    int randomY = world.getHighestBlockYAt(randomX, randomZ);

                    Location location = new Location(world, randomX, randomY, randomZ);

                    if (isLocationSafe(location)) {
                        future.complete(Optional.of(location));
                        return;
                    }

                    attempts++;
                }

                future.complete(Optional.empty()); // No safe location found
            }
        }.runTaskAsynchronously(plugin);

        return future;
    }

    private boolean isLocationSafe(Location location) {
        int x = location.getBlockX(),
                y = location.getBlockY(),
                z = location.getBlockZ();

        val block = world.getBlockAt(x, y, z);
        val below = world.getBlockAt(x, y - 1, z);
        val above = world.getBlockAt(x, y + 1, z);

        if (world.getEnvironment() == World.Environment.THE_END && location.getBlockY() <= 0) return false;
        if (!world.getWorldBorder().isInside(location)) return false;

        return (getSaveMaterials().contains(below.getType())) || (block.getType().isSolid()) || (above.getType().isSolid());
    }
}
