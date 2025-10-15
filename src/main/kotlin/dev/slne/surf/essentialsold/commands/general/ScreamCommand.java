package dev.slne.surf.essentialsold.commands.general;

import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.SurfEssentials;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ScreamCommand extends EssentialsCommand {

    private static final Map<UUID, Integer> COOLDOWN = Collections.synchronizedMap(new HashMap<>());

    public ScreamCommand() {
        super("scream", "scream", "Scream it all out");

        withPermission(Permissions.SCREAM_PERMISSION);

        executesNative((NativeResultingCommandExecutor) (sender, args) -> scream(getPlayerOrException(sender)));
    }

    private int scream(@NotNull Player player) {
        val playerUUID = player.getUniqueId();

        if (COOLDOWN.containsKey(playerUUID)) {
            EssentialsUtil.sendError(player, Component.text("Du kannst erst wieder in ", Colors.ERROR)
                    .append(Component.text("%ss".formatted(COOLDOWN.getOrDefault(playerUUID, 0)), Colors.VARIABLE_VALUE))
                    .append(Component.text(" schreien!", Colors.ERROR)));
            return 0;
        }

        if (!player.hasPermission(Permissions.SCREAM_BYPASS_PERMISSION)) {
            COOLDOWN.put(playerUUID, 10);
            Bukkit.getScheduler().runTaskTimerAsynchronously(SurfEssentials.getInstance(), bukkitTask ->
                    COOLDOWN.computeIfPresent(playerUUID, (uuid, integer) -> {
                                --integer;
                                if (integer <= 0) {
                                    bukkitTask.cancel();
                                    COOLDOWN.remove(uuid);
                                }
                                return integer;
                            }
                    ), 5L, 20L);
        }

        val position = player.getLocation();
        val scream = Screams.getRandomScream();
        val distance = new AtomicInteger(0);

        Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), task -> {
            if (distance.get() >= 10) task.cancel();

            doBoom(player, distance.get());

            distance.incrementAndGet();
        }, 0L, 1L);

        player.getWorld().playSound(scream.getSound(), position.getX(), position.getY(), position.getZ());

        return 1;
    }

    private void doBoom(@NotNull Player player, @Range(from = -20, to = 20) int distance) {
        val position = player.getLocation();
        final double
                x = position.getX(),
                y = position.getY() + 1,
                z = position.getZ();

        spawnParticles(player, x + distance, y, z);
        spawnParticles(player, x + distance, y, z + distance);
        spawnParticles(player, x + distance, y, z - distance);
        spawnParticles(player, x - distance, y, z);
        spawnParticles(player, x - distance, y, z - distance);
        spawnParticles(player, x - distance, y, z + distance);

        spawnParticles(player, x, y, z + distance);
        spawnParticles(player, x + distance, y, z + distance);
        spawnParticles(player, x + distance, y, z - distance);
        spawnParticles(player, x - distance, y, z + distance);
        spawnParticles(player, x, y, z - distance);
        spawnParticles(player, x - distance, y, z - distance);

    }

    private void spawnParticles(@NotNull Player player, double x, double y, double z) {
        player.spawnParticle(
                Particle.SONIC_BOOM,
                x,
                y,
                z,
                1,
                0,
                0,
                0,
                10
        );
    }

    public enum Screams {
        ENDERMAN_SCREAM(org.bukkit.Sound.ENTITY_ENDERMAN_SCREAM, 10, 0),
        SCULK_SHRIEKER_SHRIEK(org.bukkit.Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, 10, 0),
        WARDEN_SONIC_BOOM(org.bukkit.Sound.ENTITY_WARDEN_SONIC_BOOM, 10, 0);

        private final Sound sound;

        @Contract(pure = true)
        Screams(org.bukkit.Sound sound, @Range(from = 0, to = 100) float volume, @Range(from = 0, to = 2) float pitch) {
            this.sound = Sound.sound(sound, Sound.Source.PLAYER, volume, pitch);
        }

        @Contract(pure = true)
        public Sound getSound() {
            return sound;
        }


        public static List<Screams> getScreamsList() {
            return Arrays.stream(Screams.values()).toList();
        }

        public static Screams getRandomScream() {
            val screamsList = getScreamsList();
            return screamsList.get(EssentialsUtil.getRandomInt(screamsList.size()));
        }
    }
}
