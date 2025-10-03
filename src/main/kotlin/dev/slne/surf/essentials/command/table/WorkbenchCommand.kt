package dev.slne.surf.essentials.command.table

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType

fun workbenchCommand() = commandTree("workbench") {
    withAliases("wb")
    withPermission(EssentialsPermissionRegistry.WORKBENCH_COMMAND)
    playerExecutor { player, _ ->
        player.openInventory(Bukkit.createInventory(player, InventoryType.WORKBENCH))
        player.sendText {
            appendPrefix()
            success("Du hast eine Werkbank geöffnet.")
        }
    }
}