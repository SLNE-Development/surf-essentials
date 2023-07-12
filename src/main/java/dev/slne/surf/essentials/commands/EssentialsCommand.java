package dev.slne.surf.essentials.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.nbtapi.NBTContainer;
import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import dev.slne.surf.essentials.commands.minecraft.WeatherCommand;
import dev.slne.surf.essentials.listener.listeners.CommandRegisterListener;
import dev.slne.surf.essentials.utils.arguments.*;
import dev.slne.surf.essentials.utils.nms.arguments.*;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import net.kyori.adventure.util.TriState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A class that represents a command.
 */
@SuppressWarnings({"PatternValidation", "SameParameterValue"})
public abstract class EssentialsCommand extends CommandTree {
    protected static final String PATTERN = "[a-zA-Z1-9.,!?_]+"; // the pattern for command names
    protected static final Set<EssentialsCommand> COMMANDS = new HashSet<>(); // a set of all commands

    /**
     * Creates a new EssentialsCommand.
     *
     * @param commandName the name of the command
     * @param usage       the usage of the command
     * @param description the description of the command
     * @param aliases     the aliases of the command
     */
    public EssentialsCommand(@Pattern(PATTERN) String commandName, @NotNull String usage, String description, String... aliases) {
        super(commandName);
        withAliases(aliases);
        withUsage((usage.startsWith("/") ? "" : "/") + usage);
        withHelp(description.substring(0, Math.min(description.length(), 50)) + (description.length() > 50 ? "..." : ""), description);
        COMMANDS.add(this);
        CommandRegisterListener.getCommandLabels().add(commandName);
    }

    /**
     * Creates a new EssentialsCommand.
     *
     * @param commandName the name of the command
     * @param usage       the usage of the command
     */
    public EssentialsCommand(@Pattern(PATTERN) String commandName, String usage) {
        this(commandName, usage, "");
    }

    /**
     * Creates a new EssentialsCommand.
     *
     * @param commandName the name of the command
     */
    public EssentialsCommand(@Pattern(PATTERN) String commandName) {
        this(commandName, "");
    }

    /**
     * Registers the command
     */
    @Override
    public final void register() {
        super.register();
    }

    /**
     * Creates a new literal argument.
     *
     * @param name the name of the literal
     * @return a new literal argument
     */
    protected LiteralArgument literal(String name) {
        return LiteralArgument.literal(name);
    }

    protected MultiLiteralArgument multiLiteral(String name, Collection<String> literals) {
        return new MultiLiteralArgument(name, List.copyOf(literals));
    }

    protected MultiLiteralArgument multiLiteral(String name, Stream<String> literals) {
        return new MultiLiteralArgument(name, literals.toList());
    }

    /**
     * Creates a new single player selector argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.entity.Player} object.
     *
     * @param nodeName the name of the argument
     * @return a new single player selector argument
     */
    protected EntitySelectorArgument.OnePlayer playerArgument(String nodeName) {
        return new EntitySelectorArgument.OnePlayer(nodeName);
    }

    /**
     * Creates a new multiple player selector argument.
     * <br>
     * When the argument is parsed, it returns a {@link java.util.Collection<org.bukkit.entity.Player>}{@code <}{@link org.bukkit.entity.Player}{@code >}.
     *
     * @param nodeName the name of the argument
     * @return a new multiple player selector argument
     */
    protected EntitySelectorArgument.ManyPlayers playersArgument(String nodeName) {
        return new EntitySelectorArgument.ManyPlayers(nodeName);
    }

    /**
     * Creates a new single entity selector argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.entity.Entity} object.
     *
     * @param nodeName the name of the argument
     * @return a new single entity selector argument
     */
    protected EntitySelectorArgument.OneEntity entityArgument(String nodeName) {
        return new EntitySelectorArgument.OneEntity(nodeName);
    }

    /**
     * Creates a new multiple entity selector argument.
     * <br>
     * When the argument is parsed, it returns a {@link java.util.Collection<org.bukkit.entity.Entity>}{@code <}{@link org.bukkit.entity.Entity}{@code >}.
     *
     * @param nodeName the name of the argument
     * @return a new multiple entity selector argument
     */
    protected EntitySelectorArgument.ManyEntities entitiesArgument(String nodeName) {
        return new EntitySelectorArgument.ManyEntities(nodeName);
    }

    /**
     * Creates a new integer argument.
     * <br>
     * When the argument is parsed, it returns an {@link Integer} object.
     *
     * @param nodeName the name of the argument
     * @return a new integer argument
     */
    protected IntegerArgument integerArgument(String nodeName) {
        return new IntegerArgument(nodeName);
    }

    /**
     * Creates a new integer argument with a minimum value.
     * <br>
     * When the argument is parsed, it returns an {@link Integer} object.
     *
     * @param nodeName the name of the argument
     * @param min      the minimum value of the argument
     * @return a new integer argument with a minimum value
     */
    protected IntegerArgument integerArgument(String nodeName, int min) {
        return new IntegerArgument(nodeName, min);
    }

    /**
     * Creates a new integer argument with a minimum and maximum value.
     * <br>
     * When the argument is parsed, it returns an {@link Integer} object.
     *
     * @param nodeName the name of the argument
     * @param min      the minimum value of the argument
     * @param max      the maximum value of the argument
     * @return a new integer argument with a minimum and maximum value
     */
    protected IntegerArgument integerArgument(String nodeName, int min, int max) {
        return new IntegerArgument(nodeName, min, max);
    }

    /**
     * Creates a new OfflinePlayer argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.OfflinePlayer} object.
     *
     * @param nodeName the name of the argument
     * @return a new OfflinePlayer argument
     */
    protected OfflinePlayerArgument offlinePlayerArgument(String nodeName) {
        return new OfflinePlayerArgument(nodeName);
    }

    /**
     * Creates a new Boolean argument.
     * <br>
     * When the argument is parsed, it returns a {@link Boolean} object.
     *
     * @param nodeName the name of the argument
     * @return a new Boolean argument
     */
    protected BooleanArgument booleanArgument(String nodeName) {
        return new BooleanArgument(nodeName);
    }

    /**
     * Creates a new Float argument.
     * <br>
     * When the argument is parsed, it returns a {@link Float} object.
     *
     * @param nodeName the name of the argument
     * @return a new Float argument
     */
    protected FloatArgument floatArgument(String nodeName) {
        return new FloatArgument(nodeName);
    }

    /**
     * Creates a new Float argument with a minimum value.
     * <br>
     * When the argument is parsed, it returns a {@link Float} object.
     *
     * @param nodeName the name of the argument
     * @param min      the minimum value of the argument
     * @return a new Float argument with a minimum value
     */
    protected FloatArgument floatArgument(String nodeName, float min) {
        return new FloatArgument(nodeName, min);
    }

    /**
     * Creates a new Float argument with a minimum and maximum value.
     * <br>
     * When the argument is parsed, it returns a {@link Float} object.
     *
     * @param nodeName the name of the argument
     * @param min      the minimum value of the argument
     * @param max      the maximum value of the argument
     * @return a new Float argument with a minimum and maximum value
     */
    protected FloatArgument floatArgument(String nodeName, float min, float max) {
        return new FloatArgument(nodeName, min, max);
    }

    /**
     * Creates a new word argument that accepts a single word and no special characters.
     * <br>
     * When the argument is parsed, it returns a {@link String} object.
     *
     * @param nodeName the name of the argument
     * @return a new word argument
     */
    protected StringArgument wordArgument(String nodeName) {
        return new StringArgument(nodeName);
    }

    /**
     * Creates a new string argument that accepts multiple words but the must be enclosed in quotes.
     * <br>
     * When the argument is parsed, it returns a {@link String} object.
     *
     * @param nodeName the name of the argument
     * @return a new string argument
     */
    protected TextArgument stringArgument(String nodeName) {
        return new TextArgument(nodeName);
    }

    /**
     * Creates a new greedy string argument that accepts unlimited words and does not require quotes.
     * <br>
     * When the argument is parsed, it returns a {@link String} object.
     *
     * @param nodeName the name of the argument
     * @return a new greedy string argument
     */
    protected GreedyStringArgument greedyStringArgument(String nodeName) {
        return new GreedyStringArgument(nodeName);
    }

    /**
     * Creates a new Poll argument.
     * <br>
     * When the argument is parsed, it returns a {@link dev.slne.surf.essentials.commands.general.other.poll.Poll} object.
     *
     * @param nodeName the name of the argument
     * @return a new Poll argument
     */
    protected PollArgument pollArgument(String nodeName) {
        return new PollArgument(nodeName);
    }

    /**
     * Creates a new world argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.World} object.
     *
     * @param nodeName the name of the argument
     * @return a new world argument
     */
    protected WorldArgument worldArgument(String nodeName) {
        return new WorldArgument(nodeName);
    }

    /**
     * Creates a new world type argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.WorldType} object.
     *
     * @param nodeName the name of the argument
     * @return a new world type argument
     */
    protected WorldTypeArgument worldTypeArgument(String nodeName) {
        return new WorldTypeArgument(nodeName);
    }

    /**
     * Creates a new world environment argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.World.Environment} object.
     *
     * @param nodeName the name of the argument
     * @return a new world environment argument
     */
    protected WorldEnvironmentArgument worldEnvironmentArgument(String nodeName) {
        return new WorldEnvironmentArgument(nodeName);
    }

    /**
     * Creates a new long argument.
     * <br>
     * When the argument is parsed, it returns a {@link Long} object.
     *
     * @param nodeName the name of the argument
     * @return a new long argument
     */
    protected LongArgument longArgument(String nodeName) {
        return new LongArgument(nodeName);
    }

    /**
     * Creates a new long argument with a minimum value.
     * <br>
     * When the argument is parsed, it returns a {@link Long} object.
     *
     * @param nodeName the name of the argument
     * @param min      the minimum value of the argument
     * @return a new long argument with a minimum value
     */
    protected LongArgument longArgument(String nodeName, long min) {
        return new LongArgument(nodeName, min);
    }

    /**
     * Creates a new long argument with a minimum and maximum value.
     * <br>
     * When the argument is parsed, it returns a {@link Long} object.
     *
     * @param nodeName the name of the argument
     * @param min      the minimum value of the argument
     * @param max      the maximum value of the argument
     * @return a new long argument with a minimum and maximum value
     */
    protected LongArgument longArgument(String nodeName, long min, long max) {
        return new LongArgument(nodeName, min, max);
    }

    /**
     * Creates a new time argument.
     * <br>
     * When the argument is parsed, it returns a {@link Integer} object, which represents the amount of ticks.
     *
     * @param nodeName the name of the argument
     * @return a new time argument
     */
    protected TimeArgument timeArgument(String nodeName) {
        return new TimeArgument(nodeName);
    }

    /**
     * Creates a new item stack predicate argument.
     * <br>
     * When the argument is parsed, it returns a
     * {@link java.util.function.Predicate<org.bukkit.inventory.ItemStack>}{@code <}{@link org.bukkit.inventory.ItemStack}{@code >} object.
     *
     * @param nodeName the name of the argument
     * @return a new item stack predicate argument
     */
    protected ItemStackPredicateArgument itemStackPredicateArgument(String nodeName) {
        return new ItemStackPredicateArgument(nodeName);
    }

    /**
     * Creates a new double argument.
     * <br>
     * When the argument is parsed, it returns a {@link Double} object.
     *
     * @param nodeName the name of the argument
     * @return a new double argument
     */
    protected DoubleArgument doubleArgument(String nodeName) {
        return new DoubleArgument(nodeName);
    }

    /**
     * Creates a new double argument with a minimum value.
     * <br>
     * When the argument is parsed, it returns a {@link Double} object.
     *
     * @param nodeName the name of the argument
     * @param min      the minimum value of the argument
     * @return a new double argument with a minimum value
     */
    protected DoubleArgument doubleArgument(String nodeName, double min) {
        return new DoubleArgument(nodeName, min);
    }

    /**
     * Creates a new double argument with a minimum and maximum value.
     * <br>
     * When the argument is parsed, it returns a {@link Double} object.
     *
     * @param nodeName the name of the argument
     * @param min      the minimum value of the argument
     * @param max      the maximum value of the argument
     * @return a new double argument with a minimum and maximum value
     */
    protected DoubleArgument doubleArgument(String nodeName, double min, double max) {
        return new DoubleArgument(nodeName, min, max);
    }

    /**
     * Creates a new location argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.Location} object.
     *
     * @param nodeName the name of the argument
     * @return a new location argument
     */
    protected LocationArgument locationArgument(String nodeName) {
        return locationArgument(nodeName, LocationType.PRECISE_POSITION);
    }

    /**
     * Creates a new location argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.Location} object.
     *
     * @param nodeName     the name of the argument
     * @param locationType the type of the location
     * @return a new location argument
     */
    protected LocationArgument locationArgument(String nodeName, LocationType locationType) {
        return new LocationArgument(nodeName, locationType);
    }

    /**
     * Creates a new location 2D argument.
     * <br>
     * When the argument is parsed, it returns a {@link dev.jorel.commandapi.wrappers.Location2D} object.
     *
     * @param nodeName the name of the argument
     * @return a new location 2D argument
     */
    protected Location2DArgument location2DArgument(String nodeName) {
        return location2DArgument(nodeName, LocationType.PRECISE_POSITION);
    }

    /**
     * Creates a new location 2D argument.
     * <br>
     * When the argument is parsed, it returns a {@link dev.jorel.commandapi.wrappers.Location2D} object.
     *
     * @param nodeName     the name of the argument
     * @param locationType the type of the location
     * @return a new location 2D argument
     */
    protected Location2DArgument location2DArgument(String nodeName, LocationType locationType) {
        return new Location2DArgument(nodeName, locationType);
    }

    /**
     * Creates a new entity type argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.entity.EntityType} object.
     *
     * @param nodeName the name of the argument
     * @return a new entity type argument
     */
    protected EntityTypeArgument entityTypeArgument(String nodeName) {
        return new EntityTypeArgument(nodeName);
    }

    /**
     * Creates a new advancement argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.advancement.Advancement} object.
     *
     * @param nodeName the name of the argument
     * @return a new advancement argument
     */
    protected AdvancementArgument advancementArgument(String nodeName) {
        return new AdvancementArgument(nodeName);
    }

    /**
     * Creates a new data pack argument.
     * <br>
     * When the argument is parsed, it returns a {@link io.papermc.paper.datapack.Datapack} object.
     *
     * @param nodeName the name of the argument
     * @return a new data pack argument
     */
    protected DatapackArgument datapackArgument(String nodeName) {
        return datapackArgument(nodeName, TriState.NOT_SET);
    }

    /**
     * Creates a new data pack argument.
     * <br>
     * When the argument is parsed, it returns a {@link io.papermc.paper.datapack.Datapack} object.
     *
     * @param nodeName    the name of the argument
     * @param onlyEnabled whether to only return enabled data packs
     * @return a new data pack argument
     */
    protected DatapackArgument datapackArgument(String nodeName, TriState onlyEnabled) {
        return new DatapackArgument(nodeName, onlyEnabled);
    }

    /**
     * Creates a new game mode argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.GameMode} object.
     *
     * @param nodeName the name of the argument
     * @return a new game mode argument
     */
    protected GameModeArgument gameModeArgument(String nodeName) {
        return new GameModeArgument(nodeName);
    }

    /**
     * Creates a new difficulty argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.Difficulty} object.
     *
     * @param nodeName the name of the argument
     * @return a new difficulty argument
     */
    protected DifficultyArgument difficultyArgument(String nodeName) {
        return new DifficultyArgument(nodeName);
    }

    /**
     * Creates a new potion effect argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.potion.PotionEffectType} object.
     *
     * @param nodeName the name of the argument
     * @return a new potion effect argument
     */
    protected PotionEffectArgument potionEffectArgument(String nodeName) {
        return new PotionEffectArgument(nodeName);
    }

    /**
     * Creates a new namepaced key argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.NamespacedKey} object.
     *
     * @param nodeName the name of the argument
     * @return a new namepaced key argument
     */
    protected NamespacedKeyArgument keyArgument(String nodeName) {
        return new NamespacedKeyArgument(nodeName);
    }

    /**
     * Creates a new biome argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.block.Biome} object.
     *
     * @param nodeName the name of the argument
     * @return a new biome argument
     */
    protected BiomeArgument biomeArgument(String nodeName) {
        return new BiomeArgument(nodeName);
    }

    /**
     * Creates a new block state argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.block.data.BlockData} object.
     *
     * @param nodeName the name of the argument
     * @return a new block state argument
     */
    protected BlockStateArgument blockStateArgument(String nodeName) {
        return new BlockStateArgument(nodeName);
    }

    /**
     * Creates a new block predicate argument.
     * <br>
     * When the argument is parsed, it returns a
     * {@link java.util.function.Predicate<org.bukkit.block.Block>}{@code <}{@link org.bukkit.block.Block}{@code >} object.
     *
     * @param nodeName the name of the argument
     * @return a new block predicate argument
     */
    protected BlockPredicateArgument blockPredicateArgument(String nodeName) {
        return new BlockPredicateArgument(nodeName);
    }

    /**
     * Creates a new function argument.
     * <br>
     * When the argument is parsed, it returns a {@link dev.jorel.commandapi.wrappers.FunctionWrapper}{@literal  []} array.
     *
     * @param nodeName the name of the argument
     * @return a new function argument
     */
    protected FunctionArgument functionArgument(String nodeName) {
        return new FunctionArgument(nodeName);
    }

    /**
     * Creates a new item stack argument.
     * <br>
     * When the argument is parsed, it returns a {@link org.bukkit.inventory.ItemStack} object.
     *
     * @param nodeName the name of the argument
     * @return a new item stack argument
     */
    protected ItemStackArgument itemStackArgument(String nodeName) {
        return new ItemStackArgument(nodeName);
    }

    /**
     * Creates a new bukkitParticle argument.
     * <br>
     * When the argument is parsed, it returns a {@link dev.jorel.commandapi.wrappers.ParticleData}{@code <?>} object.
     *
     * @param nodeName the name of the argument
     * @return a new bukkitParticle argument
     */
    protected ParticleArgument particleArgument(String nodeName) {
        return new ParticleArgument(nodeName);
    }

    /**
     * Creates a new angle argument.
     * <br>
     * When the argument is parsed, it returns a {@link Float} object.
     *
     * @param nodeName the name of the argument
     * @return a new angle argument
     */
    protected AngleArgument angleArgument(String nodeName) {
        return new AngleArgument(nodeName);
    }

    /**
     * Creates a new nbt compound argument.
     * <br>
     * When the argument is parsed, it returns a {@link NBTContainer} object.
     *
     * @param nodeName the name of the argument
     * @return a new nbt compound argument
     */
    protected NBTCompoundArgument<NBTContainer> nbtCompoundArgument(String nodeName) {
        return new NBTCompoundArgument<>(nodeName);
    }

    /**
     * Creates a new weather type argument.
     * <br>
     * When the argument is parsed, it returns a {@link WeatherCommand.WeatherType} object.
     *
     * @param nodeName the name of the argument
     * @return a new weather type argument
     */
    protected WeatherTypeArgument weatherTypeArgument(String nodeName) {
        return new WeatherTypeArgument(nodeName);
    }

    /**
     * Creates a new rotation argument.
     * <br>
     * When the argument is parsed, it returns a {@link dev.jorel.commandapi.wrappers.Rotation} object.
     *
     * @param nodeName the name of the argument
     * @return a new rotation argument
     */
    protected RotationArgument rotationArgument(String nodeName) {
        return new RotationArgument(nodeName);
    }

    /**
     * Creates a new look-anchor argument.
     * <br>
     * When the argument is parsed, it returns a {@link io.papermc.paper.entity.LookAnchor} object.
     *
     * @param nodeName the name of the argument
     * @return a new look-anchor argument
     */
    protected LookAnchorArgument lookAnchorArgument(String nodeName) {
        return new LookAnchorArgument(nodeName);
    }


    /**
     * Gets the {@link Player} from the {@link NativeProxyCommandSender} or throws a {@link WrapperCommandSyntaxException}
     *
     * @param sender the sender to get the player from
     * @return the player
     * @throws WrapperCommandSyntaxException if the sender is not a player
     */
    protected Player getPlayerOrException(NativeProxyCommandSender sender) throws WrapperCommandSyntaxException {
        if (!(sender.getCallee() instanceof Player player)) {
            throw Exceptions.ERROR_NOT_PLAYER;
        }

        return player;
    }

    /**
     * Gets the {@link Entity} from the {@link NativeProxyCommandSender} or throws a {@link WrapperCommandSyntaxException}
     *
     * @param sender the sender to get the entity from
     * @return the entity
     * @throws WrapperCommandSyntaxException if the sender is not an entity
     */
    protected Entity getEntityOrException(NativeProxyCommandSender sender) throws WrapperCommandSyntaxException {
        if (!(sender.getCallee() instanceof Entity entity)) {
            throw Exceptions.ERROR_NOT_ENTITY;
        }
        return entity;
    }

    /**
     * Gets any entity from the {@link NativeProxyCommandSender} or throws a {@link WrapperCommandSyntaxException}
     *
     * @param sender the sender to get the entity from
     * @param type   the type of entity to get
     * @param <E>    the type of entity to get
     * @return the entity
     * @throws WrapperCommandSyntaxException if the sender is not an entity
     */
    protected <E extends Entity> E getSpecialEntityOrException(NativeProxyCommandSender sender, Class<E> type) throws WrapperCommandSyntaxException {
        Entity entity = getEntityOrException(sender);
        if (!type.isInstance(entity)) {
            throw Exceptions.ERROR_INVALID_ENTITY.create(type);
        }

        return type.cast(entity);
    }
}
