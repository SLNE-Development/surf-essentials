package dev.slne.surf.essentialsold.utils.brigadier;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import io.papermc.paper.datapack.Datapack;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
// TODO: all error are blue instead of red
@UtilityClass
public class Exceptions { // TODO: Add more java docs
    /**
     * The exception thrown when a player is required but the sender is not a player
     */
    public final WrapperCommandSyntaxException ERROR_NOT_PLAYER = EssentialsUtil.createException(Component.translatable("permissions.requires.player", Colors.ERROR));

    /**
     * The exception thrown when an entity is required but the sender is not an entity
     */
    public final WrapperCommandSyntaxException ERROR_NOT_ENTITY = EssentialsUtil.createException(Component.translatable("permissions.requires.entity", Colors.ERROR));

    /**
     * The exception thrown when no players were found
     */
    public final WrapperCommandSyntaxException NO_PLAYERS_FOUND = EssentialsUtil.createException(Component.translatable("argument.entity.notfound.player", Colors.ERROR));

    /**
     * The exception thrown when no entities were found
     */
    public final WrapperCommandSyntaxException NO_ENTITIES_FOUND = EssentialsUtil.createException(Component.translatable("argument.entity.notfound.entity", Colors.ERROR));

    /**
     * The exception thrown when the player was never op
     */
    public final WrapperCommandSyntaxException ERROR_PLAYER_WAS_NEVER_OP = EssentialsUtil.createException(Component.translatable("commands.deop.failed", Colors.ERROR));

    /**
     * The exception thrown when the effect give command fails
     */
    public final WrapperCommandSyntaxException ERROR_EFFECT_GIVE_FAIL = EssentialsUtil.createException(Component.translatable("commands.effect.give.failed", Colors.ERROR));

    /**
     * The exception thrown when the effect clear specific command fails
     */
    public final WrapperCommandSyntaxException ERROR_EFFECT_CLEAR_SPECIFIC_FAIL = EssentialsUtil.createException(Component.translatable("commands.effect.clear.specific.failed", Colors.ERROR));

    /**
     * The exception thrown when the effect clear everything command fails
     */
    public final WrapperCommandSyntaxException ERROR_EFFECT_CLEAR_EVERYTHING_FAIL = EssentialsUtil.createException(Component.translatable("commands.effect.clear.everything.failed", Colors.ERROR));

    /**
     * The exception is thrown when the item already has the enchantment and nothing happens
     */
    public final WrapperCommandSyntaxException ERROR_ENCHANT_NOTHING_HAPPENED = EssentialsUtil.createException(Component.translatable("commands.enchant.failed", Colors.ERROR));

    /**
     * The exception is thrown when the fill command fails
     */
    public final WrapperCommandSyntaxException ERROR_FILL_FAILED = EssentialsUtil.createException(Component.translatable("commands.fill.failed", Colors.ERROR));

    /**
     * The exception is thrown when the location is out of the world
     */
    public final WrapperCommandSyntaxException ERROR_OUT_OF_WORLD = EssentialsUtil.createException(Component.translatable("argument.pos.outofworld", Colors.ERROR));

    /**
     * The exception is thrown when more than one player was found
     */
    public final WrapperCommandSyntaxException ERROR_NOT_SINGLE_PLAYER = EssentialsUtil.createException(Component.translatable("argument.player.toomany", Colors.ERROR));

    public final WrapperCommandSyntaxException ERROR_ALREADY_OP = EssentialsUtil.createException(Component.translatable("commands.op.failed", Colors.ERROR));

    public final WrapperCommandSyntaxException ERROR_NO_PARTICLES_SHOWN = EssentialsUtil.createException(Component.translatable("commands.bukkitParticle.failed", Colors.ERROR));

    public final WrapperCommandSyntaxException ERROR_BLOCK_NOT_SET = EssentialsUtil.createException(Component.translatable("commands.setblock.failed", Colors.ERROR));

    public final WrapperCommandSyntaxException ERROR_CANNOT_SPECTATE_SELF = EssentialsUtil.createException(Component.translatable("commands.spectate.self", Colors.ERROR));

    public final WrapperCommandSyntaxException ERROR_WHITELIST_ALREADY_ENABLED = EssentialsUtil.createException(Component.translatable("commands.whitelist.alreadyOn", Colors.ERROR));

    public final WrapperCommandSyntaxException ERROR_WHITELIST_ALREADY_DISABLED = EssentialsUtil.createException(Component.translatable("commands.whitelist.alreadyOff", Colors.ERROR));

    public final WrapperCommandSyntaxException ERROR_ALREADY_WHITELISTED = EssentialsUtil.createException(Component.translatable("commands.whitelist.add.failed", Colors.ERROR));

    public final WrapperCommandSyntaxException ERROR_NOT_WHITELISTED = EssentialsUtil.createException(Component.translatable("commands.whitelist.remove.failed", Colors.ERROR));

    public final WrapperCommandSyntaxException ERROR_INVULNERABLE = EssentialsUtil.createException(Component.translatable("commands.damage.invulnerable", Colors.ERROR));


    /**
     * The exception is thrown when the given dimension is invalid (not loaded or doesn't exist)
     */
    public final ComponentConsumer<String> ERROR_INVALID_DIMENSION = dimension -> Component.translatable("argument.dimension.invalid", Colors.ERROR, Component.text(dimension, Colors.VARIABLE_VALUE));

    /**
     * The exception is thrown when the given gamemode is invalid (see {@link org.bukkit.GameMode} for valid gamemodes)
     */
    public final ComponentConsumer<String> ERROR_INVALID_GAME_MODE = gameMode -> Component.translatable("argument.gamemode.invalid", Colors.ERROR, Component.text(gameMode, Colors.VARIABLE_VALUE));

    /**
     * The exception is thrown when the world has already the given difficulty
     */
    public final ComponentConsumer<Translatable> ERROR_ALREADY_THAT_DIFFICULT = difficulty -> Component.translatable("commands.difficulty.failure", Colors.ERROR, Component.translatable(difficulty, Colors.VARIABLE_VALUE));

    /**
     * The exception is thrown when the entity is not holding an item
     */
    public final ComponentConsumer<Entity> ERROR_NOT_HOLDING_ITEM = entity -> Component.translatable("commands.enchant.failed.itemless", Colors.ERROR, EssentialsUtil.getDisplayName(entity));

    /**
     * The exception is thrown when the item is not compatible with the given enchantment
     */
    public final ComponentConsumer<ItemStack> ERROR_ENCHANTMENT_INCOMPATIBLE = item -> Component.translatable("commands.enchant.failed.incompatible", Colors.ERROR, EssentialsUtil.getDisplayName(item));

    /**
     * The exception is thrown when the entity is not valid for the command
     */
    public final ComponentConsumer<Entity> ERROR_NOT_VALID_ENTITY_FOR_COMMAND = entity -> Component.translatable("commands.enchant.failed.entity", Colors.ERROR, EssentialsUtil.getDisplayName(entity));

    /**
     * The exception is thrown when the given resource (e.g. {@code minecraft:enchantment}) is not found
     */
    public final BiComponentConsumer<String, String> ERROR_UNKNOWN_RESOURCE = (element, type) -> Component.translatable("argument.resource.not_found", Colors.ERROR, Component.text(element, Colors.VARIABLE_VALUE), Component.text(type, Colors.VARIABLE_VALUE));

    /**
     * The exception is thrown when the area is too large to fill
     */
    public final BiComponentConsumer<Integer, Integer> ERROR_FILL_AREA_TOO_LARGE = (max, count) -> Component.translatable("commands.fill.toobig", Colors.ERROR, Component.text(max, Colors.VARIABLE_VALUE), Component.text(count, Colors.VARIABLE_VALUE));

    /**
     * The exception is thrown when the enchantment level is too high
     */
    public final BiComponentConsumer<Integer, Integer> ERROR_ENCHANTMENT_LEVEL_TOO_HIGH = (level, maxLevel) -> Component.translatable("commands.enchant.failed.level", Colors.ERROR, Component.text(level, Colors.VARIABLE_VALUE), Component.text(maxLevel, Colors.VARIABLE_VALUE));
    public final BiComponentConsumer<Integer, Number> ERROR_TOO_MANY_CHUNKS = (maxCount, count) -> Component.translatable("commands.forceload.toobig", Colors.ERROR, Component.text(maxCount, Colors.VARIABLE_VALUE), Component.text(count.longValue(), Colors.VARIABLE_VALUE));
    public final BiComponentConsumer<Integer, ItemStack> ERROR_GIVE_TOO_MANY_ITEMS = (maxAllowedAmount, itemStack) -> Component.translatable("commands.give.failed.toomanyitems", Colors.ERROR, Component.text(maxAllowedAmount, Colors.VARIABLE_VALUE), EssentialsUtil.getDisplayName(itemStack));
    public final BiComponentConsumer<Component, String> ADVANCEMENT_CRITERION_NOT_FOUND = (advancement, criterion) -> Component.translatable("commands.advancement.criterionNotFound", Colors.ERROR, advancement, Component.text(criterion, Colors.VARIABLE_VALUE));
    public final TriComponentConsumer<String, Component, Component> ADVANCEMENT_ONE_TO_ONE_FAILURE = (key, advancement, player) -> Component.translatable(key + ".one.to.one.failure", Colors.ERROR, advancement, player);
    public final TriComponentConsumer<String, Component, Integer> ADVANCEMENT_ONE_TO_MANY_FAILURE = (key, advancement, size) -> Component.translatable(key + ".one.to.many.failure", Colors.ERROR, advancement, Component.text(size, Colors.VARIABLE_VALUE));
    public final TriComponentConsumer<String, Integer, Component> ADVANCEMENT_MANY_TO_ONE_FAILURE = (key, size, player) -> Component.translatable(key + ".many.to.one.failure", Colors.ERROR, Component.text(size, Colors.VARIABLE_VALUE), player);
    public final TriComponentConsumer<String, Integer, Integer> ADVANCEMENT_MANY_TO_MANY_FAILURE = (key, advancementSize, targetsSize) -> Component.translatable(key + ".many.to.many.failure", Colors.ERROR, Component.text(advancementSize, Colors.VARIABLE_VALUE), Component.text(targetsSize, Colors.VARIABLE_VALUE));
    public final QuadComponentConsumer<String, String, Component, Component> ADVANCEMENT_CRITERION_TO_ONE_FAILURE = (key, criterion, advancement, player) -> Component.translatable(key + ".criterion.to.one.failure", Colors.ERROR, Component.text(criterion, Colors.VARIABLE_VALUE), advancement, player);
    public final QuadComponentConsumer<String, String, Component, Integer> ADVANCEMENT_CRITERION_TO_MANY_FAILURE = (key, criterion, advancement, size) -> Component.translatable(key + ".criterion.to.many.failure", Colors.ERROR, Component.text(criterion, Colors.VARIABLE_VALUE), advancement, Component.text(size, Colors.VARIABLE_VALUE));


    public final WrapperCommandSyntaxException ERROR_READING_FILE = EssentialsUtil.createException(Component.text("Fehler beim Lesen der Datei", Colors.ERROR));
    public final WrapperCommandSyntaxException ERROR_NOT_HOLDING_WRITTEN_BOOK_IN_HAND = EssentialsUtil.createException(Component.text("Du hältst kein Buch in der Hand!", Colors.ERROR));
    public final WrapperCommandSyntaxException NO_BOOK_BYPASS_PERMISSION = EssentialsUtil.createException(Component.text("Du hast keine Berechtigung, Bücher von anderen Spielern zu bearbeiten!", Colors.ERROR));
    public final WrapperCommandSyntaxException ERROR_NO_INTERNET_ADDRESS = EssentialsUtil.createException(Component.text("Es konnte keine Internet-Adresse gefunden werden!", Colors.ERROR));
    public final WrapperCommandSyntaxException ERROR_NO_SPAWNER_AT_LOCATION = EssentialsUtil.createException(Component.text("Es wurde kein Spawner an der angegebenen Position gefunden!", Colors.ERROR));
    public final WrapperCommandSyntaxException ERROR_NO_BOSS_BARS = EssentialsUtil.createException(Component.text("Es sind keine Bossbars aktiv!", Colors.ERROR));
    public final WrapperCommandSyntaxException ERROR_NOTHING_TO_UNDO = EssentialsUtil.createException(Component.text("Es gibt nichts zum Rückgängigmachen!", Colors.ERROR));
    public final WrapperCommandSyntaxException ERROR_NO_FORCE_LOADED_CHUNKS = EssentialsUtil.createException(Component.text("Es waren keine Chunks dauerhaft geladen!", Colors.ERROR));
    public final WrapperCommandSyntaxException FAILED_TO_WRITE_TO_FILE = EssentialsUtil.createException(Component.text("Fehler beim Schreiben in die Datei", Colors.ERROR));
    public final WrapperCommandSyntaxException NO_SAFE_LOCATION_FOUND = EssentialsUtil.createException(Component.text("Es konnte kein sicherer Ort gefunden werden!", Colors.ERROR));
    public final WrapperCommandSyntaxException ERROR_PLAYER_FILE_NOT_FOUND = EssentialsUtil.createException(Component.text("Die Spielerdatei wurde nicht gefunden!", Colors.ERROR));
    public final WrapperCommandSyntaxException ERROR_WHILE_READING_TAG = EssentialsUtil.createException(Component.text("Fehler beim Lesen des Tags", Colors.ERROR));
    public final WrapperCommandSyntaxException MIN_SPAWN_DELAY_MUST_BE_LESS_THAN_MAX_SPAWN_DELAY = EssentialsUtil.createException(Component.text("minSpawnDelay muss kleiner als oder gleich maxSpawnDelay sein!", Colors.ERROR));
    public final WrapperCommandSyntaxException MAX_SPAWN_DELAY_MUST_BE_GREATER_THAN_MIN_SPAWN_DELAY = EssentialsUtil.createException(Component.text("maxSpawnDelay muss größer als oder gleich minSpawnDelay sein!", Colors.ERROR));


    public final ComponentConsumer<Class<? extends Entity>> ERROR_INVALID_ENTITY = entityClass -> Component.text("Ungültige Entity für diesen Command: %s".formatted(entityClass.getSimpleName()), Colors.ERROR);

    public final ComponentConsumer<Entity> ERROR_HOLDS_NOTHING_IN_HAND = player -> EssentialsUtil.getDisplayName(player)
            .append(Component.text(" hält nichts in der Hand!", Colors.ERROR));

    public final ComponentConsumer<ItemStack> ERROR_CANNOT_STACK_ITEMSTACK = item -> EssentialsUtil.getDisplayName(item)
            .append(Component.text(" kann nicht gestackt werden!", Colors.ERROR));

    public final ComponentConsumer<OfflinePlayer> ERROR_PLAYER_HAS_NOT_DIED_YET = offlinePlayer -> EssentialsUtil.getOfflineDisplayName(offlinePlayer)
            .append(Component.text(" ist noch nie gestorben", Colors.ERROR));

    public final ComponentConsumer<ItemStack> ERROR_NOT_DAMAGEABLE_ITEMSTACK = item -> EssentialsUtil.getDisplayName(item)
            .append(Component.text(" ist nicht reparaturfähig!", Colors.ERROR));

    public final ComponentConsumer<Entity> NO_SPACE_IN_INVENTORY = entity -> EssentialsUtil.getDisplayName(entity)
            .append(Component.text(" hat keinen freien Platz im Inventar!", Colors.ERROR));

    public final ComponentConsumer<String> POLL_NOT_EXISTS = pollName -> Component.text("Die Umfrage ", Colors.ERROR)
            .append(Component.text(pollName, Colors.ERROR))
            .append(Component.text(" existiert nicht!", Colors.ERROR));

    public final ComponentConsumer<World> ERROR_WHILE_UNLOADING_WORLD = world -> Component.text("Fehler beim Entladen der Welt ", Colors.ERROR)
            .append(EssentialsUtil.getDisplayName(world));

    public final ComponentConsumer<World> ERROR_WORLD_ALREADY_LOADED = world -> Component.text("Die Welt '", Colors.ERROR)
            .append(EssentialsUtil.getDisplayName(world))
            .append(Component.text("' ist bereits geladen!", Colors.ERROR));

    public final ComponentConsumer<String> ERROR_WORLD_ALREADY_CREATED = worldName -> Component.text("Die Welt '", Colors.ERROR)
            .append(Component.text(worldName, Colors.VARIABLE_VALUE))
            .append(Component.text("' existiert bereits!", Colors.ERROR));

    public final ComponentConsumer<String> ERROR_FILE_NOT_EXISTS = fileName -> Component.text("Die Datei '", Colors.ERROR)
            .append(Component.text(fileName, Colors.VARIABLE_VALUE))
            .append(Component.text("' existiert nicht!", Colors.ERROR));

    public final ComponentConsumer<String> ERROR_WORLD_TYPE_NOT_EXISTS = worldType -> Component.text("Der Welttyp '", Colors.ERROR)
            .append(Component.text(worldType, Colors.VARIABLE_VALUE))
            .append(Component.text("' existiert nicht!", Colors.ERROR));

    public final ComponentConsumer<String> ERROR_WORLD_ENVIRONMENT_NOT_EXISTS = worldEnvironment -> Component.text("Die Weltumgebung '", Colors.ERROR)
            .append(Component.text(worldEnvironment, Colors.VARIABLE_VALUE))
            .append(Component.text("' existiert nicht!", Colors.ERROR));

    public final ComponentConsumer<BossBar> ERROR_BOSS_BAR_ALREADY_EXISTS = bossBar -> Component.text("Die Bossbar ", Colors.ERROR)
            .append(EssentialsUtil.getDisplayName(bossBar))
            .append(Component.text(" existiert bereits!", Colors.ERROR));

    public final ComponentConsumer<String> ERROR_UNKNOWN_DATA_PACK = dataPackName -> Component.text("Das Datapack ", Colors.ERROR)
            .append(Component.text(dataPackName, Colors.VARIABLE_VALUE))
            .append(Component.text(" existiert nicht!", Colors.ERROR));

    public final ComponentConsumer<Datapack> ERROR_DATA_PACK_ALREADY_ENABLED = datapack -> Component.text("Das Datapack ", Colors.ERROR)
            .append(EssentialsUtil.getDisplayName(datapack))
            .append(Component.text(" ist bereits aktiviert!", Colors.ERROR));

    public final ComponentConsumer<Datapack> ERROR_DATA_PACK_ALREADY_DISABLED = datapack -> Component.text("Das Datapack ", Colors.ERROR)
            .append(EssentialsUtil.getDisplayName(datapack))
            .append(Component.text(" ist bereits deaktiviert!", Colors.ERROR));

    public final ComponentConsumer<Datapack> ERROR_DATA_PACK_INCOMPATIBLE = datapack -> Component.text("Das Datapack ", Colors.ERROR)
            .append(EssentialsUtil.getDisplayName(datapack))
            .append(Component.text(" ist nicht kompatibel!", Colors.ERROR));

    public final ComponentConsumer<String> ERROR_INVALID_DIFFICULTY = difficulty -> Component.text("Die Schwierigkeit ", Colors.ERROR)
            .append(Component.text(difficulty, Colors.VARIABLE_VALUE))
            .append(Component.text(" existiert nicht!", Colors.ERROR));

    public final ComponentConsumer<String> ERROR_UNKNOWN_ENCHANTMENT = enchantment -> ERROR_UNKNOWN_RESOURCE.message("minecraft:enchantment", enchantment);

    public final ComponentConsumer<IOException> FAILED_TO_WRITE_TO_FILE_IO = exception -> Component.text("Fehler beim Schreiben in die Datei", Colors.ERROR)
            .hoverEvent(Component.text(exception.getMessage(), Colors.ERROR));

    public final ComponentConsumer<String> WEATHER_TYPE_NOT_EXISTS = weatherType -> Component.text("Der Wettertyp ", Colors.ERROR)
            .append(Component.text(weatherType, Colors.VARIABLE_VALUE))
            .append(Component.text(" existiert nicht!", Colors.ERROR));

    public final ComponentConsumer<IOException> ERROR_WHILE_READING_TAG_IO = exception -> Component.text("Fehler beim Lesen des Tags", Colors.ERROR)
            .hoverEvent(Component.text(exception.getMessage(), Colors.ERROR));

    public final ComponentConsumer<OfflinePlayer> ERROR_POSITION_IN_UNLOADED_WORLD = player -> Component.text("Die Position von ", Colors.ERROR)
            .append(EssentialsUtil.getOfflineDisplayName(player))
            .append(Component.text(" ist in einer nicht geladenen Welt!", Colors.ERROR));


    public final BiComponentConsumer<File, IOException> ERROR_WHILE_DELETING_FILE = (file, exception) -> Component.text("Fehler beim Löschen der Datei: ", Colors.ERROR)
            .append(Component.text(file.getName(), Colors.VARIABLE_VALUE))
            .hoverEvent(Component.text(exception.getMessage(), Colors.ERROR));


    // Interfaces for creating exceptions with components
    //------------------------------------------------------------------------------------------------------------------

    public interface ComponentConsumer<A> {
        Component message(A a);

        default WrapperCommandSyntaxException create(A a) {
            return EssentialsUtil.createException(message(a).colorIfAbsent(Colors.ERROR));
        }
    }

    public interface BiComponentConsumer<A, B> {
        Component message(A a, B b);

        default WrapperCommandSyntaxException create(A a, B b) {
            return EssentialsUtil.createException(message(a, b).colorIfAbsent(Colors.ERROR));
        }
    }

    public interface TriComponentConsumer<A, B, C> {
        Component message(A a, B b, C c);

        default WrapperCommandSyntaxException create(A a, B b, C c) {
            return EssentialsUtil.createException(message(a, b, c).colorIfAbsent(Colors.ERROR));
        }
    }

    public interface QuadComponentConsumer<A, B, C, D> {
        Component message(A a, B b, C c, D d);

        default WrapperCommandSyntaxException create(A a, B b, C c, D d) {
            return EssentialsUtil.createException(message(a, b, c, d).colorIfAbsent(Colors.ERROR));
        }
    }

    public interface PentaComponentConsumer<A, B, C, D, E> {
        Component message(A a, B b, C c, D d, E e);

        default WrapperCommandSyntaxException create(A a, B b, C c, D d, E e) {
            return EssentialsUtil.createException(message(a, b, c, d, e).colorIfAbsent(Colors.ERROR));
        }
    }

    public interface HexaComponentConsumer<A, B, C, D, E, F> {
        Component message(A a, B b, C c, D d, E e, F f);

        default WrapperCommandSyntaxException create(A a, B b, C c, D d, E e, F f) {
            return EssentialsUtil.createException(message(a, b, c, d, e, f).colorIfAbsent(Colors.ERROR));
        }
    }

    public interface HeptaComponentConsumer<A, B, C, D, E, F, G> {
        Component message(A a, B b, C c, D d, E e, F f, G g);

        default WrapperCommandSyntaxException create(A a, B b, C c, D d, E e, F f, G g) {
            return EssentialsUtil.createException(message(a, b, c, d, e, f, g).colorIfAbsent(Colors.ERROR));
        }
    }

    public interface OctaComponentConsumer<A, B, C, D, E, F, G, H> {
        Component message(A a, B b, C c, D d, E e, F f, G g, H h);

        default WrapperCommandSyntaxException create(A a, B b, C c, D d, E e, F f, G g, H h) {
            return EssentialsUtil.createException(message(a, b, c, d, e, f, g, h).colorIfAbsent(Colors.ERROR));
        }
    }

    public interface NonaComponentConsumer<A, B, C, D, E, F, G, H, I> {
        Component message(A a, B b, C c, D d, E e, F f, G g, H h, I i);

        default WrapperCommandSyntaxException create(A a, B b, C c, D d, E e, F f, G g, H h, I i) {
            return EssentialsUtil.createException(message(a, b, c, d, e, f, g, h, i).colorIfAbsent(Colors.ERROR));
        }
    }

    public interface DecaComponentConsumer<A, B, C, D, E, F, G, H, I, J> {
        Component message(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j);

        default WrapperCommandSyntaxException create(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j) {
            return EssentialsUtil.createException(message(a, b, c, d, e, f, g, h, i, j).colorIfAbsent(Colors.ERROR));
        }
    }
}
