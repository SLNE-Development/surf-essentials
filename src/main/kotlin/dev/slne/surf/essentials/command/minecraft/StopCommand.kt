package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit
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

        Bukkit.shutdown()
    }
}