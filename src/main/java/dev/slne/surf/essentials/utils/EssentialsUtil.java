package dev.slne.surf.essentials.utils;

import com.mojang.authlib.GameProfile;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.annontations.UpdateRequired;
import dev.slne.surf.essentials.utils.abtract.LoggingUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The final util class wich contains miscellaneous methods
 */
@SuppressWarnings("unused")
public final class EssentialsUtil extends LoggingUtil {
    private EssentialsUtil() {} // Util class
    private static Component prefix; // plugins prefix
    private static final CommandBuildContext buildContext; // the build context
    private static final DamageSources damageSources; // damage sources instance


    /**
     * The maximum food value that a player can have.
     */
    @UpdateRequired(minVersion = "unknown", updateReason = "Maybe one day Mojang will change this")
    public static final int MAX_FOOD = 20;

    /**
     * An array of sounds that are used to scare players.
     */
    public static final Sound[] scareSounds = new Sound[]{Sound.ENTITY_LIGHTNING_BOLT_THUNDER, Sound.ENTITY_WOLF_HOWL,
            Sound.ENTITY_BAT_DEATH, Sound.ENTITY_GHAST_SCREAM, Sound.ENTITY_GHAST_HURT};


    /**
     * Creates a gradient of text with the given input, first color and second color.
     *
     * @param input the input text
     * @param firstHex the hex value of the first color
     * @param secondHex the hex value of the second color
     * @return a gradient of text with the given input, first color and second color
     */
    public static Component gradientify(@NotNull String input, @NotNull String firstHex, @NotNull String secondHex) {
        final var gradientFirst = TextColor.fromHexString(firstHex);
        final var gradientSecond = TextColor.fromHexString(secondHex);

        if (gradientFirst == null || gradientSecond == null) return Component.text(input);

        final var builder = Component.text();
        final float step = 1.0f / (input.length() - 1);
        float current = 0.0f;

        for (char c : input.toCharArray()) {
            builder.append(Component.text(c, TextColor.lerp(current, gradientFirst, gradientSecond)));
            current += step;
        }

        return builder.build();
    }


    /**
     * Converts a number of ticks to a formatted string representing days, hours, minutes, and seconds.
     *
     * @param ticks the number of ticks to convert
     * @return a formatted string representing days, hours, minutes, and seconds
     */
    public static String ticksToString(int ticks) {
        int totalSeconds = ticks / 20;
        int days, hours, minutes, seconds;
        // <editor-fold defaultstate="collapsed" desc="calculation">

        if (totalSeconds < 60) return String.format("%ds", totalSeconds);
        if (totalSeconds < 3600) {
            minutes = totalSeconds / 60;
            seconds = totalSeconds % 60;
            return String.format("%02dm %02ds", minutes, seconds);
        }
        if (totalSeconds < 86400) {
            hours = totalSeconds / 3600;
            minutes = (totalSeconds % 3600) / 60;
            seconds = totalSeconds % 60;
            return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
        }
        days = totalSeconds / 86400;
        hours = (totalSeconds % 86400) / 3600;
        minutes = ((totalSeconds % 86400) % 3600) / 60;
        seconds = totalSeconds % 60;
        // </editor-fold>
        return String.format("%dd %02dh %02dm %02ds", days, hours, minutes, seconds);
    }

    /**
     * Plays a random scare sound and adds a "Darkness" potion effect to a given player.
     *
     * @param player the player to scare
     */
    public static void scarePlayer(@NotNull Player player) {
        final int scareIndex = getRandomInt(scareSounds.length - 1);
        final var scareSound = scareSounds[scareIndex];
        final var scareEffect = new PotionEffect(PotionEffectType.DARKNESS, 20 * 7, 1, false, false, false);

        player.playSound(player.getLocation(), scareSound, 1.0F, 1.0F);
        player.addPotionEffect(scareEffect);
    }


    /**
     * Gets the {@link CommandBuildContext} object using the current Minecraft server instance.
     *
     * @return the {@link CommandBuildContext} object
     */
    @Contract(pure = true)
    public static @NotNull CommandBuildContext buildContext() {
        return buildContext;
    }

    /**
     * Deserializes the given string using the legacy ampersand format and returns the resulting component.
     *
     * @param toDeserialize the string to deserialize
     * @return the deserialized component
     */
    public static @NotNull Component deserialize(String toDeserialize) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(toDeserialize);
    }

    /**
     * Formats the given double value to a readable format with one decimal point precision.
     *
     * @param value the double value to format
     * @param <T>   the type of the double value
     * @return the formatted double value as a double
     */
    public static <T extends Double> double makeDoubleReadable(T value) {
        return Double.parseDouble(new DecimalFormat("#.#").format(value));
    }

    /**
     * Checks if the NMS (net.minecraft.server) package is supported by the server.
     *
     * @return true if NMS is supported, false otherwise
     */
    public static boolean isNmsSupported() {
        try {
            Class.forName(NMS_CLASS);
            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return false;
        }
    }

    /**
     * Returns the prefix component from this plugin.
     *
     * @return the prefix component
     */
    public static Component getPrefix() {
        if (prefix == null) {
            return Component.text(">> ", Colors.DARK_GRAY)
                    .append(gradientify("SurfEssentials", "#46B5C9", "#3A7FF2")
                            .append(Component.text(" | ", Colors.DARK_GRAY)));
        }
        return prefix;
    }

    /**
     * Sets the plugin prefix from the plugin configuration file.
     */
    public static void setPrefix() {
        FileConfiguration config = SurfEssentials.getInstance().getConfig();
        String prefixString = config.getString("prefix");
        if (prefixString == null || prefixString.isBlank() || prefixString.isEmpty()) return;

        prefix = MiniMessage.miniMessage().deserialize(prefixString);
    }

    /**
     * Calls the specified event.
     *
     * @param event the event to call
     */
    public static void callEvent(@NotNull Event event) {
        sendDebug("Calling event: " + event.getEventName());
        SurfEssentials.getInstance().getServer().getPluginManager().callEvent(event);
    }

    /**
     * Initializes and returns an object using the specified initializer.
     *
     * @param object      the object to initialize
     * @param initializer the initializer function to use
     * @param <T>         the type of object to initialize
     * @return the initialized object
     */
    @Contract("_, _ -> param1")
    public static <T> T make(T object, @NotNull Consumer<T> initializer) {
        initializer.accept(object);
        return object;
    }

    /**
     * Initializes and returns an object.
     *
     * @param object      the object to initialize
     * @param <T>         the type of object to initialize
     * @return the initialized object
     */
    @Contract("_ -> param1")
    public static <T> T make(T object) {
        return make(object, something -> {});
    }

    /**
     * Gets the display name of the specified {@link Entity} as an Adventure Component.
     *
     * @param entity the entity whose display name to retrieve
     * @param <E>    the type of entity
     * @return the display name of the specified {@link Entity} as an Adventure Component
     */
    public static <E extends Entity> @NotNull Component getDisplayName(E entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            return serverPlayer.adventure$displayName.colorIfAbsent(Colors.TERTIARY);
        }
        return PaperAdventure.asAdventure(entity.getDisplayName()).colorIfAbsent(Colors.TERTIARY);
    }

    /**
     * Returns the display name of the given {@link org.bukkit.entity.Entity}.
     * @param entity the entity whose display name is to be returned
     * @param <E> the type of entity to retrieve the display name of
     * @return a Component representing the display name of the given entity, with color set to TERTIARY if not already set
     */
    public static <E extends org.bukkit.entity.Entity> @NotNull Component getDisplayName(E entity) {
        Component displayName;
        if (entity instanceof Player player) {
            displayName = player.displayName();
        } else if (entity.customName() != null) {
            displayName = Objects.requireNonNull(entity.customName());
        } else {
            displayName = entity.name();
        }
        return displayName.colorIfAbsent(Colors.TERTIARY);
    }

    /**
     * Gets the display name of the specified Bukkit entity as a vanilla Minecraft component.
     *
     * @param entity the Bukkit entity
     * @param <E>    the type of the entity
     * @return the display name as a vanilla Minecraft component
     */
    public static <E extends org.bukkit.entity.Entity> net.minecraft.network.chat.Component getMinecraftDisplayName(E entity) {
        return PaperAdventure.asVanilla(getDisplayName(entity));
    }

    /**
     * Gets the display name of the specified entity as a vanilla Minecraft component.
     *
     * @param entity the entity
     * @param <E>    the type of the entity
     * @return the display name as a vanilla Minecraft component
     */
    public static <E extends Entity> net.minecraft.network.chat.Component getMinecraftDisplayName(E entity) {
        return PaperAdventure.asVanilla(getDisplayName(entity));
    }

    /**
     * Gets the display name of the specified game profile as a component.
     *
     * @param gameProfile the game profile
     * @return the display name as a component
     */
    public static Component getDisplayName(GameProfile gameProfile) {
        final var player = getServerPlayer(gameProfile.getId());
        if (player != null) {
            return getDisplayName(player);
        }
        return Component.text(gameProfile.getName(), Colors.TERTIARY);
    }

    /**
     * Gets the display name of the specified entity as a vanilla Minecraft component.
     *
     * @param entity the entity
     * @param <E>    the type of the entity
     * @return the display name as a vanilla Minecraft component
     */
    public static <E extends Entity> net.minecraft.network.chat.Component getDisplayNameAsVanilla(E entity) {
        return PaperAdventure.asVanilla(getDisplayName(entity));
    }

    /**
     * Returns the given value if it is not null, otherwise returns the default value.
     *
     * @param toCheck      the value to check for null
     * @param defaultValue the default value to return if toCheck is null
     * @param <Value>      the type of the values being compared
     * @return toCheck if it is not null, otherwise defaultValue
     */
    @Contract(value = "null, _ -> param2; !null, _ -> param1", pure = true)
    public static <Value> Value getDefaultIfNull(@Nullable Value toCheck, @NotNull Value defaultValue) {
        return (toCheck == null) ? Objects.requireNonNull(defaultValue) : toCheck;
    }

    /**
     * Returns the instance of the {@link DamageSources} class.
     *
     * @return the instance of the {@link DamageSources} class
     */
    @Contract(pure = true)
    public static DamageSources getDamageSources() {
        return damageSources;
    }

    /**
     * Returns the {@link ServerPlayer} with the given UUID.
     *
     * @param uuid the UUID of the player
     * @return the {@link ServerPlayer} with the given UUID
     */
    public static ServerPlayer getServerPlayer(@NotNull UUID uuid) {
        return getMinecraftServer().getPlayerList().getPlayer(uuid);
    }

    /**
     * Changes the display name of the given {@link ItemMeta}.
     *
     * @param meta the {@link ItemMeta} to modify
     * @param name the new display name
     * @param <T> the type of the {@link ItemMeta}
     * @return the modified {@link ItemMeta}
     */
    @Contract("_, _ -> param1")
    public static <T extends ItemMeta> @NotNull T changeName(@NotNull T meta, Component name) {
        meta.displayName(name);
        return meta;
    }

    /**
     * Changes the display name of the given {@link ItemStack}.
     *
     * @param stack the {@link ItemStack} to modify
     * @param name the new display name
     * @return the modified {@link ItemStack}
     */
    @Contract("_, _ -> param1")
    public static @NotNull ItemStack changeName(ItemStack stack, Component name) {
        stack.setItemMeta(changeName(stack.getItemMeta(), name));
        stack.editMeta(itemMeta -> changeName(itemMeta, name));
        return stack;
    }

    /**
     * Returns a {@link Predicate<Player>} that checks if a {@link Player} has permission for any game mode.
     *
     * @return the predicate for checking game mode permissions
     */
    @Contract(pure = true)
    public static @NotNull Predicate<Player> hasGameModePermission() {
        return player -> player.hasPermission(Permissions.GAMEMODE_ADVENTURE_SELF_PERMISSION) || player.hasPermission(Permissions.GAMEMODE_ADVENTURE_OTHER_PERMISSION)
                || player.hasPermission(Permissions.GAMEMODE_ADVENTURE_OTHER_OFFLINE_PERMISSION) || player.hasPermission(Permissions.GAMEMODE_CREATIVE_SELF_PERMISSION)
                || player.hasPermission(Permissions.GAMEMODE_CREATIVE_OTHER_PERMISSION) || player.hasPermission(Permissions.GAMEMODE_CREATIVE_OTHER_OFFLINE_PERMISSION)
                || player.hasPermission(Permissions.GAMEMODE_SURVIVAL_SELF_PERMISSION) || player.hasPermission(Permissions.GAMEMODE_SURVIVAL_OTHER_PERMISSION)
                || player.hasPermission(Permissions.GAMEMODE_SURVIVAL_OTHER_OFFLINE_PERMISSION) || player.hasPermission(Permissions.GAMEMODE_SPECTATOR_OTHER_PERMISSION)
                || player.hasPermission(Permissions.GAMEMODE_SPECTATOR_SELF_PERMISSION) || player.hasPermission(Permissions.GAMEMODE_SPECTATOR_OTHER_OFFLINE_PERMISSION);
    }

    /**
     * Checks if an {@link Enchantment} is compatible with an {@link ItemStack}.
     *
     * @param enchantment the {@link Enchantment} to check compatibility with
     * @param stack the {@link ItemStack} to check compatibility for
     * @return true if the enchantment is compatible with the item stack, false otherwise
     */
    public static boolean isEnchantmentCompatible(@NotNull Enchantment enchantment, ItemStack stack) {
        if (!enchantment.canEnchantItem(stack)) return false;

        for (Enchantment otherEnchantment : stack.getEnchantments().keySet()) {
            if (enchantment.conflictsWith(otherEnchantment)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the block ID of a given {@link Block}.
     *
     * @param block the block to get the ID for
     * @return the ID of the block
     */
    public static int getBlockId(@NotNull Block block) {
        return Block.getId(block.defaultBlockState());
    }

    /**
     * Gets the item ID of a given {@link Item}.
     *
     * @param item the item to get the ID for
     * @return the ID of the item
     */
    public static int getItemId(Item item) {
        return Item.getId(item);
    }

    /**
     * Spawns a fake {@link FallingBlockEntity} at the specified location for the given {@link Player}.
     *
     * @param player   the {@link Player} for whom the fake falling block is spawned
     * @param block    the block type of the fake falling block
     * @param location the location where the fake falling block is spawned
     * @return the spawned FallingBlockEntity
     */
    public static @NotNull FallingBlockEntity spawnFakeFallingBlock(Player player, @NotNull Block block, @NotNull Location location) {
        final var serverPlayer = EssentialsUtil.toServerPlayer(player);
        final var fallingBlockEntity = new FallingBlockEntity(serverPlayer.level, location.x(), location.y(), location.z(), block.defaultBlockState());

        fallingBlockEntity.setNoGravity(false);
        fallingBlockEntity.setInvulnerable(false);
        fallingBlockEntity.dropItem = false;

        final var packet = new ClientboundAddEntityPacket(fallingBlockEntity, EssentialsUtil.getBlockId(block));

        EssentialsUtil.sendPackets(player, packet);
        return fallingBlockEntity;
    }

    /**
     * Spawns a fake {@link FallingBlockEntity} at the specified location for the given {@link Player} and removes it after a certain duration.
     *
     * @param player   the {@link Player} for whom the fake falling block is spawned
     * @param block    the block type of the fake falling block
     * @param location the location where the fake falling block is spawned
     * @param duration the duration after which the fake falling block is removed
     * @return the spawned FallingBlockEntity
     */
    public static @NotNull FallingBlockEntity spawnFakeFallingBlock(Player player, Block block, Location location, @NotNull Duration duration) {
        final var packet = spawnFakeFallingBlock(player, block, location);
        Bukkit.getScheduler().runTaskLater(SurfEssentials.getInstance(), () -> sendRemoveEntitiesPacket(player, packet.getId()), duration.toMillis() / 50);
        return packet;
    }

    /**
     * Returns the command modification block limit for the given {@link CommandSourceStack}.
     *
     * @param source the command source
     * @return the {@link GameRules#RULE_COMMAND_MODIFICATION_BLOCK_LIMIT}
     */
    public static int modificationBlockLimit(@NotNull CommandSourceStack source) {
        return source.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
    }

    /**
     * Returns the command modification block limit for the given {@link org.bukkit.entity.Entity}.
     *
     * @param entity the entity
     * @return the {@link GameRule#COMMAND_MODIFICATION_BLOCK_LIMIT}
     */
    @SuppressWarnings("DataFlowIssue")
    public static int modificationBlockLimit(org.bukkit.entity.@NotNull Entity entity) {
        return entity.getWorld().getGameRuleValue(GameRule.COMMAND_MODIFICATION_BLOCK_LIMIT);
    }

    /**
     * Formats a {@link BoundingBox} location with the specified color.
     *
     * @param color the color to use for formatting
     * @param boundingBox the bounding box to format
     * @return the formatted location as a Component
     */
    public static @NotNull Component formatLocation(TextColor color, @NotNull BoundingBox boundingBox) {
        return Component.text(" von ", color)
                .append(formatLocation(Colors.SPACER, boundingBox.minX(), boundingBox.minY(), boundingBox.minZ()))
                .append(Component.text(" bis ", color))
                .append(formatLocation(Colors.SPACER, boundingBox.maxX(), boundingBox.maxY(), boundingBox.maxZ()));
    }

    /**
     * Formats a {@link org.bukkit.util.BoundingBox} location with the specified color.
     *
     * @param color the color to use for formatting
     * @param boundingBox the BoundingBox to format
     * @return the formatted location as a Component
     */
    public static @NotNull Component formatLocation(TextColor color, org.bukkit.util.@NotNull BoundingBox boundingBox) {
        return Component.text(" von ", color)
                .append(formatLocation(Colors.SPACER, boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ()))
                .append(Component.text(" bis ", color))
                .append(formatLocation(Colors.SPACER, boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ()));
    }

    /**
     * Formats a {@link Location} with the specified color and displays the world if requested.
     *
     * @param color the color to use for formatting
     * @param location the Location to format
     * @param displayWorld whether to display the world information
     * @return the formatted location as a Component
     */
    public static @NotNull Component formatLocation(TextColor color, @NotNull Location location, boolean displayWorld){
        final var builder = Component.text();

        builder.append(formatLocation(Colors.SPACER, location.x(), location.y(), location.z()));

        if (displayWorld){
            try (final var serverLevel = EssentialsUtil.toServerLevel(location.getWorld())){

                builder.append(Component.text(" in ", color))
                        .append(Component.text(serverLevel.dimension().location().toString(), Colors.VARIABLE_VALUE));

            } catch (IOException ignored) {}
        }

        return builder.build();
    }

    /**
     * Formats the location coordinates with the specified spacer color.
     *
     * @param spacer the color to use for the spacer
     * @param x the X-coordinate
     * @param y the Y-coordinate
     * @param z the Z-coordinate
     * @param <T> the type of the coordinates (Number)
     * @return the formatted location as a Component
     */
    public static<T extends Number> @NotNull Component formatLocation(TextColor spacer, @NotNull T x, @NotNull T y, @NotNull T z) {
        return Component.text(x.doubleValue(), Colors.VARIABLE_VALUE)
                .append(Component.text(", ", spacer))
                .append(Component.text(y.doubleValue(), Colors.VARIABLE_VALUE))
                .append(Component.text(", ", spacer))
                .append(Component.text(z.doubleValue(), Colors.VARIABLE_VALUE));
    }



    static {
        buildContext = CommandBuildContext.configurable(getMinecraftServer().registryAccess(),
                getMinecraftServer().getWorldData().getDataConfiguration().enabledFeatures());

        damageSources = new DamageSources(getMinecraftServer().registryAccess());
    }
}
