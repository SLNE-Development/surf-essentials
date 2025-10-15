package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit

fun stopCommand() = commandTree("stop") {
    withPermission(EssentialsPermissionRegistry.STOP_COMMAND)
    anyExecutor { executor, _ ->
        executor.sendText {
            appendPrefix()
            success("Der Server wird heruntergefahren...")
        }

        Bukkit.broadcast(buildText {
            appendPrefix()
            variableValue(executor.name)
            success(" fährt den Server herunter...")
        }, EssentialsPermissionRegistry.STOP_NOTIFY)

        Bukkit.shutdown()
    }
}