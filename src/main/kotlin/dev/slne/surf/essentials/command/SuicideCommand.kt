package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

fun suicideCommand() = commandTree("suicide") {
    withPermission(EssentialsPermissionRegistry.SUICIDE_COMMAND)
    playerExecutor { player, _ ->
        player.inventory.clear()
        player.health = 0.0
        player.sendHealthUpdate()

        player.sendText {
            appendPrefix()
            success("Du wurdest auf tragische Weise getötet!")
        }
    }

    entitySelectorArgumentOnePlayer("player") {
        withPermission(EssentialsPermissionRegistry.SUICIDE_COMMAND_OTHERS)
        anyExecutor { executor, args ->
            val player: Player by args

            player.inventory.clear()
            player.health = 0.0
            player.sendHealthUpdate()

            player.sendText {
                appendPrefix()
                success("${executor.name} starb auf tragischer Weise.")
            }
        }
    }
}