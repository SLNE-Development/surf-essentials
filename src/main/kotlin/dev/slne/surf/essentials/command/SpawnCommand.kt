package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.worldArgument
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.World
import org.bukkit.event.player.PlayerTeleportEvent

fun spawnCommand() = commandTree("spawn") {
    withPermission(EssentialsPermissionRegistry.SPAWN_COMMAND)
    playerExecutor { player, _ ->
        player.sendText {
            appendPrefix()
            info("Du wirst zum Spawn teleportiert...")
        }

        player.teleportAsync(player.world.spawnLocation, PlayerTeleportEvent.TeleportCause.COMMAND)
            .thenRun {
                player.sendText {
                    appendPrefix()
                    success("Du wurdest zum Spawn teleportiert.")
                }
            }
    }

    worldArgument("world") {
        withPermission(EssentialsPermissionRegistry.SPAWN_COMMAND_WORLD)
        playerExecutor { player, args ->
            val world: World by args

            player.sendText {
                appendPrefix()
                info("Du wirst zum Spawn der Welt ")
                variableValue(world.name)
                info(" teleportiert...")
            }

            player.teleportAsync(world.spawnLocation, PlayerTeleportEvent.TeleportCause.COMMAND)
                .thenRun {
                    player.sendText {
                        appendPrefix()
                        success("Du wurdest zum Spawn der Welt ")
                        variableValue(world.name)
                        success(" teleportiert.")
                    }
                }
        }
    }
}