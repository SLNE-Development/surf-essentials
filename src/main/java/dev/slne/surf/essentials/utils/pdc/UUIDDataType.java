package dev.slne.surf.essentials.utils.pdc;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * A custom persistent data type for representing UUIDs as bytes.
 *
 * @author Paper
 * @see <a href="https://docs.papermc.io/paper/dev/pdc#custom-data-types">UUIDDataType</a>
 */
public class UUIDDataType implements PersistentDataType<byte[], UUID> {
    /**
     * The singleton instance of this class.
     */
    public static final UUIDDataType INSTANCE = new UUIDDataType();

    /**
     * Constructs a new {@link UUIDDataType}. This constructor is private because this class is a singleton.
     */
    private UUIDDataType(){}

    /**
     * Gets the primitive type that is used to store UUIDs.
     *
     * @return the primitive type that is used to store UUIDs
     */
    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }


    /**
     * Gets the complex type that is represented by this persistent data type.
     *
     * @return the complex type that is represented by this persistent data type
     */
    @Override
    public @NotNull Class<UUID> getComplexType() {
        return UUID.class;
    }

    /**
     * Converts a UUID value to a byte array.
     *
     * @param complex the UUID value to be converted
     * @param context the persistent data adapter context
     * @return a byte array that represents the given UUID value
     */
    @Override
    public byte @NotNull [] toPrimitive(UUID complex, @NotNull PersistentDataAdapterContext context) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(complex.getMostSignificantBits());
        bb.putLong(complex.getLeastSignificantBits());
        return bb.array();
    }

    /**
     * Converts a byte array to a UUID value.
     *
     * @param primitive the byte array to be converted
     * @param context the persistent data adapter context
     * @return a UUID value that represents the given byte array
     */
    @Override
    public @NotNull UUID fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        ByteBuffer bb = ByteBuffer.wrap(primitive);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }
}
