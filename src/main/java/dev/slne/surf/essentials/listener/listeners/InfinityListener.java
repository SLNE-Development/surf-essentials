package dev.slne.surf.essentials.listener.listeners;

import dev.slne.surf.essentials.commands.cheat.InfinityCommand;
import dev.slne.surf.essentials.utils.abtract.CraftUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

/**
 * A listener that handles the Infinity feature related events.
 *
 * @see InfinityCommand
 */
public class InfinityListener implements Listener {
    /**
     * Handles the {@link BlockPlaceEvent}.
     * <p>
     * If the player is in Infinity mode, the item they placed will be regained.
     *
     * @param event the BlockPlaceEvent object
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (InfinityCommand.getPlayersInInfinity().contains(CraftUtil.toServerPlayer(event.getPlayer()))) {
            regainItems(event.getPlayer(), event.getItemInHand(), event.getHand());
        }
    }

    /**
     * Handles the {@link PlayerInteractEvent}.
     * <p>
     * If the player is in Infinity mode and they right click a block with a spawn egg, the egg they used will be regained.
     *
     * @param event the PlayerInteractEvent object
     */
    @EventHandler
    public void onSpawnEggPlace(PlayerInteractEvent event) {
        if (InfinityCommand.getPlayersInInfinity().contains(CraftUtil.toServerPlayer(event.getPlayer()))) {

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getInventory().getItemInMainHand().getItemMeta() instanceof SpawnEggMeta) {
                regainItems(event.getPlayer(), event.getItem(), event.getHand());
            }
        }
    }

    /**
     * Handles the {@link PlayerItemConsumeEvent}.
     * If the player is in Infinity mode, the item they consumed will be regained.
     *
     * @param event the PlayerItemConsumeEvent object
     */
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (InfinityCommand.getPlayersInInfinity().contains(CraftUtil.toServerPlayer(event.getPlayer()))) {
            regainItems(event.getPlayer(), event.getItem(), event.getHand());
        }
    }

    /**
     * Regains the specified item for the given player and the given hand.
     *
     * @param player    the player who will regain the item
     * @param eventItem the item to regain
     * @param hand      the hand that held the item
     */
    public void regainItems(final Player player, final ItemStack eventItem, EquipmentSlot hand) {
        final var inventory = player.getInventory();

        if (hand == EquipmentSlot.HAND) {
            inventory.setItemInMainHand(eventItem);
        } else if (hand == EquipmentSlot.OFF_HAND) {
            inventory.setItemInOffHand(eventItem);
        }

        player.updateInventory();
    }
}
