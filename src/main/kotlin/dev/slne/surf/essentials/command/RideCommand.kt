package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Entity

fun rideCommand() = commandTree("ride") {
    withPermission(EssentialsPermissionRegistry.RIDE_COMMAND)
    entitySelectorArgumentOneEntity("target") {
        playerExecutor { player, args ->
            val target: Entity by args
            target.addPassenger(player)

            player.sendText {
                appendPrefix()
                success("Du sitzt nun auf ")
                variableValue(target.name)
                success(".")
            }
        }

        literalArgument("mount") {
            entitySelectorArgumentOneEntity("vehicle") {
                anyExecutor { executor, args ->
                    val target: Entity by args
                    val vehicle: Entity by args

                    val result = vehicle.addPassenger(target)

                    if (result) {
                        executor.sendText {
                            appendPrefix()
                            success("Du hast ")
                            variableValue(target.name)
                            success(" auf ")
                            variableValue(vehicle.name)
                            success(" gesetzt.")
                        }
                    } else {
                        executor.sendText {
                            appendPrefix()
                            error("${target.name} kann nicht auf ${vehicle.name} gesetzt werden.")
                        }
                    }
                }
            }
        }

        literalArgument("dismount") {
            anyExecutor { executor, args ->
                val target: Entity by args

                target.teleportAsync(target.location).thenRun {
                    executor.sendText {
                        appendPrefix()
                        variableValue(target.name)
                        success(" wurde abgesetzt.")
                    }
                }
            }
        }
    }
}