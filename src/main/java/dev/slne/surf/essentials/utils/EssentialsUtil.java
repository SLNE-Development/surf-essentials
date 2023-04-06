package dev.slne.surf.essentials.utils;

import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.abtract.CommandUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

public final class EssentialsUtil extends CommandUtil {
    private EssentialsUtil(){}
    private static Component prefix;
    public static final int MAX_FOOD = 20;

    public static Sound[] scareSounds = new Sound[]{Sound.ENTITY_LIGHTNING_BOLT_THUNDER, Sound.ENTITY_WOLF_HOWL,
            Sound.ENTITY_BAT_DEATH, Sound.ENTITY_GHAST_SCREAM, Sound.ENTITY_GHAST_HURT};


    public static Component gradientify(@NotNull String input, @NotNull String firstHex, @NotNull String secondHex) {
        TextColor gradientFirst = TextColor.fromHexString(firstHex);
        TextColor gradientSecond = TextColor.fromHexString(secondHex);

        if (gradientFirst == null || gradientSecond == null) {
            return Component.text(input);
        }

        TextComponent.Builder builder = Component.text();
        float step = 1.0f / (input.length() - 1);
        float current = 0.0f;
        for (char c : input.toCharArray()) {
            builder.append(Component.text(c, TextColor.lerp(current, gradientFirst, gradientSecond)));
            current += step;
        }

        return builder.build();
    }


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

    public static void scarePlayer(@NotNull Player player) {
        Random random = new Random();
        int scareIndex = random.nextInt(scareSounds.length - 1);
        Sound scareSound = scareSounds[scareIndex];
        player.playSound(player.getLocation(), scareSound, 1.0F, 1.0F);
        PotionEffect scareEffect = new PotionEffect(PotionEffectType.DARKNESS, 20*7, 1, false, false, false);
        player.addPotionEffect(scareEffect);
    }


    @SuppressWarnings("unchecked")
    public static @NotNull CommandBuildContext buildContext(){
        return CommandBuildContext.configurable(MinecraftServer.getServer().registryAccess(),
            MinecraftServer.getServer().getWorldData().getDataConfiguration().enabledFeatures());
    }

    public static Component deserialize(String toDeserialize){
        return LegacyComponentSerializer.legacyAmpersand().deserialize(toDeserialize);
    }

    public static <T extends Double> double makeDoubleReadable(T value){
        return Double.parseDouble(new DecimalFormat("#.#").format(value));
    }

    public static boolean isNmsSupported(){
        try {
            Class.forName(NMS_CLASS);
            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return false;
        }
    }

    public static Component getPrefix(){
        if (prefix == null) {
            return Component.text(">> ", Colors.DARK_GRAY)
                    .append(gradientify("SurfEssentials", "#46B5C9", "#3A7FF2")
                            .append(Component.text(" | ", Colors.DARK_GRAY)));
        }
        return prefix;
    }

    public static void setPrefix(){
        FileConfiguration config = SurfEssentials.getInstance().getConfig();
        String prefixString = config.getString("prefix");
        if(prefixString == null || prefixString.isBlank() || prefixString.isEmpty()) return;

        prefix = MiniMessage.miniMessage().deserialize(prefixString);
    }

    public static void callEvent(@NotNull Event event){
        sendDebug("Calling event: " + event.getEventName());
        SurfEssentials.getInstance().getServer().getPluginManager().callEvent(event);
    }

    @Contract("_, _ -> param1")
    public static <T> T make(T object, @NotNull Consumer<T> initializer) {
        initializer.accept(object);
        return object;
    }

    public static<E extends Entity> @NotNull Component getDisplayName(E entity) {
        if (entity instanceof ServerPlayer serverPlayer){
            return serverPlayer.adventure$displayName.colorIfAbsent(Colors.TERTIARY);
        }
        return PaperAdventure.asAdventure(entity.getDisplayName()).colorIfAbsent(Colors.TERTIARY);
    }
    public static<E extends org.bukkit.entity.Entity> @NotNull Component getDisplayName(E entity) {
        Component displayName;
        if (entity instanceof Player player){
            displayName = player.displayName();
        }else if (entity.customName() != null) {
            displayName = Objects.requireNonNull(entity.customName());
        }else {
            displayName = entity.name();
        }
        return displayName.colorIfAbsent(Colors.TERTIARY);
    }

    public static<E extends Entity> net.minecraft.network.chat.Component getDisplayNameAsVanilla(E entity) {
       return PaperAdventure.asVanilla(getDisplayName(entity));
    }

    @Contract(value = "null, _ -> param2; !null, _ -> param1", pure = true)
    public static<Value> Value getDefaultIfNull(@Nullable Value toCheck, @NotNull Value defaultValue){
        return (toCheck == null) ? Objects.requireNonNull(defaultValue) : toCheck;
    }

    public static DamageSources getDamageSources(){
        return new DamageSources(getMinecraftServer().registryAccess());
    }

    public static ServerPlayer getServerPlayer(@NotNull UUID uuid){
        return getMinecraftServer().getPlayerList().getPlayer(uuid);
    }

    @Contract("_, _ -> param1")
    public static<T extends ItemMeta> @NotNull T changeName(T meta, Component name){
        meta.displayName(name);
        return meta;
    }

    @Contract("_, _ -> param1")
    public static @NotNull ItemStack changeName(ItemStack stack, Component name){
        stack.editMeta(itemMeta -> changeName(itemMeta, name));
        return stack;
    }


}
