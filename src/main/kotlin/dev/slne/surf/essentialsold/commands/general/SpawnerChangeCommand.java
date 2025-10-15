package dev.slne.surf.essentialsold.commands.general;

import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.jorel.commandapi.executors.ResultingCommandExecutor;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.brigadier.Exceptions;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SpawnerChangeCommand extends EssentialsCommand {
    public SpawnerChangeCommand() {
        super("spawnerchange", "spawnerchange <entity>", "Change the entity of a spawner", "spawner");

        withPermission(Permissions.SPAWNER_PERMISSION);

        executesNative((NativeResultingCommandExecutor) (sender, args) -> giveSpawner(getSpecialEntityOrException(sender, HumanEntity.class)));
        then(locationArgument("pos", LocationType.BLOCK_POSITION)
                .executes((ResultingCommandExecutor) (sender, args) -> querySpawner(sender, getLocation(args, "pos")))
                .then(entityTypeArgument("entity")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> modifySpawner(
                                sender.getCallee(),
                                getLocation(args, "pos"),
                                Optional.of(getEntityType(args, "entity")),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty())
                        )
                        .then(integerArgument("minSpawnDelay", 1)
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> modifySpawner(
                                        sender.getCallee(),
                                        getLocation(args, "pos"),
                                        Optional.of(getEntityType(args, "entity")),
                                        Optional.of(getInteger(args, "minSpawnDelay")),
                                        Optional.empty(),
                                        Optional.empty(),
                                        Optional.empty())
                                )
                                .then(integerArgument("maxSpawnDelay", 1)
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> modifySpawner(
                                                sender.getCallee(),
                                                getLocation(args, "pos"),
                                                Optional.of(getEntityType(args, "entity")),
                                                Optional.of(getInteger(args, "minSpawnDelay")),
                                                Optional.of(getInteger(args, "maxSpawnDelay")),
                                                Optional.empty(),
                                                Optional.empty())
                                        )
                                        .then(integerArgument("spawnRange", 1)
                                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> modifySpawner(
                                                        sender.getCallee(),
                                                        getLocation(args, "pos"),
                                                        Optional.of(getEntityType(args, "entity")),
                                                        Optional.of(getInteger(args, "minSpawnDelay")),
                                                        Optional.of(getInteger(args, "maxSpawnDelay")),
                                                        Optional.of(getInteger(args, "spawnRange")),
                                                        Optional.empty())
                                                )
                                                .then(integerArgument("requiredPlayerRange", 1)
                                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> modifySpawner(
                                                                sender.getCallee(),
                                                                getLocation(args, "pos"),
                                                                Optional.of(getEntityType(args, "entity")),
                                                                Optional.of(getInteger(args, "minSpawnDelay")),
                                                                Optional.of(getInteger(args, "maxSpawnDelay")),
                                                                Optional.of(getInteger(args, "spawnRange")),
                                                                Optional.of(getInteger(args, "requiredPlayerRange"))
                                                        ))
                                                )
                                        )
                                )
                        )
                )
        );
    }


    private static int giveSpawner(HumanEntity source) throws WrapperCommandSyntaxException {
        if (!source.getInventory().addItem(new ItemStack(Material.SPAWNER)).isEmpty())
            throw Exceptions.NO_SPACE_IN_INVENTORY.create(source);

        EssentialsUtil.sendSuccess(source, "Dir wurde ein spawner gegeben!");
        return 1;
    }

    private static int querySpawner(CommandSender sender, Location location) throws WrapperCommandSyntaxException {
        if (!(location.getBlock().getState(true) instanceof CreatureSpawner spawner)) throw Exceptions.ERROR_NO_SPAWNER_AT_LOCATION;


        val entityType = spawner.getSpawnedType();
        val entityName = (entityType != null) ? entityType.getKey().asString() : "__unknown__";
        val minSpawnDelay = spawner.getMinSpawnDelay();
        val maxSpawnDelay = spawner.getMaxSpawnDelay();
        val spawnRange = spawner.getSpawnRange();
        val requiredPlayerRange = spawner.getRequiredPlayerRange();

        EssentialsUtil.sendSuccess(sender, Component.text("Spawner", Colors.TERTIARY)
                .hoverEvent(HoverEvent.showText(Component.text("Entity: ", Colors.INFO)
                        .append(Component.text(entityName, Colors.TERTIARY))
                        .append(Component.newline())
                        .append(Component.text("minSpawnDelay: ", Colors.INFO)
                                .append(Component.text(minSpawnDelay, Colors.TERTIARY)))
                        .append(Component.newline())
                        .append(Component.text("maxSpawnDelay: ", Colors.INFO)
                                .append(Component.text(maxSpawnDelay, Colors.TERTIARY)))
                        .append(Component.newline())
                        .append(Component.text("spawnRange: ", Colors.INFO)
                                .append(Component.text(spawnRange, Colors.TERTIARY)))
                        .append(Component.newline())
                        .append(Component.text("requiredPlayerRange: ", Colors.INFO)
                                .append(Component.text(requiredPlayerRange, Colors.TERTIARY)))))
                .append(Component.text(" bei ", Colors.INFO)
                        .append(EssentialsUtil.formatLocationWithoutSpacer(location))));
        return 1;

    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType"})
    private static int modifySpawner(
            CommandSender source,
            Location location,
            Optional<EntityType> optionalEntityType,
            Optional<Integer> optionalMinSpawnDelay,
            Optional<Integer> optionalMaxSpawnDelay,
            Optional<Integer> optionalSpawnRange,
            Optional<Integer> optionalRequiredPlayerRange
    ) throws WrapperCommandSyntaxException {
        if (!(location.getBlock().getState(true) instanceof CreatureSpawner spawner)) throw Exceptions.ERROR_NO_SPAWNER_AT_LOCATION;

        val type = optionalEntityType.orElse(spawner.getSpawnedType());
        final int minSpawnDelay = optionalMinSpawnDelay.orElse(spawner.getMinSpawnDelay());
        final int maxSpawnDelay = optionalMaxSpawnDelay.orElse(spawner.getMaxSpawnDelay());
        final int spawnRange = optionalSpawnRange.orElse(spawner.getSpawnRange());
        final int requiredPlayerRange = optionalRequiredPlayerRange.orElse(spawner.getRequiredPlayerRange());


        if (minSpawnDelay < maxSpawnDelay) throw Exceptions.MIN_SPAWN_DELAY_MUST_BE_LESS_THAN_MAX_SPAWN_DELAY;
        if (minSpawnDelay > maxSpawnDelay) throw Exceptions.MAX_SPAWN_DELAY_MUST_BE_GREATER_THAN_MIN_SPAWN_DELAY;

        spawner.setSpawnedType(type);
        spawner.setMinSpawnDelay(minSpawnDelay);
        spawner.setMaxSpawnDelay(maxSpawnDelay);
        spawner.setSpawnRange(spawnRange);
        spawner.setRequiredPlayerRange(requiredPlayerRange);
        spawner.update(true, true);

        EssentialsUtil.sendSuccess(source, Component.text("Der ", Colors.SUCCESS)
                .append(Component.text("Spawner", Colors.TERTIARY)
                        .hoverEvent(HoverEvent.showText(Component.text("Entity: ", Colors.INFO)
                                .append(Component.text((type != null) ? type.getKey().asString() : "__unknown__", Colors.TERTIARY))
                                .append(Component.newline())
                                .append(Component.text("minSpawnDelay: ", Colors.INFO)
                                        .append(Component.text(minSpawnDelay, Colors.TERTIARY)))
                                .append(Component.newline())
                                .append(Component.text("maxSpawnDelay: ", Colors.INFO)
                                        .append(Component.text(maxSpawnDelay, Colors.TERTIARY)))
                                .append(Component.newline())
                                .append(Component.text("spawnRange: ", Colors.INFO)
                                        .append(Component.text(spawnRange, Colors.TERTIARY)))
                                .append(Component.newline())
                                .append(Component.text("requiredPlayerRange: ", Colors.INFO)
                                        .append(Component.text(requiredPlayerRange, Colors.TERTIARY))))))
                .append(Component.text(" wurde erfolgreich geändert!")));
        return 1;
    }

    protected Location getLocation(@NotNull CommandArguments args, String nodeName) {
        val loc = args.get(nodeName);

        if (!(loc instanceof Location location)) {
            throw new IllegalArgumentException("Location argument '%s' is not a location".formatted(nodeName));
        }

        return location;
    }

    protected EntityType getEntityType(@NotNull CommandArguments args, String nodeName) {
        val type = args.get(nodeName);

        if (!(type instanceof EntityType entityType)) {
            throw new IllegalArgumentException("Entity type argument '%s' is not an entity type".formatted(nodeName));
        }

        return entityType;
    }

    protected int getInteger(@NotNull CommandArguments args, String nodeName) {
        val integer = args.get(nodeName);

        if (!(integer instanceof Integer i)) {
            throw new IllegalArgumentException("Integer argument '%s' is not an integer".formatted(nodeName));
        }

        return i;
    }
}
