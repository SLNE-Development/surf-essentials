package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import org.bukkit.entity.Entity

fun rideCommand() = commandTree("ride") {
    withPermission(EssentialsPermissionRegistry.RIDE_COMMAND)
    entitySelectorArgumentOneEntity("target") {
        playerExecutor { player, args ->
            val target: Entity by args
            target.addPassenger(player)

            player.sendText {
                appendSuccessPrefix()
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

                    if (target.uniqueId == vehicle.uniqueId) {
                        executor.sendText {
                            appendErrorPrefix()
                            error("Du kannst ein Entity nicht auf sich selbst setzen.")
                        }
                        return@anyExecutor
                    }

                    val result = vehicle.addPassenger(target)

                    if (result) {
                        executor.sendText {
                            appendSuccessPrefix()
                            success("Du hast ")
                            variableValue(target.name)
                            success(" auf ")
                            variableValue(vehicle.name)
                            success(" gesetzt.")
                        }
                    } else {
                        executor.sendText {
                            appendErrorPrefix()
                            error("${target.name} kann nicht auf ${vehicle.name} gesetzt werden.")
                        }
                    }
                }
            }
        }

        literalArgument("dismount") {
            anyExecutor { executor, args ->
                val target: Entity by args

                target.vehicle?.removePassenger(target)

                executor.sendText {
                    appendSuccessPrefix()
                    variableValue(target.name)
                    success(" wurde abgesetzt.")
                }
            }
        }
    }
}