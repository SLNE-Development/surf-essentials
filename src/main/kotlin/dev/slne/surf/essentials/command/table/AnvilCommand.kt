package dev.slne.surf.essentials.command.table

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType

fun anvilCommand() = commandTree("anvil") {
    withPermission(EssentialsPermissionRegistry.ANVIL_COMMAND)
    playerExecutor { player, _ ->
        player.openInventory(Bukkit.createInventory(player, InventoryType.ANVIL))
        player.sendText {
            appendPrefix()
            success("Du hast einen Amboss geöffnet.")
        }
    }
}