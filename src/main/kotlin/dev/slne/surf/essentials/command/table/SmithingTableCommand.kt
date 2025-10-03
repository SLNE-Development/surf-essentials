package dev.slne.surf.essentials.command.table

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType

fun smithingTableCommand() = commandTree("smithingTable") {
    withPermission(EssentialsPermissionRegistry.SMITHING_TABLE_COMMAND)
    playerExecutor { player, _ ->
        player.openInventory(Bukkit.createInventory(player, InventoryType.SMITHING))
        player.sendText {
            appendPrefix()
            success("Du hast einen Schmiedetisch geöffnet.")
        }
    }
}