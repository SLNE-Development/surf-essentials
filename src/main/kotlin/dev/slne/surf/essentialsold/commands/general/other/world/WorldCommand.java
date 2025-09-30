package dev.slne.surf.essentialsold.commands.general.other.world;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import dev.slne.surf.essentialsold.SurfEssentials;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.brigadier.Exceptions;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.gui.GuiUtils;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WorldCommand extends EssentialsCommand {
    private static final List<String> CUSTOM_LOADED_WORLDS = new ArrayList<>();

    public WorldCommand() {
        super("world", "world <create | join | unload | load | remove | gui>", "Join, create, delete and unload worlds.");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.WORLD_CREATE_PERMISSION, Permissions.WORLD_CHANGE_PERMISSION,
                Permissions.WORLD_UNLOAD_PERMISSION, Permissions.WORLD_LOAD_PERMISSION, Permissions.WORLD_REMOVE_PERMISSION,
                Permissions.WORLD_GUI_PERMISSION, Permissions.WORLD_QUERY_PERMISSION));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> query(sender));

        then(literal("join")
                .withPermission(Permissions.WORLD_CHANGE_PERMISSION)
                .then(worldArgument("world")
                                .includeSuggestions(worldSuggestions())
                        .replaceSuggestions(worldSuggestions())
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> join(sender.getCallee(), Objects.requireNonNull(args.getUnchecked("world")), getEntityOrException(sender)))
                        .then(entityArgument("entity")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> join(sender.getCallee(), Objects.requireNonNull(args.getUnchecked("world")), args.getUnchecked("entity"))))));

        then(literal("unload")
                .withPermission(Permissions.WORLD_UNLOAD_PERMISSION)
                .then(worldArgument("world")
                        .replaceSuggestions(worldSuggestions())
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> unload(sender.getCallee(), Objects.requireNonNull(args.getUnchecked("world"))))));

        then(literal("load")
                .withPermission(Permissions.WORLD_LOAD_PERMISSION)
                .then(wordArgument("world")
                        .replaceSuggestions(offlineWorldSuggestions())
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> load(sender.getCallee(), args.getUnchecked("world")))));

        then(literal("remove")
                .withPermission(Permissions.WORLD_REMOVE_PERMISSION)
                .then(wordArgument("world")
                        .replaceSuggestions(allWorldsSuggestions())
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> remove(sender.getCallee(), args.getUnchecked("world")))));

        then(literal("gui")
                .withPermission(Permissions.WORLD_GUI_PERMISSION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> gui(getSpecialEntityOrException(sender, HumanEntity.class))));

        then(literal("create")
                .withPermission(Permissions.WORLD_CREATE_PERMISSION)
                .then(wordArgument("name")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> create(
                                sender.getCallee(),
                                args.getUnchecked("name"),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty()
                        ))
                        .then(worldEnvironmentArgument("environment")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> create(
                                        sender.getCallee(),
                                        args.getUnchecked("name"),
                                        Optional.ofNullable(args.getUnchecked("environment")),
                                        Optional.empty(),
                                        Optional.empty(),
                                        Optional.empty(),
                                        Optional.empty()
                                ))
                                .then(worldTypeArgument("type")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> create(
                                                sender.getCallee(),
                                                args.getUnchecked("name"),
                                                Optional.ofNullable(args.getUnchecked("environment")),
                                                Optional.ofNullable(args.getUnchecked("type")),
                                                Optional.empty(),
                                                Optional.empty(),
                                                Optional.empty()
                                        ))
                                        .then(booleanArgument("generateStructures")
                                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> create(
                                                        sender.getCallee(),
                                                        args.getUnchecked("name"),
                                                        Optional.ofNullable(args.getUnchecked("environment")),
                                                        Optional.ofNullable(args.getUnchecked("type")),
                                                        Optional.ofNullable(args.getUnchecked("generateStructures")),
                                                        Optional.empty(),
                                                        Optional.empty()
                                                ))
                                                .then(booleanArgument("hardcore")
                                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> create(
                                                                sender.getCallee(),
                                                                args.getUnchecked("name"),
                                                                Optional.ofNullable(args.getUnchecked("environment")),
                                                                Optional.ofNullable(args.getUnchecked("type")),
                                                                Optional.ofNullable(args.getUnchecked("generateStructures")),
                                                                Optional.ofNullable(args.getUnchecked("hardcore")),
                                                                Optional.empty()
                                                        ))
                                                        .then(longArgument("seed")
                                                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> create(
                                                                        sender.getCallee(),
                                                                        args.getUnchecked("name"),
                                                                        Optional.ofNullable(args.getUnchecked("environment")),
                                                                        Optional.ofNullable(args.getUnchecked("type")),
                                                                        Optional.ofNullable(args.getUnchecked("generateStructures")),
                                                                        Optional.ofNullable(args.getUnchecked("hardcore")),
                                                                        Optional.ofNullable(args.getUnchecked("seed"))
                                                                ))
                                                        )
                                                        .then(greedyStringArgument("stringSeed")
                                                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> create(
                                                                        sender.getCallee(),
                                                                        args.getUnchecked("name"),
                                                                        Optional.ofNullable(args.getUnchecked("environment")),
                                                                        Optional.ofNullable(args.getUnchecked("type")),
                                                                        Optional.ofNullable(args.getUnchecked("generateStructures")),
                                                                        Optional.ofNullable(args.getUnchecked("hardcore")),
                                                                        Optional.of(EssentialsUtil.convertStringToSeed(Objects.requireNonNull(args.getUnchecked("stringSeed"))))
                                                                ))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }


    private int query(NativeProxyCommandSender source) {
        EssentialsUtil.sendSuccess(source, Component.text("Du befindest dich in der Welt ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(source.getWorld()))
                .append(Component.text(".", Colors.SUCCESS)));
        return 1;
    }

    private int join(CommandSender source, World world, Entity targetUnchecked) throws WrapperCommandSyntaxException {
        val target = EssentialsUtil.checkEntitySuggestion(source, targetUnchecked);
        val spawn = world.getSpawnLocation();

        target.teleportAsync(spawn);

        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(target)
                .append(Component.text(" hat die Welt ", Colors.SUCCESS)
                        .append(EssentialsUtil.getDisplayName(world))
                        .append(Component.text(" betreten.", Colors.SUCCESS))));

        return 1;
    }

    private int unload(CommandSender source, World world) throws WrapperCommandSyntaxException {
        val overworld = Bukkit.getWorlds().get(0);
        val overworldSpawn = overworld.getSpawnLocation();

        EssentialsUtil.sendInfo(source, "Teleportiere Spieler in overworld...");

        for (Player player : world.getPlayers()) {
            player.teleport(overworldSpawn);
        }

        EssentialsUtil.sendInfo(source, "Entlade Welt...");
        if (!Bukkit.unloadWorld(world, true)) throw Exceptions.ERROR_WHILE_UNLOADING_WORLD.create(world);

        EssentialsUtil.sendSuccess(source, Component.text("Die Welt ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(world))
                .append(Component.text(" wurde erfolgreich entladen.", Colors.SUCCESS)));

        return 1;
    }

    private int remove(CommandSender source, String levelName) throws WrapperCommandSyntaxException {
        val world = Bukkit.getWorld(levelName);
        val file = new File(Bukkit.getServer().getWorldContainer(), levelName);

        if (!worlds().contains(levelName)) throw Exceptions.ERROR_FILE_NOT_EXISTS.create(levelName);

        if (world != null) {
            EssentialsUtil.sendInfo(source, "Teleportiere Spieler...");
            world.getPlayers().forEach(player -> player.teleport(world.getSpawnLocation()));

            EssentialsUtil.sendInfo(source, "Entlade Welt...");
            if (!Bukkit.unloadWorld(world, false)) throw Exceptions.ERROR_WHILE_UNLOADING_WORLD.create(world);
        }

        EssentialsUtil.sendInfo(source, "Lösche Dateien...");
        Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), __ -> {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        EssentialsUtil.sendSuccess(source, "Die Welt wurde erfolgreich gelöscht.");

        return 1;
    }

    private int load(CommandSender source, String worldName) throws WrapperCommandSyntaxException {
        val file = new File(Bukkit.getWorldContainer(), worldName);
        if (!file.exists()) throw Exceptions.ERROR_INVALID_DIMENSION.create(file.getName());

        val world = Bukkit.getWorld(file.getName());
        if (world != null) throw Exceptions.ERROR_WORLD_ALREADY_LOADED.create(world);

        EssentialsUtil.sendInfo(source, "Lade Welt...");
        Bukkit.createWorld(WorldCreator.name(file.getName()));

        EssentialsUtil.sendSuccess(source, Component.text("Die Welt ", Colors.SUCCESS)
                .append(Component.text(file.getName(), Colors.TERTIARY))
                .append(Component.text(" wurde erfolgreich geladen.", Colors.SUCCESS)));
        return 1;
    }

    private int gui(HumanEntity source) {
        val gui = new ChestGui(6, ComponentHolder.of(Component.text("World GUI", Colors.SECONDARY)));

        gui.setOnGlobalClick(event -> event.setCancelled(true));
        GuiUtils.setAllBoarders(gui);

        val worldActionSelect = new StaticPane(1, 1, 7, 4);

        worldActionSelect.addItem(WorldItems.WORLD_JOIN(), 3, 1);
        worldActionSelect.addItem(WorldItems.WORLD_LOAD(), 0, 0);
        worldActionSelect.addItem(WorldItems.WORLD_UNLOAD(), 6, 0);
        worldActionSelect.addItem(WorldItems.WORLD_REMOVE(), 6, 3);

        gui.addPane(worldActionSelect);

        gui.show(source);

        return 1;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private int create(CommandSender source, String worldName, Optional<World.Environment> environment, Optional<WorldType> worldType,
                       Optional<Boolean> generateStructures, Optional<Boolean> hardcore, Optional<Long> seed) throws WrapperCommandSyntaxException {

        for (String world : worlds()) {
            if (world.equals(worldName)) throw Exceptions.ERROR_WORLD_ALREADY_CREATED.create(worldName);
        }

        val worldCreator = new WorldCreator(worldName);

        environment.ifPresent(worldCreator::environment);
        worldType.ifPresent(worldCreator::type);
        generateStructures.ifPresent(worldCreator::generateStructures);
        seed.ifPresent(worldCreator::seed);
        hardcore.ifPresent(worldCreator::hardcore);
        worldCreator.keepSpawnLoaded(TriState.TRUE);

        EssentialsUtil.sendInfo(source, "Erstelle Welt...");
        val world = worldCreator.createWorld();

        EssentialsUtil.sendSuccess(source, Component.text("Die Welt ", Colors.SUCCESS)
                .append(Component.text(world != null ? world.getKey().asString() : worldName, Colors.TERTIARY))
                .append(Component.text(" wurde erfolgreich erstellt.", Colors.SUCCESS)));

        return 1;
    }

    private ArgumentSuggestions<CommandSender> worldSuggestions() {
        return ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.supplyAsync(() -> Bukkit.getWorlds().stream().map(world -> world.getKey().asString()).toList()));
    }

    private ArgumentSuggestions<CommandSender> offlineWorldSuggestions() {
        return ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.supplyAsync(() -> worlds().stream().filter(world -> Bukkit.getWorld(world) == null).toList()));
    }

    private ArgumentSuggestions<CommandSender> allWorldsSuggestions() {
        return ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.supplyAsync(this::worlds));
    }

    private @NotNull List<String> worlds() {
        val world = new ArrayList<String>();
        for (File file : Objects.requireNonNull(SurfEssentials.getInstance().getServer().getWorldContainer().listFiles())) {
            if (!file.isDirectory()) continue;
            if (!Arrays.asList(Objects.requireNonNull(file.list())).contains("level.dat") || !Arrays.asList(Objects.requireNonNull(file.list())).contains("paper-world.yml"))
                continue;
            world.add(file.getName());
        }
        return world;
    }
}
