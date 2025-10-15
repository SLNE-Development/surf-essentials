package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Location
import org.bukkit.World

fun setWorldSpawnCommand() = commandTree("setworldspawn") {
    withPermission(EssentialsPermissionRegistry.SET_WORLD_SPAWN_COMMAND)
    playerExecutor { player, _ ->
        player.world.spawnLocation = player.location

        player.sendText {
            appendPrefix()
            success("Der Wiedereinstiegspunkt der Welt ")
            variableValue(player.world.name)
            success(" wurde geändert.")
        }
    }

    locationArgument("location") {
        playerExecutor { player, args ->
            val location: Location by args
            val world = player.world

            world.spawnLocation = location

            player.sendText {
                appendPrefix()
                success("Der Wiedereinstiegspunkt der Welt ")
                variableValue(world.name)
                success(" wurde geändert.")
            }
        }

        worldArgument("world") {
            anyExecutor { executor, args ->
                val location: Location by args
                val world: World by args

                world.spawnLocation = location

                executor.sendText {
                    appendPrefix()
                    success("Der Wiedereinstiegspunkt der Welt ")
                    variableValue(world.name)
                    success(" wurde geändert.")
                }
            }
        }
    }
}