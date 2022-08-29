package dev.slne.surf.essentials.commands.cheat;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RepairCommand extends EssentialsCommand {
    public RepairCommand(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Check if sender is player
        if (!(sender instanceof Player player)) {sender.sendMessage(Component.text("You must be a player to execute this command!", SurfColors.ERROR)); return true;}
        //if player provided right args
        if (args.length > 0){player.sendMessage(SurfApi.getPrefix().append(Component.text("Du darfst keine Argumente angeben!", SurfColors.ERROR ))); return true;}
        //Repairs item in hand
        repairinHand(player);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }

    //Repair tool/armor in main hand
    public static void repairinHand(Player player) {
        //Material Type
        final Material material = player.getInventory().getItemInMainHand().getType();
        //Check if item is valid
        if (!(material.isBlock() || material.getMaxDurability() < 1)) {
            //Get item Meta
            Damageable item = (Damageable) player.getInventory().getItemInMainHand().getItemMeta();
            //Repair item in hand
            item.setDamage(0);
            player.getInventory().getItemInMainHand().setItemMeta(item);
            //Success Message
            player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Dein Item wurde Repariert!", SurfColors.SUCCESS)));
        }else {
            //Fail Message
            player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du kannst dieses Item nicht Reparieren!", SurfColors.ERROR))
                    .append(Component.text("\n").append(SurfApi.getPrefix()).append(Component.text("Bitte stelle sicher, dass du das zu reparierende Item in deiner Main Hand hälst!", SurfColors.GOLD))));

        }

    }

}
