package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Validate;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;

public class ScreamCommand extends BrigadierCommand {
    private static final Map<UUID, Integer> COOLDOWN = EssentialsUtil.make(new HashMap<>(), map -> {});
    @Override
    public String[] names() {
        return new String[]{"scream"};
    }

    @Override
    public String usage() {
        return "/scream";
    }

    @Override
    public String description() {
        return "Scream it all out";
    }

    @Override
    public void literal(@NotNull LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(0, Permissions.SCREAM_PERMISSION));
        literal.executes(context -> scream(context.getSource()));
    }

    private int scream(@NotNull CommandSourceStack source) throws CommandSyntaxException {
        final var player = source.getPlayerOrException();
        final var playerUUID = player.getUUID();

        if (COOLDOWN.containsKey(playerUUID)) {
            EssentialsUtil.sendError(player, Component.text("Du kannst erst wieder in ", Colors.ERROR)
                    .append(Component.text("%ss".formatted(COOLDOWN.getOrDefault(player.getUUID(), 0)), Colors.VARIABLE_VALUE))
                    .append(Component.text(" schreien!", Colors.ERROR)));
            return 0;
        }

        if (!player.getBukkitEntity().hasPermission(Permissions.SCREAM_BYPASS_PERMISSION)) {
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

        final var position = player.blockPosition();
        final var level = player.getLevel();
        final var scream = Screams.getRandomScream();
        final var counter = new Object() {
            short distance = 0;
        };

        Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), task -> {
            if (counter.distance >= 10) task.cancel();

            doBoom(player, counter.distance);

            counter.distance++;
        }, 0L, 1L);

        level.playSound(null, position, scream.getSoundEvent(), SoundSource.MASTER, scream.getVolume(), scream.getPitch());

        return 1;
    }

    private void doBoom(@NotNull ServerPlayer player, @Range(from = -20, to = 20) short distance){
        Validate.isInBound(distance, -20, 20);

        final double x = player.getX();
        final double y = player.getY() + 1;
        final double z = player.getZ();

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

    private void spawnParticles(@NotNull ServerPlayer player, double x, double y, double z){
        player.getLevel().sendParticles(
                player, // player
                ParticleTypes.SONIC_BOOM, // particle
                false,  // force
                x, // x
                y, // y
                z, // z
                1, // count
                0 , // delta X
                0 , // delta Y
                0, // delta Z
                10 // speed
        );
    }

    public enum Screams{
        ENDERMAN_SCREAM(SoundEvents.ENDERMAN_SCREAM, 10, 0),
        SCULK_SHRIEKER_SHRIEK(SoundEvents.SCULK_SHRIEKER_SHRIEK,10, 0),
        WARDEN_SONIC_BOOM(SoundEvents.WARDEN_SONIC_BOOM, 10, 0);

        private final SoundEvent soundEvent;
        private final @Range(from = 0, to = 100) float volume;
        private final @Range(from = 0, to = 2) float pitch;

        @Contract(pure = true)
        Screams(SoundEvent soundEvent, @Range(from = 0, to = 100) float volume, @Range(from = 0, to = 2) float pitch){
            this.soundEvent = soundEvent;
            this.volume = volume;
            this.pitch = pitch;
        }

        @Contract(pure = true)
        public SoundEvent getSoundEvent() {
            return soundEvent;
        }

        @Contract(pure = true)
        public @Range(from = 0, to = 100) float getVolume() {
            return volume;
        }

        @Contract(pure = true)
        public @Range(from = 0, to = 2) float getPitch() {
            return pitch;
        }


        public static List<Screams> getScreamsList(){
            return Arrays.stream(Screams.values()).toList();
        }
        public static Screams getRandomScream(){
            final var screamsList = getScreamsList();
            return screamsList.get(EssentialsUtil.getRandomInt(screamsList.size()));
        }
    }
}
