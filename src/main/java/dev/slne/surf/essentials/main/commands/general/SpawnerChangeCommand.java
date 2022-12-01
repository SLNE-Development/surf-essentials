package dev.slne.surf.essentials.main.commands.general;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dev.slne.surf.api.utils.message.SurfColors.SUCCESS;

public class SpawnerChangeCommand extends EssentialsCommand {
    public SpawnerChangeCommand(PluginCommand command) {
        super(command);
        command.setDescription("Allows you to change the type, speed and radius of a spawner.");
        command.setUsage("/spawner [<entity>] [<minSpawnDelay>] [<maxSpawnDelay>] [<spawnRange>] [<requiredPlayerRange>]");
        command.setPermission("surf.essentials.command.spawner");
        command.permissionMessage(SurfEssentials.NO_PERMISSION());
    }
    SurfEssentials surf = SurfEssentials.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //check if sender is player
        if (sender instanceof Player player){
            //check if the player is facing a block, if not, a spawner is given
            if (player.getTargetBlock(6) == null){
                player.getInventory().addItem(new ItemStack(Material.SPAWNER));
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Dir wurde ein Spawner gegeben!", SUCCESS))));
                return true;
            }
            //target block
            Block block = player.getTargetBlock(6);
            //check if the block is a spawner, otherwise a spawner is given
            if (Objects.requireNonNull(block).getBlockData().getMaterial() != Material.SPAWNER){
                player.getInventory().addItem(new ItemStack(Material.SPAWNER));
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Dir wurde ein Spawner gegeben!", SUCCESS))));
                return true;
            }
            //the spawner that the player targets
            final CreatureSpawner spawner = (CreatureSpawner) block.getState();
            //if no argument is given
            if (args.length == 0){
                //displays information about the spawner
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Dieser Spawner spawnt ", SUCCESS))
                        .append(Component.text(spawner.getSpawnedType().toString().toLowerCase(), SurfColors.GOLD))
                        .append(Component.text(" alle ", SUCCESS))
                        .append(Component.text(spawner.getMinSpawnDelay(), SurfColors.GOLD)
                                .hoverEvent(HoverEvent.showText(Component.text(surf.ticksToString(spawner.getMinSpawnDelay()), SurfColors.GRAY))))
                        .append(Component.text(" - ", SUCCESS))
                        .append(Component.text(spawner.getMaxSpawnDelay(), SurfColors.GOLD)
                                .hoverEvent(HoverEvent.showText(Component.text(surf.ticksToString(spawner.getMaxSpawnDelay()), SurfColors.GRAY))))
                        .append(Component.text(" ticks!", SUCCESS))));
                return true;
            }
            //the entity for arg[0]
            EntityType entity = null;
            //check if input is valid
            try {
                entity = EntityType.valueOf(args[0].toUpperCase());
            }catch (IllegalArgumentException ignored) {}
            //if the entity is not valid, a message is sent to the player
            if (entity == null){
                somethingWentWrongAsync_DE(player, "Ungültige entity");
                return true;
            }

            switch (args.length) {
                //only an entity is given
                case 1 -> {
                    //set the new entity type for the spawner
                    spawner.setSpawnedType(entity);
                    spawner.update();
                    changeMessage_DE(player, spawner, -1);
                }
                //an entity and minimum-spawn-delay is given
                case 2 -> {
                    //check if arg[1] is valid int
                    if (!surf.isInt(args[1])) {
                        somethingWentWrongAsync_DE(player, "Du musst eine gültige Zahl angeben!");
                        break;
                    }
                    //check if integer is lower than the max spawn delay
                    if (Integer.parseInt(args[1]) > spawner.getMaxSpawnDelay()){
                       somethingWentWrongAsync_DE(player, "Minimum Spawn Delay muss kleiner oder gleich der Maximum Spawn Delay sein!");
                       return true;
                    }
                    //set new conditions for the spawner
                    spawner.setSpawnedType(entity);
                    spawner.setMinSpawnDelay(Integer.parseInt(args[1]));
                    spawner.update();
                    changeMessage_DE(player, spawner, -1);
                }
                //an entity, minimum-spawn-delay and maximum-spawn-delay is given
                case 3 -> {
                    //check if arg[1] & arg[2] are valid integers
                    if (!surf.isInt(args[1]) || !surf.isInt(args[2])) {
                        somethingWentWrongAsync_DE(player, "Du musst eine gültige Zahl angeben!");
                        break;
                    }
                    //check if integer is lower than the max spawn delay
                    if (Integer.parseInt(args[1]) > spawner.getMaxSpawnDelay()){
                        somethingWentWrongAsync_DE(player, "Minimum Spawn Delay muss kleiner oder gleich der Maximum Spawn Delay sein!");
                        return true;
                    }
                    //set new conditions for the spawner
                    spawner.setSpawnedType(entity);
                    spawner.setMinSpawnDelay(Integer.parseInt(args[1]));
                    spawner.setMaxSpawnDelay(Integer.parseInt(args[2]));
                    spawner.update();
                    changeMessage_DE(player, spawner, -1);
                }
                //an entity, minimum-spawn-delay, maximum-spawn-delay and spawn-range is given
                case 4 -> {
                    if (!surf.isInt(args[1]) || !surf.isInt(args[2]) || !surf.isInt(args[3])) {
                        somethingWentWrongAsync_DE(player, "Du musst eine gültige Zahl angeben!");
                        break;
                    }
                    //check if integer is lower than the max spawn delay
                    if (Integer.parseInt(args[1]) > spawner.getMaxSpawnDelay()){
                        somethingWentWrongAsync_DE(player, "Minimum Spawn Delay muss kleiner oder gleich des Maximum Spawn Delay sein!");
                        return true;
                    }
                    //set new conditions for the spawner
                    spawner.setSpawnedType(entity);
                    spawner.setMinSpawnDelay(Integer.parseInt(args[1]));
                    spawner.setMaxSpawnDelay(Integer.parseInt(args[2]));
                    spawner.setSpawnRange(Integer.parseInt(args[3]));
                    spawner.update();
                    changeMessage_DE(player, spawner, -1);
                }
                //an entity, minimum-spawn-delay, maximum-spawn-delay, spawn-range and required-player-range is given
                default -> {
                    if (!surf.isInt(args[1]) || !surf.isInt(args[2]) || !surf.isInt(args[3]) || !surf.isInt(args[4])) {
                        somethingWentWrongAsync_DE(player, "Du musst eine gültige Zahl angeben!");
                        break;
                    }
                    if (Integer.parseInt(args[1]) > spawner.getMaxSpawnDelay()){
                        somethingWentWrongAsync_DE(player, "Minimum Spawn Delay muss kleiner oder gleich der Maximum Spawn Delay sein!");
                        return true;
                    }
                    spawner.setSpawnedType(entity);
                    spawner.setMinSpawnDelay(Integer.parseInt(args[1]));
                    spawner.setMaxSpawnDelay(Integer.parseInt(args[2]));
                    spawner.setSpawnRange(Integer.parseInt(args[3]));
                    spawner.setRequiredPlayerRange(Integer.parseInt(args[4]));
                    spawner.update();
                    changeMessage_DE(player, spawner, spawner.getRequiredPlayerRange());
                }
            }
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //returns a list of all entity's
        List<String> list = new ArrayList<>();
        if (!(args.length == 1)) return list;
        EntityType[] entitys = EntityType.values();
        for (EntityType entity : entitys) {
            list.add(entity.name());
        }
        List<String> allEnitys = new ArrayList<>();
        String currentarg = args[args.length - 1];
        for (String s : list) {
            if (s.startsWith(currentarg.toUpperCase())) {
                allEnitys.add(s);
            }
        }
        return allEnitys;
    }

    /**
     *
     * Sends an error message to the sender.
     *
     * @param sender  the sender
     * @param error  the error
     */
    public void somethingWentWrongAsync_DE(Player sender, String error){
        SurfApi.getUser(sender).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(surf.gradientify("Es ist ein Fehler aufgetreten:", "#eb3349", "#f45c43"))
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                .append(surf.gradientify(error, "#EA98DA", "#5B6CF9"))));
    }

    /**
     *
     * Sends a success message to the sender.
     *
     * @param player  the sender
     * @param spawner  the spawner
     * @param amount  RequiredPlayerRange
     */
    private void changeMessage_DE(Player player, CreatureSpawner spawner, int amount){
        TextComponent.Builder builder = Component.text();
        builder.append(SurfApi.getPrefix()
                .append(Component.text("Dieser Spawner spawnt nun ", SUCCESS))
                .append(Component.text(spawner.getSpawnedType().toString().toLowerCase(), SurfColors.GOLD))
                .append(Component.text(" alle ", SUCCESS))
                .append(Component.text(spawner.getMinSpawnDelay(), SurfColors.GOLD)
                        .hoverEvent(HoverEvent.showText(Component.text(surf.ticksToString(spawner.getMinSpawnDelay()), SurfColors.GRAY))))
                .append(Component.text(" - ", SUCCESS))
                .append(Component.text(spawner.getMaxSpawnDelay(), SurfColors.GOLD)
                        .hoverEvent(HoverEvent.showText(Component.text(surf.ticksToString(spawner.getMaxSpawnDelay()), SurfColors.GRAY))))
                .append(Component.text(" ticks, in einem Radius von ", SUCCESS))
                .append(Component.text(spawner.getSpawnRange(), SurfColors.GOLD))
                .append(Component.text(" Blöcken!", SUCCESS)));

        if (amount != -1){
            builder.append(Component.newline()
                    .append(SurfApi.getPrefix())
                    .append(Component.text("Es muss sich ein Spieler im Radius von ", SUCCESS))
                    .append(Component.text(amount, SurfColors.GOLD))
                    .append(Component.text(" Blöcken in der nähe befinden!", SUCCESS)));
        }

        SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(builder.build()));
    }
}
