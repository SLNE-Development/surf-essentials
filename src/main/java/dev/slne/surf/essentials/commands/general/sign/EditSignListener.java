package dev.slne.surf.essentials.commands.general.sign;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EditSignListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        //Check if player has permission to edit a sign
        if (!player.hasPermission("sign.edit")) {
            return;
        }

        //The clicked Block
        Block block = event.getClickedBlock();

        //Checks if right clicked is a Sign, the player isn´t sneaking and has right clicked the block
        if (block != null && block.getType().toString().contains("SIGN") && !player.isSneaking() && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            //Gets the material from the Sign
            Material material = block.getType();
            Block attachedBlock;
            //Gets the block data from sign
            BlockData data = block.getBlockData();

            //If sign is Wall sign
            if (data instanceof Directional){
                Directional directional = (Directional) data;
                //Gets the facing direction
                attachedBlock = block.getRelative(directional.getFacing().getOppositeFace());
            }else {
                //Sets the direction to down
                attachedBlock = block.getRelative(BlockFace.DOWN);
            }
            //placed a copy of the sign
            BlockPlaceEvent placeEvent = new BlockPlaceEvent(block, block.getState(), attachedBlock, new ItemStack(material), player, true, EquipmentSlot.HAND);
            Bukkit.getPluginManager().callEvent(placeEvent);

            //Check if place event is canceled
            if (!placeEvent.isCancelled()){
                Sign sign = (Sign) block.getState();
                //Makes the sign editable and opens the sign gui for the player
                sign.setEditable(true);
                player.openSign(sign);
                //updates the sign for changes
                sign.update();
            }else {
                return;
            }
        }
    }
}
