package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit

fun changeSlotCommand() = commandTree("changeslot") {
    withPermission(EssentialsPermissionRegistry.CHANGESLOT_COMMAND)
    integerArgument("amount") {
        anyExecutor { executor, args ->
            val amount: Int by args
            Bukkit.getServer().maxPlayers = amount

            executor.sendText {
                appendPrefix()
                success("Die maximale Spieleranzahl wurde auf ")
                variableValue(amount)
                success(" gesetzt.")
            }
        }
    }
}