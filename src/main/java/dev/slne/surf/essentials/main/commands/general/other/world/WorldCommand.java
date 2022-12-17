package dev.slne.surf.essentials.main.commands.general.other.world;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.commands.EssentialsCommand;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static dev.slne.surf.essentials.main.utils.EssentialsUtil.sortedSuggestions;

public class WorldCommand extends EssentialsCommand implements Listener {
    public WorldCommand(PluginCommand command) {
        super(command);
        command.setPermission("surf.essentials.command.world.create");
        command.permissionMessage(EssentialsUtil.NO_PERMISSION());
        command.setUsage("/world <name> <environment> [<type>] [<generateStructures>]");
        command.setDescription("Create Worlds in game");
        command.setAliases(List.of("newworld", "addworld", "cworld"));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            //displays information about the current world to the player
            if (args.length == 0) {
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Du befindest dich gerade in der Welt ", SurfColors.INFO))
                        .append(Component.text(player.getWorld().getName(), SurfColors.TERTIARY))
                        .append(Component.text("!", SurfColors.INFO))));
                return true;
            }

            if (args[0].equalsIgnoreCase("change") || args[0].equalsIgnoreCase("join")) {//change the current world
                WorldChange.change(player, args);
                return true;
            } else if (args[0].equalsIgnoreCase("create")){//create a new world
                WorldCreate.createFromArguments(player, args);
                return true;
            }else if (args[0].equalsIgnoreCase("load")){//loads a world
                WorldLoad.load(player, args);
                return true;
            }else if (args[0].equalsIgnoreCase("unload")){//unload a world
                WorldUnload.unload(player, args);
                return true;
            }else if (args[0].equalsIgnoreCase("remove")){//deletes a world
                WorldRemove.remove(player, args);
                return true;
            }else if (args[0].equalsIgnoreCase("allPlayers")){//only for commands to teleport all players on a given world to the overworld
                OverworldTeleport.tp(player, args);
                return true;
            }
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();//all completion are added to the list
        List<String> completions = new ArrayList<>();//the final completion list
        String currentarg = args[args.length - 1];//the current argument

        if (args.length > 1){
            //the player wants to change the world
            if (args[0].equalsIgnoreCase("change") || args[0].equalsIgnoreCase("join")){
                //suggest all available worlds
                if (args.length == 2){
                    Bukkit.getWorlds().forEach(world -> list.add(world.getName()));
                    sortedSuggestions(list, currentarg, completions);
                    return completions;
                }
                //suggest all online player
                if (args.length == 3) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        list.add(player.getName());
                    }
                    sortedSuggestions(list, currentarg, completions);
                    return completions;
                }
            }
            if (args[0].equalsIgnoreCase("create")){
                //player enters world name
                if (args.length == 2){
                    list.add("WORLD_NAME");
                    return list;
                }
                //player enters environment
                if (args.length == 3){
                    for (World.Environment environment : World.Environment.values()) {
                        if (environment.name().equalsIgnoreCase("CUSTOM")) continue;
                        list.add(environment.toString());
                    }
                    sortedSuggestions(list, currentarg, completions);
                    return completions;
                }

                if (args.length == 4) {
                    for (WorldType worldType : WorldType.values()) {
                        list.add(worldType.toString());
                    }
                    sortedSuggestions(list, currentarg, completions);
                    return completions;
                }
                if (args.length == 5) {
                    list.add("true");
                    list.add("false");
                    sortedSuggestions(list, currentarg, completions);
                    return completions;
                }
            }
            if (args[0].equalsIgnoreCase("load")){
                //lists all available worlds
                if (args.length == 2){
                    for (File file : Objects.requireNonNull(SurfEssentials.getInstance().getServer().getWorldContainer().listFiles())) {
                        //check if file is directory
                        if (!file.isDirectory()) continue;
                        //if directory contains world file
                        if (!Arrays.asList(Objects.requireNonNull(file.list())).contains("level.dat") || !Arrays.asList(Objects.requireNonNull(file.list())).contains("uid.dat")) continue;
                        //if the world is loaded
                        if (SurfEssentials.getInstance().getServer().getWorld(file.getName()) != null) continue;
                        list.add(file.getName());
                    }
                    sortedSuggestions(list, currentarg, completions);
                }
            }
            if (args[0].equalsIgnoreCase("unload")){
                //lists all loaded worlds
                if (args.length == 2){
                    for (World world : Bukkit.getWorlds()) {
                        list.add(world.getName());
                    }
                    sortedSuggestions(list, currentarg, completions);
                }
            }
            if (args[0].equalsIgnoreCase("remove")){
                //lists all worlds
                if (args.length == 2){
                    for (File file : Objects.requireNonNull(SurfEssentials.getInstance().getServer().getWorldContainer().listFiles())) {
                        //check if file is directory
                        if (!file.isDirectory()) continue;
                        //if directory contains world file
                        if (!Arrays.asList(Objects.requireNonNull(file.list())).contains("level.dat") || !Arrays.asList(Objects.requireNonNull(file.list())).contains("paper-world.yml")) continue;
                        list.add(file.getName());
                    }
                    for (World world : Bukkit.getWorlds()) {
                        if (list.contains(world.getName())) continue;
                        list.add(world.getName());
                    }
                    sortedSuggestions(list, currentarg, completions);
                }
            }
        }

        return list;
    }

    /**
     *
     * success message for creating a world
     *
     * @param player  the command sender
     * @param wc  the WorldCreator
     */
    public static void successCreate(Player player, WorldCreator wc){
        SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append((Component.text("Die Welt ", SurfColors.SUCCESS))
                .append(Component.text(wc.name(), SurfColors.TERTIARY))
                .append(Component.text(" wurde erfolgreich erstellt!", SurfColors.SUCCESS)))
                .hoverEvent(HoverEvent.showText(Component.text("Klicke um dich in die Welt zu teleportieren!", SurfColors.GRAY)))
                .clickEvent(ClickEvent.runCommand("/world join %s".formatted(wc.name())))));
    }

    /**
     *
     * changes the world for a player
     *
     * @param target  the player to teleport
     * @param world  the new world
     * @param logger  the logger
     */
    public static void changeWorld(Player target, World world, ComponentLogger logger){
        double x = target.getLocation().getX();
        double z = target.getLocation().getZ();
        target.teleportAsync(new Location(world, x, world.getHighestBlockYAt((int) x, (int) z), z));

        SurfApi.getUser(target).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Du wirst in die Welt ", SurfColors.SUCCESS))
                .append(Component.text(world.getName(), SurfColors.TERTIARY))
                .append(Component.text(" teleportiert!", SurfColors.SUCCESS))));

        logger.info(target.displayName()
                .append(Component.text(" teleported to the world ", SurfColors.SUCCESS))
                .append(Component.text(world.getName(), SurfColors.TERTIARY))
                .append(Component.text("!", SurfColors.SUCCESS)));
    }

    /**
     *
     * create world message
     *
     * @param target  the sender
     * @param world  the WorldCreator
     */
    public static void creatingWorld(Player target, WorldCreator world){
        SurfApi.getUser(target).thenAccept(user -> user.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Erstelle Welt ", SurfColors.INFO))
                .append(Component.text(world.name(), SurfColors.TERTIARY))
                .append(Component.text("...!", SurfColors.INFO))));
    }

    public static List<String> getCustomLoadedWorlds(){
        List<World> worlds = Bukkit.getWorlds();

        return worlds.stream()
                .filter(world -> !world.getName().equalsIgnoreCase("world")
                        && !world.getName().equalsIgnoreCase("world_the_end")
                        && !world.getName().equalsIgnoreCase("world_the_nether"))
                .map(World::getName)
                .collect(Collectors.toList());
    }
}
