package dev.slne.surf.essentials.command

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentManyPlayers
import dev.jorel.commandapi.kotlindsl.getValue
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

fun strikeCommand() = commandTree("strike") {
    withPermission(EssentialsPermissionRegistry.STRIKE_COMMAND)
    entitySelectorArgumentManyPlayers("players") {
        anyExecutor { executor, args ->
            val players: Collection<Player> by args

            players.forEach {
                val location = it.location
                plugin.launch(plugin.regionDispatcher(location)) {
                    location.world.spawnEntity(location, EntityType.LIGHTNING_BOLT)
                }
            }

            executor.sendText {
                appendPrefix()
                success("Du hast ")
                variableValue(players.size.toString())
                success(" Spieler vom Blitz treffen lassen.")
            }
        }
    }
}