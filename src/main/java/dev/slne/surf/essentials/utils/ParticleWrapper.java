package dev.slne.surf.essentials.utils;

import com.github.retrooper.packetevents.protocol.particle.data.*;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.util.Vector3i;
import dev.slne.surf.essentials.annontations.UpdateRequired;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Particle;
import org.bukkit.Vibration;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Optional;


/**
 * A wrapper for {@link Particle} and {@link ParticleData} that allows for
 * easy conversion to PacketEvents' {@link com.github.retrooper.packetevents.protocol.particle.Particle} and {@link Particle}
 *
 * @param <BukkitData> The type of data that the Bukkit particle uses
 */
@Data
@RequiredArgsConstructor
public final class ParticleWrapper<BukkitData> {
    private final @NonNull Particle bukkitParticle; // The bukkit particle
    private final @NonNull BukkitData bukkitData; // The bukkit particle data

    /**
     * Creates a new ParticleWrapper from a {@link dev.jorel.commandapi.wrappers.ParticleData<BukkitData>}{@code <}{@link BukkitData}{@code >}
     *
     * @param particleData The particle data to convert
     */
    public ParticleWrapper(dev.jorel.commandapi.wrappers.ParticleData<BukkitData> particleData) {
        this(particleData.particle(), particleData.data());
    }

    /**
     * Converts the {@link BukkitData} to PacketEvents {@link ParticleData}
     *
     * @param data The data to convert
     * @return An {@link Optional} containing the converted data, or {@link Optional#empty()} if the data could not be converted
     */
    @UpdateRequired(minVersion = "1.21", updateReason = "Add support for new particles data")
    private Optional<ParticleData> convertData(BukkitData data) {
        if (data instanceof org.bukkit.Particle.DustOptions dustOptions) {
            val size = dustOptions.getSize();
            val color = dustOptions.getColor();
            val startColor = new Vector3f(color.getRed(), color.getGreen(), color.getBlue());

            if (dustOptions instanceof org.bukkit.Particle.DustTransition dustTransition) {
                val toColor = dustTransition.getToColor();
                val endColor = new Vector3f(toColor.getRed(), toColor.getGreen(), toColor.getBlue());
                return Optional.of(new ParticleDustColorTransitionData(size, startColor, endColor));
            }
            return Optional.of(new ParticleDustData(size, startColor));

        } else if (data instanceof ItemStack itemStack) {
            return Optional.of(new ParticleItemStackData(SpigotConversionUtil.fromBukkitItemStack(itemStack)));

        } else if (data instanceof BlockData blockData) {
            return Optional.of(new ParticleBlockStateData(SpigotConversionUtil.fromBukkitBlockData(blockData)));

        } else if (data instanceof Float roll) {
            return Optional.of(new ParticleSculkChargeData(roll));

        } else if (data instanceof Integer delay) {
            return Optional.of(new ParticleShriekData(delay));

        } else if (data instanceof @SuppressWarnings("deprecation")MaterialData materialData) {
            val itemType = materialData.getItemType();
            @SuppressWarnings("deprecation") val materialDataData = materialData.getData();

            if (itemType.isBlock()) {
                return Optional.of(LegacyParticleData.ofBlock(SpigotConversionUtil.fromBukkitItemMaterial(itemType), materialDataData));
            } else {
                return Optional.of(LegacyParticleData.ofItem(SpigotConversionUtil.fromBukkitItemMaterial(itemType), materialDataData));
            }

        } else if (data instanceof Vibration vibration) { // TODO not sure about this
            val ticks = vibration.getArrivalTime();
            val destination = vibration.getDestination();

            if (destination instanceof Vibration.Destination.BlockDestination blockDestination) {
                val location = blockDestination.getLocation();
                val block = blockDestination.getBlock();

                return Optional.of(new ParticleVibrationData(
                        new Vector3i(
                                location.getBlockX(),
                                location.getBlockY(),
                                location.getBlockZ()
                        ),
                        new Vector3i(
                                block.getX(),
                                block.getY(),
                                block.getZ()
                        ),
                        ticks
                ));
            } else if (destination instanceof Vibration.Destination.EntityDestination entityDestination) {
                val entity = entityDestination.getEntity();
                val location = entity.getLocation();

                return Optional.of(new ParticleVibrationData(
                        new Vector3i(
                                location.getBlockX(),
                                location.getBlockY(),
                                location.getBlockZ()
                        ),
                        entity.getEntityId(),
                        ticks
                ));
            }

            return Optional.empty();
        }
        return Optional.empty();
    }

    /**
     * Gets the PacketEvents {@link com.github.retrooper.packetevents.protocol.particle.Particle} from the bukkit particle
     *
     * @return The PacketEvents particle
     */
    public com.github.retrooper.packetevents.protocol.particle.Particle getPacketEventsParticle() {
        return new com.github.retrooper.packetevents.protocol.particle.Particle(
                SpigotConversionUtil.fromBukkitParticle(bukkitParticle),
                getPacketEventsParticleData()
        );
    }

    /**
     * Gets the PacketEvents {@link ParticleData} from the {@link BukkitData}
     *
     * @return The PacketEvents particle data
     */
    public ParticleData getPacketEventsParticleData() {
        return convertData(bukkitData).orElse(new ParticleData());
    }
}
