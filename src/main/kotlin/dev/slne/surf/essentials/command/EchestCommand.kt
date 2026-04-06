package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

fun echestCommand() = commandTree("echest") {
    withPermission(EssentialsPermissionRegistry.ECHEST_COMMAND)
    playerExecutor { player, _ ->
        player.openInventory(player.enderChest)
        player.sendText {
            appendSuccessPrefix()
            success("Du hast deine Endertruhe geöffnet.")
        }
    }
    entitySelectorArgumentOnePlayer("player") {
        withPermission(EssentialsPermissionRegistry.ECHEST_COMMAND_OTHERS)
        playerExecutor { executor, args ->
            val player: Player by args
            executor.openInventory(player.enderChest)
            executor.sendText {
                appendSuccessPrefix()
                success("Du hast die Endertruhe von ")
                variableValue(player.name)
                success(" geöffnet.")
            }
        }
    }
}
