package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.api.paper.util.forEachPlayer
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.player.PlayerKickEvent

fun stopCommand() = commandTree("stop") {
    withPermission(EssentialsPermissionRegistry.STOP_COMMAND)
    anyExecutor { executor, _ ->
        executor.sendText {
            appendSuccessPrefix()
            success("Der Server wird heruntergefahren...")
        }

        Bukkit.broadcast(buildText {
            appendSuccessPrefix()
            variableValue(executor.name)
            success(" fährt den Server herunter...")
        }, EssentialsPermissionRegistry.STOP_NOTIFY)

        forEachPlayer {
            it.kick(buildText {
                error("Der Server wird heruntergefahren...")
            }, PlayerKickEvent.Cause.RESTART_COMMAND)
        }

        server.worlds.forEach(World::save)

        Bukkit.shutdown()
    }
}