package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.worldArgument
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit
import org.bukkit.World

fun listCommand() = commandTree("list") {
    withPermission(EssentialsPermissionRegistry.LIST_COMMAND)
    anyExecutor { executor, _ ->
        val onlinePlayers = Bukkit.getOnlinePlayers()
        val maxCount = Bukkit.getMaxPlayers()

        if (onlinePlayers.isEmpty()) {
            executor.sendText {
                appendPrefix()
                error("Es sind aktuell keine Spieler online.")
            }
            return@anyExecutor
        }

        executor.sendText {
            appendPrefix()
            info("Es sind aktuell ")
            variableValue(onlinePlayers.size)
            info(" von ")
            variableValue(maxCount)
            info(" Spielern online.")
        }
    }
    worldArgument("world") {
        withPermission(EssentialsPermissionRegistry.LIST_COMMAND_WORLD)
        anyExecutor { executor, args ->
            val world: World by args
            val playersInWorld = world.players

            if (playersInWorld.isEmpty()) {
                executor.sendText {
                    appendPrefix()
                    error("In der Welt ")
                    variableValue(world.name)
                    error(" sind aktuell keine Spieler online.")
                }
                return@anyExecutor
            }

            executor.sendText {
                appendPrefix()
                info("In der Welt ")
                variableValue(world.name)
                info(" sind aktuell ")
                variableValue(playersInWorld.size)
                info(" Spieler online.")
            }
        }
    }
}