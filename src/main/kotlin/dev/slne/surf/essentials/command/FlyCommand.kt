package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

fun flyCommand() = commandTree("fly") {
    withPermission(EssentialsPermissionRegistry.FLY_COMMAND)
    playerExecutor { player, args ->
        if (player.allowFlight) {
            player.allowFlight = false
            player.isFlying = false


            player.sendText {
                appendSuccessPrefix()
                success("Du kannst nun nicht mehr fliegen.")
            }
        } else {
            player.allowFlight = true
            player.isFlying = true

            player.sendText {
                appendSuccessPrefix()
                success("Du kannst nun fliegen.")
            }
        }
    }
    entitySelectorArgumentOnePlayer("player") {
        withPermission(EssentialsPermissionRegistry.FLY_COMMAND_OTHERS)
        anyExecutor { executor, args ->
            val player: Player by args

            if (player.allowFlight) {
                player.allowFlight = false
                player.isFlying = false


                executor.sendText {
                    appendSuccessPrefix()
                    variableValue(player.name)
                    success(" kann nun nicht mehr fliegen.")
                }

                player.sendText {
                    appendSuccessPrefix()
                    success("Du kannst nun nicht mehr fliegen.")
                }
            } else {
                player.allowFlight = true
                player.isFlying = true

                executor.sendText {
                    appendSuccessPrefix()
                    variableValue(player.name)
                    success(" kann nun fliegen.")
                }

                player.sendText {
                    appendSuccessPrefix()
                    success("Du kannst nun fliegen.")
                }
            }
        }
    }
}