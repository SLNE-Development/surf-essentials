package dev.slne.surf.essentials.main.commands.cheat;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class FillStackCommand extends EssentialsCommand {
    public FillStackCommand(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0){
                fillStack(player, true, null);
                return true;
            }else if (!SurfEssentials.getInstance().isInt(args[0])){
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Du musst eine gültige Zahl angeben!", SurfColors.ERROR))));
                return true;
            }else fillStack(player, true, Integer.valueOf(args[0]));
            return true;
        }else if (sender instanceof ConsoleCommandSender){
            ComponentLogger logger = SurfEssentials.logger();
            if (args.length == 0){
                logger.warn("You must specify a player!", SurfColors.ERROR);
                return true;
            }
            if (Bukkit.getPlayerExact(args[0]) == null){
                logger.warn(Component.text("The player does not exist!", SurfColors.ERROR));
                return true;
            }
            if (args.length == 1) {
                fillStack(Bukkit.getPlayerExact(args[0]), false, null );
            }
            if (args.length > 2 && !SurfEssentials.getInstance().isInt(args[1])){
                logger.warn(Component.text("You must enter a valid number!"));
                return true;
            }
            fillStack(Objects.requireNonNull(Bukkit.getPlayerExact(args[0])), false, Integer.valueOf(args[1]));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
    public void fillStack(Player player, Boolean isPlayer, Integer fillSize){
        ComponentLogger logger = SurfEssentials.logger();
        PlayerInventory inv = player.getInventory();
        if (inv.getItemInMainHand().getType() == Material.AIR){
            if (isPlayer){
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Du musst das zu füllende Item in deiner Hand halten!", SurfColors.ERROR))));
            }else {
                logger.warn(Component.text("The player is not holding an item!"));
            }
        }else if (inv.getItemInMainHand().getMaxStackSize() == 1){
            if (isPlayer){
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Das Item ist nicht stackbar", SurfColors.ERROR))));
            }else {
                logger.warn(Component.text("The item is not stackable!", SurfColors.ERROR));
            }
        }
        else if (fillSize != null && fillSize > inv.getItemInMainHand().getMaxStackSize()){
            if (isPlayer){
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Du kannst ", SurfColors.ERROR)
                                .append(inv.getItemInMainHand().displayName().color(SurfColors.GOLD)))
                        .append(Component.text(" nur ", SurfColors.ERROR)
                                .append(Component.text(inv.getItemInMainHand().getMaxStackSize(), SurfColors.AQUA)))
                        .append(Component.text("x stacken!", SurfColors.ERROR))));
            }else {
                logger.warn(Component.text("The maximum stack size is ", SurfColors.ERROR)
                        .append(Component.text(fillSize, SurfColors.GREEN)));
            }
        }else {
            ItemStack item = inv.getItemInMainHand();
            if (fillSize == null){
                item.setAmount(item.getMaxStackSize());
            }else {
                item.setAmount(fillSize);
            }
            player.updateInventory();


            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Das item ", SurfColors.SUCCESS))
                    .append(item.displayName().color(SurfColors.GOLD))
                    .append(Component.text(" wurde erfolgreich gestackt!", SurfColors.SUCCESS))));
            if (!isPlayer){
                logger.info(Component.text("The item was stacked", SurfColors.SUCCESS));
            }
        }
    }
}
