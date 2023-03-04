package dev.slne.surf.essentials.commands.general.other.world;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.gui.GuiUtils;
import dev.slne.surf.essentials.utils.permission.Permissions;
import dev.slne.surf.essentials.utils.brigadier.BrigadierCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class WorldCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"world"};
    }

    @Override
    public String usage() {
        return "/world <create | join | unload | load | remove | gui>";
    }

    @Override
    public String description() {
        return "Join, create, delete and unload worlds";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.WORLD_CREATE_PERMISSION) || sourceStack.hasPermission(2, Permissions.WORLD_CHANGE_PERMISSION) ||
                sourceStack.hasPermission(2, Permissions.WORLD_UNLOAD_PERMISSION) || sourceStack.hasPermission(2, Permissions.WORLD_REMOVE_PERMISSION) ||
                sourceStack.hasPermission(2, Permissions.WORLD_GUI_PERMISSION) || sourceStack.hasPermission(2, Permissions.WORLD_LOAD_PERMISSION) || sourceStack.hasPermission(2, Permissions.WORLD_QUERY_PERMISSION));

        literal.executes(context -> query(context.getSource()));

        literal.then(Commands.literal("join")
                        .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.WORLD_CHANGE_PERMISSION))
                .then(Commands.argument("world", DimensionArgument.dimension())
                        .suggests(this::worldSuggestions)
                        .executes(context -> join(context.getSource(), DimensionArgument.getDimension(context, "world"), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> join(context.getSource(), DimensionArgument.getDimension(context, "world"), EntityArgument.getPlayer(context, "player"))))));

        literal.then(Commands.literal("unload")
                        .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.WORLD_UNLOAD_PERMISSION))
                .then(Commands.argument("world", DimensionArgument.dimension())
                        .suggests(this::worldSuggestions)
                        .executes(context -> unload(context.getSource(), DimensionArgument.getDimension(context, "world")))));

        literal.then(Commands.literal("remove")
                        .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.WORLD_REMOVE_PERMISSION))
                .then(Commands.argument("world", StringArgumentType.greedyString())
                        .suggests(this::offlineWorldSuggestions)
                        .executes(context -> remove(context.getSource(), StringArgumentType.getString(context, "world")))));

        literal.then(Commands.literal("load")
                        .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.WORLD_LOAD_PERMISSION))
                .then(Commands.argument("world", StringArgumentType.greedyString())
                        .suggests(this::offlineWorldSuggestions)
                        .executes(context -> load(context.getSource(), StringArgumentType.getString(context, "world")))));

        literal.then(Commands.literal("gui")
                        .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.WORLD_GUI_PERMISSION))
                .executes(context -> gui(context.getSource())));

        for (World.Environment environment : World.Environment.values()) {
            if (environment == World.Environment.CUSTOM) continue;
            for (WorldType worldType : WorldType.values()) {
                literal.then(Commands.literal("create")
                                .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.WORLD_CREATE_PERMISSION))

                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> create(context.getSource(), StringArgumentType.getString(context, "name"),
                                        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()))

                                .then(Commands.literal(environment.toString().toLowerCase())
                                        .executes(context -> create(context.getSource(), StringArgumentType.getString(context, "name"),
                                                Optional.of(environment), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()))

                                        .then(Commands.literal(worldType.toString().toLowerCase())
                                                .executes(context -> create(context.getSource(), StringArgumentType.getString(context, "name"),
                                                        Optional.of(environment), Optional.of(worldType), Optional.empty(), Optional.empty(), Optional.empty()))

                                                .then(Commands.argument("generateStructures", BoolArgumentType.bool())
                                                        .executes(context -> create(context.getSource(), StringArgumentType.getString(context, "name"),
                                                                Optional.of(environment), Optional.of(worldType), Optional.of(BoolArgumentType.getBool(context, "generateStructures")),
                                                                Optional.empty(), Optional.empty()))

                                                        .then(Commands.argument("hardcore", BoolArgumentType.bool())
                                                                .executes(context -> create(context.getSource(), StringArgumentType.getString(context, "name"),
                                                                        Optional.of(environment), Optional.of(worldType), Optional.of(BoolArgumentType.getBool(context, "generateStructures")),
                                                                        Optional.of(BoolArgumentType.getBool(context, "hardcore")), Optional.empty()))

                                                                .then(Commands.argument("seed", LongArgumentType.longArg())
                                                                        .executes(context -> create(context.getSource(), StringArgumentType.getString(context, "name"),
                                                                                Optional.of(environment), Optional.of(worldType), Optional.of(BoolArgumentType.getBool(context, "generateStructures")),
                                                                                Optional.of(BoolArgumentType.getBool(context, "hardcore")), Optional.of(LongArgumentType.getLong(context, "seed")))))))))));
            }
        }
    }



    private int query(CommandSourceStack source) throws CommandSyntaxException{
        EssentialsUtil.sendSuccess(source, Component.text("Du befindest dich in der Welt ", SurfColors.SUCCESS)
                .append(Component.text(source.getLevel().dimension().location().toString(), SurfColors.TERTIARY))
                .append(Component.text(".", SurfColors.SUCCESS)));
        return 1;
    }

    private int join(CommandSourceStack source, ServerLevel level, ServerPlayer targetUnchecked) throws CommandSyntaxException{
        ServerPlayer target = EssentialsUtil.checkSinglePlayerSuggestion(source, targetUnchecked);
        BlockPos spawn = level.getSharedSpawnPos();

        target.teleportTo(level, spawn.getX(), spawn.getY(), spawn.getZ(), level.getSharedSpawnAngle(), 0.0F);

        if (source.isPlayer()) {
            EssentialsUtil.sendSuccess(source, target.adventure$displayName.colorIfAbsent(SurfColors.TERTIARY)
                    .append(Component.text(" hat die Welt ", SurfColors.SUCCESS)
                            .append(Component.text(level.dimension().location().toString(), SurfColors.TERTIARY))
                            .append(Component.text(" betreten.", SurfColors.SUCCESS))));
        }else {
            source.sendSuccess(target.getDisplayName()
                    .copy().append(net.minecraft.network.chat.Component.literal(" has entered ")
                            .withStyle(ChatFormatting.GREEN))
                    .append(net.minecraft.network.chat.Component.literal(level.dimension().location().toString())
                            .withStyle(ChatFormatting.GOLD)), false);
        }
        return 1;
    }

    private int unload(CommandSourceStack source, ServerLevel level) throws CommandSyntaxException{
        ServerLevel overworld = source.getServer().overworld();
        BlockPos overworldSpawn = overworld.getSharedSpawnPos();

        if (level.dimension() == Level.OVERWORLD) throw ERROR_CANNOT_UNLOAD_WORLD.create(level.dimension().location().toString());

        if (source.isPlayer()){
            EssentialsUtil.sendInfo(source, "Teleportiere Spieler in overworld...");
        }

        level.getPlayers(player -> {
            player.teleportTo(overworld, overworldSpawn.getX(), overworldSpawn.getY(), overworldSpawn.getZ(), overworld.getSharedSpawnAngle(), 0.0F);
            return true;
        });

        if (source.isPlayer()){
            EssentialsUtil.sendInfo(source, "Entlade Welt...");
        }

        Bukkit.unloadWorld(level.getWorld(), true);

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, Component.text("Die Welt ", SurfColors.SUCCESS)
                    .append(Component.text(level.dimension().location().toString(), SurfColors.TERTIARY))
                    .append(Component.text(" wurde erfolgreich entladen.", SurfColors.SUCCESS)));
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("The world '" + level.dimension().location() + "' was successfully unloaded")
                    .withStyle(ChatFormatting.GREEN), false);
        }
        return 1;

    }

    private int remove(CommandSourceStack source, String levelName) throws CommandSyntaxException{
        World world = Bukkit.getWorld(levelName);
        ServerLevel overworld = source.getServer().overworld();
        File file = new File(Bukkit.getServer().getWorldContainer(), levelName);

        if (!file.exists()) throw ERROR_FILE_NOT_EXISTS.create(file.getName());

        if (world != null){
            if (world == overworld.getWorld() || ((CraftWorld) world).getHandle().dimension() == Level.NETHER || ((CraftWorld) world).getHandle().dimension() == Level.END){
                throw ERROR_CANNOT_UNLOAD_WORLD.create(((CraftWorld) world).getHandle().dimension().location().toString());
            }
            if (source.isPlayer()){
                EssentialsUtil.sendInfo(source, "Teleportiere Spieler...");
            }
            world.getPlayers().forEach(player -> player.teleport(overworld.getWorld().getSpawnLocation()));
            if (source.isPlayer()){
                EssentialsUtil.sendInfo(source, "Entlade Welt...");
            }
            Bukkit.unloadWorld(world, false);
        }

        if (source.isPlayer()){
            EssentialsUtil.sendInfo(source, "Lösche Dateien");
        }

        Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), bukkitTask -> {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, "Die Welt wurde erfolgreich gelöscht.");
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("The world was successfully deleted.")
                    .withStyle(ChatFormatting.GREEN), false);
        }
        return 1;
    }

    private int load(CommandSourceStack source, String worldName) throws CommandSyntaxException{
        File file = new File(Bukkit.getWorldContainer(), worldName);

        if (!file.exists()) throw ERROR_INVALID_VALUE.create(file.getName());

        World world = Bukkit.getWorld(file.getName());

        if (world != null) throw ERROR_WORLD_ALREADY_LOADED.create(((CraftWorld) world).getHandle().dimension().location().toString());

        if (source.isPlayer()){
            EssentialsUtil.sendInfo(source, "Lade Welt...");
        }

        Bukkit.createWorld(WorldCreator.name(file.getName()));

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, Component.text("Die Welt ", SurfColors.SUCCESS)
                    .append(Component.text(file.getName(), SurfColors.TERTIARY))
                    .append(Component.text(" wurde erfolgreich geladen.", SurfColors.SUCCESS)));
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("Loaded world '" + file.getName() + "'")
                    .withStyle(ChatFormatting.GREEN), false);
        }
        return 1;
    }

    private int gui(CommandSourceStack source) throws CommandSyntaxException{
        Player player = source.getPlayerOrException().getBukkitEntity();
        ChestGui gui = new ChestGui(6, ComponentHolder.of(Component.text("World GUI", SurfColors.SECONDARY)));

        gui.setOnGlobalClick(event -> event.setCancelled(true));
        GuiUtils.setAllBoarders(gui);

        StaticPane worldActionSelect = new StaticPane(1, 1,7,4);

        worldActionSelect.addItem(WorldItems.WORLD_JOIN(), 3,1);
        worldActionSelect.addItem(WorldItems.WORLD_LOAD(), 0,0);
        worldActionSelect.addItem(WorldItems.WORLD_UNLOAD(),6,0);
        worldActionSelect.addItem(WorldItems.WORLD_REMOVE(), 6,3);

        gui.addPane(worldActionSelect);

        gui.show(player);

        return 1;
    }

    private int create(CommandSourceStack source, String worldName, Optional<World.Environment> environment, Optional<WorldType> worldType,
                       Optional<Boolean> generateStructures, Optional<Boolean> hardcore, Optional<Long> seed) throws CommandSyntaxException{

        for (World world : Bukkit.getWorlds()) {
            if (world.getName().equals(worldName)) throw ERROR_WORLD_ALREADY_CREATED.create(world.getName());
        }

        WorldCreator worldCreator = new WorldCreator(worldName);

        worldCreator.environment(environment.orElse(World.Environment.NORMAL));
        worldCreator.type(worldType.orElse(WorldType.NORMAL));
        worldCreator.generateStructures(generateStructures.orElse(true));
        worldCreator.seed(seed.orElse(new Random().nextLong()));
        worldCreator.hardcore(hardcore.orElse(false));
        worldCreator.keepSpawnLoaded(TriState.TRUE);

        if (source.isPlayer()){
            EssentialsUtil.sendInfo(source, "Erstelle Welt...");
        }

        worldCreator.createWorld();

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, Component.text("Die Welt ", SurfColors.SUCCESS)
                    .append(Component.text(worldName, SurfColors.TERTIARY))
                    .append(Component.text(" wurde erfolgreich erstellt.", SurfColors.SUCCESS)));
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("Created world '" + worldName + "'")
                    .withStyle(ChatFormatting.GREEN), false);
        }
        return 1;
    }




    private CompletableFuture<Suggestions> worldSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        for (ServerLevel level : context.getSource().getServer().getAllLevels()) {
            builder.suggest(level.dimension().location().toString());
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> offlineWorldSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder){
        for (File file : Objects.requireNonNull(SurfEssentials.getInstance().getServer().getWorldContainer().listFiles())) {
            if (!file.isDirectory()) continue;
            if (!Arrays.asList(Objects.requireNonNull(file.list())).contains("level.dat") || !Arrays.asList(Objects.requireNonNull(file.list())).contains("paper-world.yml")) continue;
            builder.suggest(file.getName());
        }
        return builder.buildFuture();
    }

    private static final DynamicCommandExceptionType ERROR_CANNOT_UNLOAD_WORLD = new DynamicCommandExceptionType(worldName ->
            net.minecraft.network.chat.Component.literal("Cannot unload world '" + worldName + "'")
            .withStyle(ChatFormatting.RED));

    private static final DynamicCommandExceptionType ERROR_FILE_NOT_EXISTS = new DynamicCommandExceptionType(fileName ->
            net.minecraft.network.chat.Component.literal("The file '" + fileName + "' doesn´t exist")
                    .withStyle(ChatFormatting.RED));

    DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType(id ->
            net.minecraft.network.chat.Component.translatable("argument.dimension.invalid", id));

    DynamicCommandExceptionType ERROR_WORLD_ALREADY_LOADED = new DynamicCommandExceptionType(worldName ->
            net.minecraft.network.chat.Component.literal("The world '" + worldName + "' is already loaded")
                    .withStyle(ChatFormatting.RED));

    DynamicCommandExceptionType ERROR_WORLD_ALREADY_CREATED = new DynamicCommandExceptionType(worldName ->
            net.minecraft.network.chat.Component.literal("The world '" + worldName + "' already exists")
                    .withStyle(ChatFormatting.RED));
}
