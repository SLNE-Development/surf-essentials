package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

fun teleportToTopCommand() = commandTree("teleporttop") {
    withAliases("tptop")
    withPermission(EssentialsPermissionRegistry.TELEPORT_TOP_COMMAND)
    playerExecutor { player, _ ->
        player.teleportAsync(
            player.world.getHighestBlockAt(player.location).location.clone().add(0.0, 1.25, 0.0)
        )
        player.sendText {
            appendPrefix()
            success("Du wurdest zum höchsten Block teleportiert.")
        }
    }
    entitySelectorArgumentOnePlayer("player") {
        withPermission(EssentialsPermissionRegistry.TELEPORT_TOP_COMMAND_OTHERS)
        anyExecutor { executor, args ->
            val player: Player by args
            player.teleportAsync(
                player.world.getHighestBlockAt(player.location).location.clone().add(0.0, 1.25, 0.0)
            )

            executor.sendText {
                appendPrefix()
                variableValue(player.name)
                success(" wurde zum höchsten Block teleportiert.")
            }

            player.sendText {
                appendPrefix()
                success("Du wurdest zum höchsten Block teleportiert.")
            }
        }
    }
}