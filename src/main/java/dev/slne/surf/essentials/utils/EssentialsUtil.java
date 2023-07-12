package dev.slne.surf.essentials.utils;

import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.annontations.UpdateRequired;
import dev.slne.surf.essentials.utils.abtract.PropertiesUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.papermc.paper.datapack.Datapack;
import io.papermc.paper.entity.TeleportFlag;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.*;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permissible;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The final util class wich contains miscellaneous methods
 */
@UtilityClass
public class EssentialsUtil extends PropertiesUtil {

    private Component prefix; // plugins prefix
    private final YamlConfiguration paperGlobalConfiguration; // the paper global configuration
    private final YamlConfiguration spigotConfiguration; // the spigot configuration
    private final int MAX_SPAWNABLE_HIGHT = 20_000_000; // the max spawnable hight
    private final int MAX_SPAWNABLE_WIDTH = 30_000_000; // the max spawnable width

    // cache
    private final Map<String, Object> cachedConfigValues = new HashMap<>(); // cached messages


    /**
     * A constant denoting infinite potion duration.
     */
    public final int INFINITE_DURATION = PotionEffect.INFINITE_DURATION;

    /**
     * The maximum food value that a player can have.
     */
    @UpdateRequired(minVersion = "unknown", updateReason = "Maybe one day Mojang will change this")
    public final int MAX_FOOD = 20;

    /**
     * An array of sounds that are used to scare players.
     */
    public final Sound[] scareSounds = new Sound[]{Sound.ENTITY_LIGHTNING_BOLT_THUNDER, Sound.ENTITY_WOLF_HOWL,
            Sound.ENTITY_BAT_DEATH, Sound.ENTITY_GHAST_SCREAM, Sound.ENTITY_GHAST_HURT};


    /**
     * Creates a gradient of text with the given input, first color and second color.
     *
     * @param input     the input text
     * @param firstHex  the hex value of the first color
     * @param secondHex the hex value of the second color
     * @return a gradient of text with the given input, first color and second color
     */
    public Component gradientify(@NotNull String input, @NotNull String firstHex, @NotNull String secondHex) {
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
    public String ticksToString(long ticks) {
        long totalSeconds = ticks / 20;
        long days, hours, minutes, seconds;
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

    public String ticksToString(int ticks) {
        return ticksToString((long) ticks);
    }

    /**
     * Plays a random scare sound and adds a "Darkness" potion effect to a given player.
     *
     * @param player the player to scare
     */
    public void scarePlayer(@NotNull Player player) {
        final int scareIndex = getRandomInt(scareSounds.length - 1);
        final var scareSound = scareSounds[scareIndex];
        final var scareEffect = new PotionEffect(PotionEffectType.DARKNESS, 20 * 7, 1, false, false, false);

        player.playSound(player.getLocation(), scareSound, 1.0F, 1.0F);
        player.addPotionEffect(scareEffect);
    }

    /**
     * Deserializes the given string using the legacy ampersand format and returns the resulting component.
     *
     * @param toDeserialize the string to deserialize
     * @return the deserialized component
     */
    public @NotNull Component deserialize(String toDeserialize) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(toDeserialize);
    }

    /**
     * Formats the given double value to a readable format with one decimal point precision.
     *
     * @param value the double value to format
     * @param <T>   the type of the double value
     * @return the formatted double value as a double
     */
    public <T extends Double> double makeDoubleReadable(T value) {
        return Double.parseDouble(new DecimalFormat("#.#").format(value));
    }

    /**
     * Returns the prefix component from this plugin.
     *
     * @return the prefix component
     */
    public Component getPrefix() {
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
    public void setPrefix() {
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
    public void callEvent(@NotNull Event event) {
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
    public <T> T make(T object, @NotNull Consumer<T> initializer) {
        initializer.accept(object);
        return object;
    }

    /**
     * Initializes and returns an object.
     *
     * @param object the object to initialize
     * @param <T>    the type of object to initialize
     * @return the initialized object
     */
    @Contract("_ -> param1")
    public <T> T make(T object) {
        return make(object, something -> {
        });
    }

    /**
     * Returns the display name of the given {@link org.bukkit.entity.Entity}.
     *
     * @param sender   the sender whose display name is to be returned
     * @param <Sender> the type of sender to retrieve the display name of
     * @return a Component representing the display name of the given sender, with color set to TERTIARY if not already set
     */
    public <Sender extends CommandSender> @NotNull Component getDisplayName(Sender sender) {
        Component displayName;
        if (sender instanceof org.bukkit.entity.Entity entity) {
            if (entity instanceof Player player) {
                displayName = player.displayName();
            } else if (entity.customName() != null) {
                displayName = Objects.requireNonNull(entity.customName());
            } else {
                displayName = entity.name();
            }
        } else {
            displayName = sender.name();
        }

        return displayName.colorIfAbsent(Colors.VARIABLE_VALUE);
    }

    /**
     * Returns the display name of the given {@link ItemStack}.
     *
     * @param stack the item stack whose display name is to be returned
     * @return a Component representing the display name of the given item stack, with color set to VARIABLE_VALUE if not already set
     */
    public Component getDisplayName(ItemStack stack) {
        return stack.displayName().colorIfAbsent(Colors.VARIABLE_VALUE);
    }

    /**
     * Returns the display name of the given {@link Advancement}.
     *
     * @param advancement the advancement whose display name is to be returned
     * @return a Component representing the display name of the given advancement, with color set to VARIABLE_VALUE if not already set
     */
    public <Advancement extends org.bukkit.advancement.Advancement> @NotNull Component getDisplayName(@NotNull Advancement advancement) {
        return advancement.displayName().colorIfAbsent(Colors.VARIABLE_VALUE);
    }

    /**
     * Returns the display name of the given {@link BossBar}.
     *
     * @param bar   the boss bar whose display name is to be returned
     * @param <Bar> the type of boss bar
     * @return a Component representing the display name of the given boss bar, with color set to VARIABLE_VALUE if not already set
     */
    public <Bar extends BossBar> @NotNull Component getDisplayName(@NotNull Bar bar) {
        return Component.text("[", Colors.SPACER)
                .append(Component.text((bar instanceof KeyedBossBar keyedBossBar) ? keyedBossBar.getKey().asString() : bar.getTitle(), Colors.convertBossBarColor(bar.getColor())))
                .append(Component.text("]", Colors.SPACER))
                .hoverEvent(HoverEvent.showText(Component.text("Titel: ", Colors.VARIABLE_KEY)
                        .append(Component.text(bar.getTitle(), Colors.VARIABLE_VALUE))
                        .appendNewline()
                        .append(Component.text("Farbe: ", Colors.VARIABLE_KEY)
                                .append(Component.text(bar.getColor().name().toLowerCase(), Colors.convertBossBarColor(bar.getColor()))))
                        .appendNewline()
                        .append(Component.text("Fortschritt: ", Colors.VARIABLE_KEY)
                                .append(Component.text("%s%%".formatted(bar.getProgress() * 100), Colors.VARIABLE_VALUE)))
                        .appendNewline()
                        .append(Component.text("Spieler: ", Colors.VARIABLE_KEY)
                                .append(Component.text(bar.getPlayers().size(), Colors.VARIABLE_VALUE)))
                        .appendNewline()
                        .append(Component.text("Sichtbarkeit: ", Colors.VARIABLE_KEY)
                                .append(Component.text(bar.isVisible() ? "sichtbar" : "unsichtbar", Colors.VARIABLE_VALUE)))
                        .appendNewline()
                        .append(Component.text("Flags: ", Colors.VARIABLE_KEY)
                                .append(Component.join(
                                        JoinConfiguration.commas(true),
                                        Arrays.stream(BarFlag.values())
                                                .filter(bar::hasFlag)
                                                .map(barFlag -> Component.text(barFlag.name().toLowerCase(), Colors.VARIABLE_VALUE))
                                                .toList()
                                ))
                        )
                        .appendNewline()
                        .append(Component.text("Style: ", Colors.VARIABLE_KEY)
                                .append(Component.text(bar.getStyle().name().toLowerCase(), Colors.VARIABLE_VALUE)))));
    }

    /**
     * Returns the display name of the given {@link Datapack}.
     *
     * @param pack   the datapack whose display name is to be returned
     * @param <Pack> the type of datapack
     * @return a Component representing the display name of the given datapack, with color set to VARIABLE_VALUE if not already set
     */
    public <Pack extends Datapack> @NotNull Component getDisplayName(@NotNull Pack pack) {
        return getDisplayName(pack, pack.isEnabled());
    }

    /**
     * Returns the display name of the given {@link Datapack}.
     *
     * @param pack    the datapack whose display name is to be returned
     * @param enabled whether the datapack is enabled
     * @param <Pack>  the type of datapack
     * @return a Component representing the display name of the given datapack, with color set to VARIABLE_VALUE if not already set
     */
    @UpdateRequired(updateReason = "Paper API is not really yet available, but bukkit is deprecated")
    public <Pack extends Datapack> @NotNull Component getDisplayName(@NotNull Pack pack, boolean enabled) {
        return Component.text("[", (enabled) ? Colors.GREEN : Colors.RED)
                .append(Component.text(pack.getName(), Colors.VARIABLE_VALUE))
                .append(Component.text("]", (enabled) ? Colors.GREEN : Colors.RED));
    }

    /**
     * Returns the display name of the given {@link PotionEffect}.
     *
     * @param effect   the potion effect type whose display name is to be returned
     * @param <Effect> the type of potion effect
     * @return a Component representing the display name of the given potion effect type
     */
    public <Effect extends PotionEffect> @NotNull Component getDisplayName(@NotNull Effect effect) {
        return Component.translatable(effect.getType().translationKey(), effect.getType().getEffectCategory().getColor())
                .hoverEvent(HoverEvent.showText(Component.text("Dauer: ", Colors.VARIABLE_KEY)
                        .append(Component.text((effect.getDuration() != INFINITE_DURATION) ? ticksToString(effect.getDuration()) : "unendlich", Colors.VARIABLE_VALUE))));
    }

    /**
     * Returns the display name of the given {@link PotionEffectType}.
     *
     * @param effectType   the potion effect type whose display name is to be returned
     * @param <EffectType> the type of potion effect
     * @return a Component representing the display name of the given potion effect type
     */
    public <EffectType extends PotionEffectType> @NotNull Component getDisplayName(@NotNull EffectType effectType) {
        return Component.text(effectType.getName(), effectType.getEffectCategory().getColor());
    }

    /**
     * Returns the display name of the given {@link org.bukkit.enchantments.Enchantment}.
     *
     * @param enchantment   the enchantment whose display name is to be returned
     * @param level         the level of the enchantment
     * @param <Enchantment> the type of enchantment
     * @return a Component representing the display name of the given enchantment
     */
    public <Enchantment extends org.bukkit.enchantments.Enchantment> Component getDisplayName(Enchantment enchantment, int level) {
        return enchantment.displayName(level).colorIfAbsent(Colors.VARIABLE_VALUE);
    }

    /**
     * Returns the display name of the given {@link org.bukkit.enchantments.Enchantment}.
     *
     * @param enchantment   the enchantment whose display name is to be returned
     * @param <Enchantment> the type of enchantment
     * @return a Component representing the display name of the given enchantment
     */
    public <Enchantment extends org.bukkit.enchantments.Enchantment> Component getDisplayName(Enchantment enchantment) {
        return getDisplayName(enchantment, 1);
    }

    /**
     * Returns the display name of the given {@link WorldInfo}.
     *
     * @param world   the world whose display name is to be returned
     * @param <World> the type of world
     * @return a Component representing the display name of the given world
     */
    @Contract("_ -> new")
    public <World extends org.bukkit.World> @NotNull Component getDisplayName(@NotNull World world) {
        return Component.text(world.getKey().asString(), Colors.VARIABLE_VALUE);
    }

    /**
     * Returns the display name of the given {@link Translatable} object.
     *
     * @param translate   the translatable object
     * @param <Translate> the type of translatable object
     * @return the translatable object's display name
     */
    public <Translate extends Translatable> Component getDisplayName(Translate translate) {
        return Component.translatable(translate.translationKey(), Colors.VARIABLE_VALUE);
    }

    /**
     * Gets the display name component for a player with the given name.
     * <p>
     * If a player with the given name is online, the display name component of that player
     * is returned. Otherwise, a text component with the given name and tertiary color is returned.
     * </p>
     *
     * @param name the name of the player
     * @return the display name component
     * @since 1.0.4
     */
    public Component getDisplayName(String name) {
        final var player = Bukkit.getPlayer(name);

        if (player != null) return getDisplayName(player);
        return Component.text(name, Colors.TERTIARY);
    }

    /**
     * Gets the display name of an OfflinePlayer if online otherwise just the String name
     *
     * @param offlinePlayer   the OfflinePlayer
     * @param <OfflinePlayer> the type of the OfflinePlayer
     * @return the display name
     */
    public <OfflinePlayer extends org.bukkit.OfflinePlayer> Component getOfflineDisplayName(OfflinePlayer offlinePlayer) {
        final String name = offlinePlayer.getName();
        if (!offlinePlayer.isOnline())
            return name != null ? Component.text(name, Colors.VARIABLE_VALUE) : Component.text("Unknown Player", Colors.DARK_GRAY);

        return getDisplayName(offlinePlayer.getPlayer());
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
    public <Value> Value getDefaultIfNull(@Nullable Value toCheck, @NotNull Value defaultValue) {
        return (toCheck == null) ? Objects.requireNonNull(defaultValue) : toCheck;
    }

    /**
     * Changes the display name of the given {@link ItemMeta}.
     *
     * @param meta the {@link ItemMeta} to modify
     * @param name the new display name
     * @param <T>  the type of the {@link ItemMeta}
     * @return the modified {@link ItemMeta}
     */
    @Contract("_, _ -> param1")
    public <T extends ItemMeta> @NotNull T changeName(@NotNull T meta, Component name) {
        meta.displayName(name);
        return meta;
    }

    /**
     * Changes the display name of the given {@link ItemStack}.
     *
     * @param stack the {@link ItemStack} to modify
     * @param name  the new display name
     * @return the modified {@link ItemStack}
     */
    @Contract("_, _ -> param1")
    public @NotNull ItemStack changeName(ItemStack stack, Component name) {
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
    public <P extends Permissible> @NotNull Predicate<P> hasGameModePermission() {
        return permissible -> permissible.hasPermission(Permissions.GAMEMODE_ADVENTURE_SELF_PERMISSION) || permissible.hasPermission(Permissions.GAMEMODE_ADVENTURE_OTHER_PERMISSION)
                || permissible.hasPermission(Permissions.GAMEMODE_ADVENTURE_OTHER_OFFLINE_PERMISSION) || permissible.hasPermission(Permissions.GAMEMODE_CREATIVE_SELF_PERMISSION)
                || permissible.hasPermission(Permissions.GAMEMODE_CREATIVE_OTHER_PERMISSION) || permissible.hasPermission(Permissions.GAMEMODE_CREATIVE_OTHER_OFFLINE_PERMISSION)
                || permissible.hasPermission(Permissions.GAMEMODE_SURVIVAL_SELF_PERMISSION) || permissible.hasPermission(Permissions.GAMEMODE_SURVIVAL_OTHER_PERMISSION)
                || permissible.hasPermission(Permissions.GAMEMODE_SURVIVAL_OTHER_OFFLINE_PERMISSION) || permissible.hasPermission(Permissions.GAMEMODE_SPECTATOR_OTHER_PERMISSION)
                || permissible.hasPermission(Permissions.GAMEMODE_SPECTATOR_SELF_PERMISSION) || permissible.hasPermission(Permissions.GAMEMODE_SPECTATOR_OTHER_OFFLINE_PERMISSION);
    }

    /**
     * Checks if an {@link Enchantment} is compatible with an {@link ItemStack}.
     *
     * @param enchantment the {@link Enchantment} to check compatibility with
     * @param stack       the {@link ItemStack} to check compatibility for
     * @return true if the enchantment is compatible with the item stack, false otherwise
     */
    public boolean isEnchantmentCompatible(@NotNull Enchantment enchantment, ItemStack stack) {
        if (!enchantment.canEnchantItem(stack)) return false;

        for (Enchantment other : stack.getEnchantments().keySet()) {
            if (enchantment.conflictsWith(other)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the block ID of a given {@link org.bukkit.block.Block}.
     *
     * @param block the block to get the ID for
     * @return the ID of the block
     */
    public int getBlockId(@NotNull org.bukkit.block.Block block) {
        return SpigotConversionUtil.fromBukkitBlockData(block.getBlockData()).getGlobalId();
    }

    /**
     * Returns the command modification block limit for the given {@link World}.
     *
     * @param world the world
     * @return the {@link GameRule#COMMAND_MODIFICATION_BLOCK_LIMIT}
     */
    @SuppressWarnings("DataFlowIssue")
    public int modificationBlockLimit(@NotNull World world) {
        return world.getGameRuleValue(GameRule.COMMAND_MODIFICATION_BLOCK_LIMIT);
    }

    /**
     * Returns the command modification block limit for the given {@link org.bukkit.entity.Entity}.
     *
     * @param entity the entity
     * @return the {@link GameRule#COMMAND_MODIFICATION_BLOCK_LIMIT}
     */
    @SuppressWarnings("DataFlowIssue")
    public int modificationBlockLimit(org.bukkit.entity.@NotNull Entity entity) {
        return entity.getWorld().getGameRuleValue(GameRule.COMMAND_MODIFICATION_BLOCK_LIMIT);
    }

    /**
     * Formats a {@link org.bukkit.util.BoundingBox} location with the specified color.
     *
     * @param color       the color to use for formatting
     * @param boundingBox the BoundingBox to format
     * @return the formatted location as a Component
     */
    public @NotNull Component formatLocation(TextColor color, org.bukkit.util.@NotNull BoundingBox boundingBox) {
        return Component.text(" von ", color)
                .append(formatLocation(Colors.SPACER, boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ()))
                .append(Component.text(" bis ", color))
                .append(formatLocation(Colors.SPACER, boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ()));
    }

    /**
     * Formats a {@link Location} with the specified color and displays the world if requested.
     *
     * @param color        the color to use for formatting
     * @param location     the Location to format
     * @param displayWorld whether to display the world information
     * @return the formatted location as a Component
     */
    public @NotNull Component formatLocation(TextColor color, @NotNull Location location, boolean displayWorld) {
        final var builder = Component.text();

        builder.append(formatLocation(Colors.SPACER, location.x(), location.y(), location.z()));

        if (displayWorld) {
            builder.append(Component.text(" in ", color))
                    .append(getDisplayName(location.getWorld()));
        }

        return builder.build();
    }

    /**
     * Formats the location coordinates with the specified spacer color.
     *
     * @param spacer the color to use for the spacer
     * @param x      the X-coordinate
     * @param y      the Y-coordinate
     * @param z      the Z-coordinate
     * @param <T>    the type of the coordinates (Number)
     * @return the formatted location as a Component
     */
    public <T extends Number> @NotNull Component formatLocation(TextColor spacer, @NotNull T x, @NotNull T y, @NotNull T z) {
        return Component.text(makeDoubleReadable(x.doubleValue()), Colors.VARIABLE_VALUE)
                .append(Component.text(", ", spacer))
                .append(Component.text(makeDoubleReadable(y.doubleValue()), Colors.VARIABLE_VALUE))
                .append(Component.text(", ", spacer))
                .append(Component.text(makeDoubleReadable(z.doubleValue()), Colors.VARIABLE_VALUE));
    }

    /**
     * Formats the coordinates to a readable format.
     *
     * @param x   the X-coordinate
     * @param y   the Y-coordinate
     * @param z   the Z-coordinate
     * @param <T> the type of the coordinates (Number)
     * @return the formatted location as a Component
     */
    public <T extends Number> Component formatLocationWithoutSpacer(T x, T y, T z) {
        return Component.text("%s %s %s".formatted(makeDoubleReadable(x.doubleValue()), makeDoubleReadable(y.doubleValue()), makeDoubleReadable(z.doubleValue())), Colors.VARIABLE_VALUE);
    }

    /**
     * Formats the Location position to a readable format.
     *
     * @param location the location to format
     * @return the formatted location as a Component
     */
    public Component formatLocationWithoutSpacer(Location location) {
        return formatLocationWithoutSpacer(location.x(), location.y(), location.z());
    }

    /**
     * Replaces the entity name in the given {@link Component} with the display name of the given {@link org.bukkit.entity.Entity}.
     *
     * @param component the component to replace the entity name in
     * @param entity    the entity to get the display name from
     * @return the component with the replaced entity name
     */
    @Contract(pure = true)
    public @NotNull Component replaceEntityName(@NotNull Component component, org.bukkit.entity.Entity entity, TextColor colorIfAbsent) {
        return component.replaceText(b -> b
                .matchLiteral(
                        LegacyComponentSerializer
                                .legacyAmpersand()
                                .serialize(entity.teamDisplayName())
                                .replace("§", "")
                )
                .once()
                .replacement(EssentialsUtil.getDisplayName(entity))
        ).colorIfAbsent(colorIfAbsent);
    }

    /**
     * Heals the given {@link Damageable} entity by the given amount.
     * <br>
     * This method will call the {@link EntityRegainHealthEvent} and
     * set the health of the entity if the event is not cancelled.
     *
     * @param damageable   the entity to heal
     * @param amount       the amount to heal
     * @param regainReason the reason for the health regain
     * @param isFastRegen  whether the health regain is fast regen
     */
    public void heal(@NotNull Damageable damageable, double amount, EntityRegainHealthEvent.RegainReason regainReason, boolean isFastRegen) {
        double health = damageable.getHealth();
        if (health > 0.0F) {
            EntityRegainHealthEvent event = new EntityRegainHealthEvent(damageable, amount, regainReason, isFastRegen);
            if (damageable.isValid()) {
                callEvent(event);
            }

            if (!event.isCancelled()) {
                damageable.setHealth(event.getAmount());
            }
        }
    }

    /**
     * Gets the current {@link Location} the sender is in.
     *
     * @param sender   the sender to get the location from
     * @param <Sender> the type of the sender
     * @return the location the sender is in
     */
    public <Sender extends Audience> Location getSenderLocation(Sender sender) {
        return (sender instanceof org.bukkit.entity.Entity entity) ? entity.getLocation() : new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
    }


    /**
     * Retrieves a list of BlockPositions between two closed points in 3D space.
     *
     * @param startX The starting X coordinate.
     * @param startY The starting Y coordinate.
     * @param startZ The starting Z coordinate.
     * @param endX   The ending X coordinate.
     * @param endY   The ending Y coordinate.
     * @param endZ   The ending Z coordinate.
     * @return A list of BlockPositions between the specified points.
     */
    @SuppressWarnings("UnstableApiUsage")
    public List<BlockPosition> getBlockPositionBetweenClosed(int startX, int startY, int startZ, int endX, int endY, int endZ) {
        final ArrayList<BlockPosition> blockPositions = new ArrayList<>();
        final int lengthX = endX - startX + 1;
        final int lengthY = endY - startY + 1;
        final int lengthZ = endZ - startZ + 1;
        final int volume = lengthX * lengthY * lengthZ;

        // Loop through all the blocks
        for (int index = 0; index < volume; index++) {
            int offsetX = index % lengthX;
            int quotient = index / lengthX;
            int offsetY = quotient % lengthY;
            int offsetZ = quotient / lengthY;
            blockPositions.add(Position.block(startX + offsetX, startY + offsetY, startZ + offsetZ));
        }

        return blockPositions;
    }

    /**
     * Retrieves a list of BlockPositions that are inside the specified bounding box.
     *
     * @param boundingBox The bounding box to get the BlockPositions from.
     * @return A list of BlockPositions between the specified points.
     */
    @SuppressWarnings("UnstableApiUsage")
    public List<BlockPosition> getBlockPositionFromBoundingBox(org.bukkit.util.BoundingBox boundingBox) {
        return getBlockPositionBetweenClosed(boundingBox.getMin().getBlockX(), boundingBox.getMin().getBlockY(), boundingBox.getMin().getBlockZ(), boundingBox.getMax().getBlockX(), boundingBox.getMax().getBlockY(), boundingBox.getMax().getBlockZ());
    }

    /**
     * Retrieves a list of Blocks that are inside the specified bounding box.
     *
     * @param world       The world to get the blocks from.
     * @param boundingBox The bounding box to get the blocks from.
     * @return A list of Blocks between the specified points.
     */
    public List<org.bukkit.block.Block> getBlocksFromBoundingBox(World world, org.bukkit.util.BoundingBox boundingBox) {
        return getBlockPositionFromBoundingBox(boundingBox)
                .stream()
                .map(blockPosition -> world.getBlockAt(blockPosition.blockX(), blockPosition.blockY(), blockPosition.blockZ()))
                .toList();
    }

    /**
     * Retrieves a list of Blocks that are inside the specified bounding box.
     * <br>
     * <br>
     * <b>Note:</b> This method uses the first world in the server's world list.
     *
     * @param boundingBox The bounding box to get the blocks from.
     * @return A list of Blocks between the specified points.
     * @deprecated It is recommended to specify the world to get the blocks from.
     * <br>
     * Use {@link #getBlocksFromBoundingBox(World, org.bukkit.util.BoundingBox)} instead.
     */
    @Deprecated
    public List<org.bukkit.block.Block> getBlocksFromBoundingBox(org.bukkit.util.BoundingBox boundingBox) {
        return getBlocksFromBoundingBox(Bukkit.getWorlds().get(0), boundingBox);
    }

    /**
     * Gets from the {@code  paperGlobalConfiguration} if the time command affects all worlds.
     *
     * @return True if the time command affects all worlds, false otherwise.
     */
    public boolean timeCommandAffectsAllWorlds() {
        return (boolean) cachedConfigValues.computeIfAbsent("commands.time-command-affects-all-worlds", key -> paperGlobalConfiguration.getBoolean(key, false));
    }

    /**
     * Gets from the {@code spigotConfiguration} the whitelist message.
     *
     * @return The whitelist message.
     */
    public String whitelistMessage() {
        return (String) cachedConfigValues.computeIfAbsent("messages.whitelist", key -> spigotConfiguration.getString(key, "You are not whitelisted on this server!"));
    }

    /**
     * Gets from the {@code spigotConfiguration} the unknown command message.
     *
     * @return The unknown command message.
     */
    public String unknowCommandMessage() {
        return (String) cachedConfigValues.computeIfAbsent("messages.unknown-command", key -> spigotConfiguration.getString(key, "Unknown command. Type \"/help\" for help."));
    }

    /**
     * Checks if the given location is within the spawnable bounds.
     *
     * @param location The location to check.
     * @return True if the location is within the spawnable bounds, false otherwise.
     */
    public boolean isInSpawnableBounds(Location location) {
        double x = location.x();
        double y = location.y();
        double z = location.z();

        return !(y < -MAX_SPAWNABLE_HIGHT || (y >= MAX_SPAWNABLE_HIGHT)) && (x >= -MAX_SPAWNABLE_WIDTH) && (z >= -MAX_SPAWNABLE_WIDTH) && (x < MAX_SPAWNABLE_WIDTH) && (z < MAX_SPAWNABLE_WIDTH);
    }

    /**
     * Converts the given String into a minecraft seed
     *
     * @param seed The seed to convert
     * @return The converted seed
     */
    public long convertStringToSeed(String seed) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(seed.getBytes(StandardCharsets.UTF_8));

            long result = 0;
            for (int i = 0; i < 8; i++) {
                result |= (long) (hash[i] & 0xFF) << (i * 8);
            }

            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * Loads/Generates(in 1.13+) the Chunk asynchronously, and then teleports the entity when the chunk is ready.
     * <br>
     * <br>
     * The chunk is loaded/generated using slowly so the teleportation can take some time
     *
     * @param entity      Entity to teleport
     * @param destination Location to teleport to
     * @return A future that will be completed with the result of the teleport
     */
    @ApiStatus.Experimental
    public static CompletableFuture<Boolean> teleportLazy(org.bukkit.entity.Entity entity, Location destination) {
        return teleportLazy(entity, destination, PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    /**
     * Loads/Generates(in 1.13+) the Chunk asynchronously, and then teleports the entity when the chunk is ready.
     * <br>
     * <br>
     * The chunk is loaded/generated using slowly so the teleportation can take some time
     *
     * @param entity      Entity to teleport
     * @param destination Location to teleport to
     * @param flags       Flags to use when teleporting
     * @return A future that will be completed with the result of the teleport
     */
    @ApiStatus.Experimental
    public static CompletableFuture<Boolean> teleportLazy(org.bukkit.entity.Entity entity, Location destination, TeleportFlag... flags) {
        return teleportLazy(entity, destination, PlayerTeleportEvent.TeleportCause.PLUGIN, flags);
    }

    /**
     * Loads/Generates(in 1.13+) the Chunk asynchronously, and then teleports the entity when the chunk is ready.
     * <br>
     * <br>
     * The chunk is loaded/generated using slowly so the teleportation can take some time
     *
     * @param entity      Entity to teleport
     * @param destination Location to teleport to
     * @param cause       Reason for teleport
     * @param flags       Flags to use when teleporting
     * @return A future that will be completed with the result of the teleport
     */
    @ApiStatus.Experimental
    public static CompletableFuture<Boolean> teleportLazy(org.bukkit.entity.Entity entity, Location destination, PlayerTeleportEvent.TeleportCause cause, TeleportFlag... flags) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        destination.getWorld().getChunkAtAsync(destination).thenAccept((chunk) -> future.complete(entity.teleport(destination, cause, flags))).exceptionally(ex -> {
            future.completeExceptionally(ex);
            return null;
        });
        return future;
    }

    /**
     * Loads/Generates(in 1.13+) the Chunk asynchronously, and then teleports the entity when the chunk is ready.
     * <br>
     * <br>
     * The chunk is loaded/generated using the urgent flag so the teleportation can happen as soon as possible
     *
     * @param entity      Entity to teleport
     * @param destination Location to teleport to
     * @param <E>         Entity type to teleport
     * @return A future that will be completed with the teleported entity or null if the teleport failed
     */
    @ApiStatus.Experimental
    public static <E extends org.bukkit.entity.Entity> CompletableFuture<E> teleportAsync(E entity, Location destination) {
        return teleportAsync(entity, destination, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    /**
     * Loads/Generates(in 1.13+) the Chunk asynchronously, and then teleports the entity when the chunk is ready.
     * <br>
     * <br>
     * The chunk is loaded/generated using the urgent flag so the teleportation can happen as soon as possible
     *
     * @param entity      Entity to teleport
     * @param destination Location to teleport to
     * @param flags       Flags to use when teleporting
     * @param <E>         Entity type to teleport
     * @return A future that will be completed with the teleported entity or null if the teleport failed
     */
    @ApiStatus.Experimental
    public static <E extends org.bukkit.entity.Entity> CompletableFuture<E> teleportAsync(E entity, Location destination, TeleportFlag... flags) {
        return teleportAsync(entity, destination, PlayerTeleportEvent.TeleportCause.PLUGIN, flags);
    }

    /**
     * Loads/Generates(in 1.13+) the Chunk asynchronously, and then teleports the entity when the chunk is ready.
     * <br>
     * <br>
     * The chunk is loaded/generated using the urgent flag so the teleportation can happen as soon as possible
     *
     * @param entity      Entity to teleport
     * @param destination Location to teleport to
     * @param cause       Reason for teleport
     * @param flags       Flags to use when teleporting
     * @param <E>         Entity type to teleport
     * @return A future that will be completed with the teleported entity or null if the teleport failed
     */
    @ApiStatus.Experimental
    public static <E extends org.bukkit.entity.Entity> CompletableFuture<E> teleportAsync(E entity, Location destination, PlayerTeleportEvent.TeleportCause cause, TeleportFlag... flags) {
        CompletableFuture<E> future = new CompletableFuture<>();
        destination.getWorld().getChunkAtAsyncUrgently(destination).thenAccept(chunk -> future.complete(entity.teleport(destination, cause, flags) ? entity : null)).exceptionally(ex -> {
            future.completeExceptionally(ex);
            return null;
        });
        return future;
    }


    static {
        paperGlobalConfiguration = YamlConfiguration.loadConfiguration(new File("config" + File.separatorChar + "paper-global.yml"));
        spigotConfiguration = YamlConfiguration.loadConfiguration(new File("spigot.yml"));
    }
}