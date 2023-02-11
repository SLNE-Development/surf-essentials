package dev.slne.surf.essentials.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import net.kyori.adventure.nbt.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.nbt.BinaryTagIO.Compression.GZIP;

public abstract class EssentialsUtil {
    /**
     * An array of {@link Sound} objects representing the sounds that can be played to
     * scare the player.
     */
    public static Sound[] scareSounds = new Sound[]{Sound.ENTITY_LIGHTNING_BOLT_THUNDER, Sound.ENTITY_WOLF_HOWL,
            Sound.ENTITY_BAT_DEATH, Sound.ENTITY_GHAST_SCREAM, Sound.ENTITY_GHAST_HURT};

    public static final DynamicCommandExceptionType ERROR_POSITION_IN_UNLOADED_WORLD = new DynamicCommandExceptionType(gameProfile ->
            net.minecraft.network.chat.Component.literal(((GameProfile) gameProfile).getName() + " has logged out in an unloaded world."));

    public static final int MAX_FOOD = 20;


    /**
     *
     * Converts the color from the input string to a gradient.
     *
     * @param input  the string to convert the color from
     * @param firstHex  the first hex color
     * @param secondHex  the second hex color
     */
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

    /**
     *
     * converts ticks in a time format.
     *
     * @param ticks  the ticks to convert
     * @return Time format in string
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
     *
     * Sends an error message to the sender.
     *
     * @param sender  the sender
     * @param error  the error
     */
    public static void somethingWentWrongAsync_DE(@NotNull Player sender, @NotNull String error){
        SurfApi.getUser(sender).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(gradientify("Es ist ein Fehler aufgetreten:", "#eb3349", "#f45c43"))
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                .append(gradientify(error, "#EA98DA", "#5B6CF9"))));
    }

    /**
     *
     * sorts the tab completion suggestion
     *
     * @param list  the list
     * @param currentarg  the currentarg
     * @param completions  the completions
     */
    public static void sortedSuggestions(@NotNull List<String> list, @NotNull String currentarg, @NotNull List<String> completions){
        for (String s : list) {
            if (s.toLowerCase().startsWith(currentarg)) {
                completions.add(s);
            }
        }
    }

    /**
     * Returns whether the given player is vanished or not.
     * A player is considered vanished if they have a "vanished" metadata value
     * set to `true`.
     *
     * @param player the player to check
     * @return `true` if the player is vanished, `false` otherwise
     */
    public static boolean isVanished(@NotNull Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

    /**
     * Suggests all possible color codes to the given {@link SuggestionsBuilder}.
     *
     * @param builder the {@link SuggestionsBuilder} to which the color codes will be added
     * @return a {@link CompletableFuture} containing the {@link Suggestions}
     */
    public static CompletableFuture<Suggestions> suggestAllColorCodes(@NotNull SuggestionsBuilder builder) {
        // <editor-fold defaultstate="collapsed" desc="allColorCodes">
        builder.suggest("&0", net.minecraft.network.chat.Component.literal("Black").withStyle(ChatFormatting.BLACK));
        builder.suggest("&2", net.minecraft.network.chat.Component.literal("Dark Green").withStyle(ChatFormatting.DARK_GREEN));
        builder.suggest("&4", net.minecraft.network.chat.Component.literal("Dark Red").withStyle(ChatFormatting.DARK_RED));
        builder.suggest("&6", net.minecraft.network.chat.Component.literal("Gold").withStyle(ChatFormatting.GOLD));
        builder.suggest("&8", net.minecraft.network.chat.Component.literal("Dark Gray").withStyle(ChatFormatting.DARK_GRAY));
        builder.suggest("&a", net.minecraft.network.chat.Component.literal("Green").withStyle(ChatFormatting.GREEN));
        builder.suggest("&c", net.minecraft.network.chat.Component.literal("Red").withStyle(ChatFormatting.RED));
        builder.suggest("&e", net.minecraft.network.chat.Component.literal("Yellow").withStyle(ChatFormatting.YELLOW));
        builder.suggest("&1", net.minecraft.network.chat.Component.literal("Dark Blue").withStyle(ChatFormatting.DARK_BLUE));
        builder.suggest("&3", net.minecraft.network.chat.Component.literal("Dark Aqua").withStyle(ChatFormatting.DARK_AQUA));
        builder.suggest("&5", net.minecraft.network.chat.Component.literal("Dark Purple").withStyle(ChatFormatting.DARK_PURPLE));
        builder.suggest("&7", net.minecraft.network.chat.Component.literal("Gray").withStyle(ChatFormatting.GRAY));
        builder.suggest("&9", net.minecraft.network.chat.Component.literal("Blue").withStyle(ChatFormatting.BLUE));
        builder.suggest("&b", net.minecraft.network.chat.Component.literal("Aqua").withStyle(ChatFormatting.AQUA));
        builder.suggest("&d", net.minecraft.network.chat.Component.literal("Light Purple").withStyle(ChatFormatting.LIGHT_PURPLE));
        builder.suggest("&f", net.minecraft.network.chat.Component.literal("White").withStyle(ChatFormatting.WHITE));

        builder.suggest("&k", net.minecraft.network.chat.Component.literal("Obfuscated").withStyle(ChatFormatting.OBFUSCATED));
        builder.suggest("&m", net.minecraft.network.chat.Component.literal("Strikethrough").withStyle(ChatFormatting.STRIKETHROUGH));
        builder.suggest("&o", net.minecraft.network.chat.Component.literal("Italic").withStyle(ChatFormatting.ITALIC));
        builder.suggest("&l", net.minecraft.network.chat.Component.literal("Bold").withStyle(ChatFormatting.BOLD));
        builder.suggest("&n", net.minecraft.network.chat.Component.literal("Underline").withStyle(ChatFormatting.UNDERLINE));
        builder.suggest("&r", net.minecraft.network.chat.Component.literal("Reset").withStyle(ChatFormatting.RESET));
        // </editor-fold>
        return builder.buildFuture();
    }

    /**
     * Suggests all possible color codes to the given {@link SuggestionsBuilder} with the {@link StringArgumentType} input.
     *
     * @param builder the {@link SuggestionsBuilder} to which the color codes will be added
     * @param context the {@link CommandContext<CommandSourceStack>}
     * @param stringArgumentType the {@link StringArgumentType} from the current argument
     * @return a {@link CompletableFuture} containing the {@link Suggestions}
     */
    public static CompletableFuture<Suggestions> suggestAllColorCodes(@NotNull SuggestionsBuilder builder, @NotNull CommandContext<CommandSourceStack> context, @NotNull String stringArgumentType) {
        String input;
        try {
            input = context.getArgument(stringArgumentType, String.class);
        }catch (IllegalArgumentException ignored) {
            input = "";
        }

        // <editor-fold defaultstate="collapsed" desc="allColorCodes">
        builder.suggest("\"" + input + "&0", net.minecraft.network.chat.Component.literal("Black").withStyle(ChatFormatting.BLACK));
        builder.suggest("\"" + input + "&2", net.minecraft.network.chat.Component.literal("Dark Green").withStyle(ChatFormatting.DARK_GREEN));
        builder.suggest("\"" + input + "&4", net.minecraft.network.chat.Component.literal("Dark Red").withStyle(ChatFormatting.DARK_RED));
        builder.suggest("\"" + input + "&6", net.minecraft.network.chat.Component.literal("Gold").withStyle(ChatFormatting.GOLD));
        builder.suggest("\"" + input + "&8", net.minecraft.network.chat.Component.literal("Dark Gray").withStyle(ChatFormatting.DARK_GRAY));
        builder.suggest("\"" + input + "&a", net.minecraft.network.chat.Component.literal("Green").withStyle(ChatFormatting.GREEN));
        builder.suggest("\"" + input + "&c", net.minecraft.network.chat.Component.literal("Red").withStyle(ChatFormatting.RED));
        builder.suggest("\"" + input + "&e", net.minecraft.network.chat.Component.literal("Yellow").withStyle(ChatFormatting.YELLOW));
        builder.suggest("\"" + input + "&1", net.minecraft.network.chat.Component.literal("Dark Blue").withStyle(ChatFormatting.DARK_BLUE));
        builder.suggest("\"" + input + "&3", net.minecraft.network.chat.Component.literal("Dark Aqua").withStyle(ChatFormatting.DARK_AQUA));
        builder.suggest("\"" + input + "&5", net.minecraft.network.chat.Component.literal("Dark Purple").withStyle(ChatFormatting.DARK_PURPLE));
        builder.suggest("\"" + input + "&7", net.minecraft.network.chat.Component.literal("Gray").withStyle(ChatFormatting.GRAY));
        builder.suggest("\"" + input + "&9", net.minecraft.network.chat.Component.literal("Blue").withStyle(ChatFormatting.BLUE));
        builder.suggest("\"" + input + "&b", net.minecraft.network.chat.Component.literal("Aqua").withStyle(ChatFormatting.AQUA));
        builder.suggest("\"" + input + "&d", net.minecraft.network.chat.Component.literal("Light Purple").withStyle(ChatFormatting.LIGHT_PURPLE));
        builder.suggest("\"" + input + "&f", net.minecraft.network.chat.Component.literal("White").withStyle(ChatFormatting.WHITE));

        builder.suggest("\"" + input + "&k", net.minecraft.network.chat.Component.literal("Obfuscated").withStyle(ChatFormatting.OBFUSCATED));
        builder.suggest("\"" + input + "&m", net.minecraft.network.chat.Component.literal("Strikethrough").withStyle(ChatFormatting.STRIKETHROUGH));
        builder.suggest("\"" + input + "&o", net.minecraft.network.chat.Component.literal("Italic").withStyle(ChatFormatting.ITALIC));
        builder.suggest("\"" + input + "&l", net.minecraft.network.chat.Component.literal("Bold").withStyle(ChatFormatting.BOLD));
        builder.suggest("\"" + input + "&n", net.minecraft.network.chat.Component.literal("Underline").withStyle(ChatFormatting.UNDERLINE));
        builder.suggest("\"" + input + "&r", net.minecraft.network.chat.Component.literal("Reset").withStyle(ChatFormatting.RESET));
        // </editor-fold>
        return builder.buildFuture();
    }

    /**
     * Scares the player by playing a random scare sound from the {@link #scareSounds} array and
     * applying a {@link PotionEffectType#DARKNESS} effect to the player for 7 seconds.
     *
     * @param player the player to scare
     */
    public static void scarePlayer(@NotNull Player player) {
        Random random = new Random();
        int scareIndex = random.nextInt(scareSounds.length - 1);
        Sound scareSound = scareSounds[scareIndex];
        player.playSound(player.getLocation(), scareSound, 1.0F, 1.0F);
        PotionEffect scareEffect = new PotionEffect(PotionEffectType.DARKNESS, 20*7, 1, false, false, false);
        player.addPotionEffect(scareEffect);
    }


    /**
     Sends an error message to the player.
     @param source the command source
     @param error the error message to send
     @throws CommandSyntaxException if an error occurs while sending the message
     */
    public static void sendError(CommandSourceStack source, String error) throws CommandSyntaxException {
        SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(net.kyori.adventure.text.Component.text(error, SurfColors.ERROR))));
    }

    /**
     Sends an error message to the player.
     @param source the command source
     @param error the error message to send
     @throws CommandSyntaxException if an error occurs while sending the message
     */
    public static void sendError(CommandSourceStack source, Component error) throws CommandSyntaxException {
        SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(error)));
    }

    /**
     * Sends a success message to the player associated with the specified command source stack.
     *
     * @param source the {@link CommandSourceStack} to get the player from
     * @param success the success message to send as a String
     * @throws CommandSyntaxException if the player cannot be found
     */
    public static void sendSuccess(CommandSourceStack source, String success) throws CommandSyntaxException {
        SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(Component.text(success, SurfColors.SUCCESS))));
    }

    /**
     * Sends a success message to the player associated with the specified command source stack.
     *
     * @param source the {@link CommandSourceStack} to get the player from
     * @param success the success message to send as a {@link Component}
     * @throws CommandSyntaxException if the player cannot be found
     */
    public static void sendSuccess(CommandSourceStack source, Component success) throws CommandSyntaxException {
        SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(success)));
    }

    public static void sendInfo(CommandSourceStack source, Component info) throws CommandSyntaxException {
        SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(info)));
    }

    public static void sendInfo(CommandSourceStack source, String info) throws CommandSyntaxException {
        sendInfo(source, Component.text(info, SurfColors.INFO));
    }



    /**
     * Builds a command build context.
     *
     * @return the built command build context
     */
    public static @NotNull CommandBuildContext buildContext(){
        return CommandBuildContext.configurable(MinecraftServer.getServer().registryAccess(),
            MinecraftServer.getServer().getWorldData().getDataConfiguration().enabledFeatures());
    }

    public static boolean canPlayerSeePlayer(@NotNull ServerPlayer player, @NotNull ServerPlayer playerToCheck){
        if (!isVanished(playerToCheck.getBukkitEntity())) return true;
        return player.getBukkitEntity().canSee(playerToCheck.getBukkitEntity());
    }

    public static Collection<ServerPlayer> checkPlayerSuggestion(CommandSourceStack source, Collection<ServerPlayer> targets) throws CommandSyntaxException {
        if (!source.isPlayer()) return targets;
        if (targets.size() == 1) {
            ServerPlayer player = targets.iterator().next();

            if (canPlayerSeePlayer(source.getPlayerOrException(), player)) return targets;
            throw EntityArgument.NO_PLAYERS_FOUND.create();
        } else {
            for (ServerPlayer target : targets) {
                if (canPlayerSeePlayer(source.getPlayerOrException(), target)) continue;
                targets.remove(target);
            }
            return targets;
        }
    }

    public static Collection<? extends Entity> checkEntitySuggestion(CommandSourceStack source, Collection<? extends Entity> targets) throws CommandSyntaxException {
        if (!source.isPlayer()) return targets;
        if (targets.size() == 1) {
            Entity entity = targets.iterator().next();

            if (entity instanceof ServerPlayer serverPlayer) {
                if (canPlayerSeePlayer(source.getPlayerOrException(), serverPlayer)) return targets;
                throw EntityArgument.NO_ENTITIES_FOUND.create();
            }
        } else {
            for (Entity target : targets) {
                if (target instanceof ServerPlayer serverPlayer) {
                    if (canPlayerSeePlayer(source.getPlayerOrException(), serverPlayer)) continue;
                    targets.remove(target);
                }
            }
        }
        return targets;
    }

    public static <T extends ServerPlayer> ServerPlayer checkSinglePlayerSuggestion(CommandSourceStack source, T player) throws CommandSyntaxException {
        Collection<ServerPlayer> players = checkPlayerSuggestion(source, Collections.singleton(player));
        return players.iterator().next();
    }

    public static <T extends Entity> Entity checkSingleEntitySuggestion(CommandSourceStack source, T entity) throws CommandSyntaxException{
        Collection<? extends Entity> entities = checkEntitySuggestion(source, Collections.singleton(entity));
        return entities.iterator().next();
    }

    public static Component deserialize(String toDeserialize){
        return LegacyComponentSerializer.legacyAmpersand().deserialize(toDeserialize);
    }

    public static <T extends Double> double makeDoubleReadable(T value){
        return Double.parseDouble(new DecimalFormat("#.#").format(value));
    }

    public static File getPlayerFile(UUID uuid) {
        for (World world : Bukkit.getWorlds()) {
            File worldFolder = world.getWorldFolder();
            if (!worldFolder.isDirectory()) continue;
            File playerDataFolder = new File(worldFolder, "playerdata");
            if (!playerDataFolder.isDirectory()) continue;
            File playerFile = new File(playerDataFolder, uuid.toString() + ".dat");
            if (playerFile.exists()) return playerFile;
        }
        return null;
    }

    public static Location getLocation(GameProfile gameProfile) throws IOException, CommandSyntaxException {
        File dataFile = getPlayerFile(gameProfile.getId());

        if (dataFile == null) return null;
        CompoundBinaryTag tag = BinaryTagIO.unlimitedReader().read(dataFile.toPath(), GZIP);
        ListBinaryTag posTag = tag.getList("Pos");
        ListBinaryTag rotTag = tag.getList("Rotation");

        long worldUUIDMost = tag.getLong("WorldUUIDMost");
        long worldUUIDLeast = tag.getLong("WorldUUIDLeast");

        World world = Bukkit.getWorld(new UUID(worldUUIDMost, worldUUIDLeast));

        if (world == null) throw ERROR_POSITION_IN_UNLOADED_WORLD.create(gameProfile);

        return new Location(world, posTag.getDouble(0), posTag.getDouble(1), posTag.getDouble(2), rotTag.getFloat(0), rotTag.getFloat(1));
    }

    public static void setLocation(UUID uuid, Location location) throws IOException{
        File dataFile = EssentialsUtil.getPlayerFile(uuid);

        if (dataFile == null) return;
        CompoundBinaryTag rawTag = BinaryTagIO.unlimitedReader().read(dataFile.toPath(), GZIP);
        CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder().put(rawTag);

        ListBinaryTag.Builder<BinaryTag> posTag = ListBinaryTag.builder();
        posTag.add(DoubleBinaryTag.of(location.getX()));
        posTag.add(DoubleBinaryTag.of(location.getY()));
        posTag.add(DoubleBinaryTag.of(location.getZ()));

        ListBinaryTag.Builder<BinaryTag> rotTag = ListBinaryTag.builder();
        rotTag.add(FloatBinaryTag.of(location.getYaw()));
        rotTag.add(FloatBinaryTag.of(location.getPitch()));

        builder.put("Pos", posTag.build());
        builder.put("Rotation", rotTag.build());

        long worldUUIDLeast = location.getWorld().getUID().getLeastSignificantBits();
        long worldUUIDMost = location.getWorld().getUID().getMostSignificantBits();
        builder.putLong("WorldUUIDLeast", worldUUIDLeast);
        builder.putLong("WorldUUIDMost", worldUUIDMost);

        BinaryTagIO.writer().write(builder.build(), dataFile.toPath(), GZIP);
    }

    public static boolean isNmsSupported(){
        try {
            Class.forName(CraftWorld.class.getName());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
