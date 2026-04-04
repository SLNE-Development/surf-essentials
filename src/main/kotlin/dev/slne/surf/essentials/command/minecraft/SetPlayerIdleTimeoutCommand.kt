package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import org.bukkit.Bukkit

fun setPlayerIdleTimeoutCommand() = commandTree("setidletimeout") {
    withPermission(EssentialsPermissionRegistry.PLAYER_IDLE_TIMEOUT_COMMAND)
    integerArgument("minutes") {
        anyExecutor { executor, args ->
            val minutes: Int by args

            Bukkit.setIdleTimeout(minutes)
            executor.sendText {
                appendSuccessPrefix()
                success("Die Inaktivitätszeit wurde auf ")
                variableValue("$minutes Minuten")
                success(" gesetzt.")
            }
        }
    }
}