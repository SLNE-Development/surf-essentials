package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Location
import org.bukkit.entity.EntityType

fun summonCommand() = commandTree("summon") {
    withPermission(EssentialsPermissionRegistry.SUMMON_COMMAND)
    entityTypeArgument("entityType") {
        playerExecutor { player, args ->
            val entityType: EntityType by args

            player.location.world.spawnEntity(player.location, entityType)
            player.sendText {
                appendPrefix()
                success("Du hast einen ")
                variableValue(entityType.name)
                success(" beschworen.")
            }
        }

        integerArgument("amount") {
            playerExecutor { player, args ->
                val entityType: EntityType by args
                val amount: Int by args

                repeat(amount) {
                    player.location.world.spawnEntity(player.location, entityType)
                }

                player.sendText {
                    appendPrefix()
                    success("Du hast ")
                    variableValue(amount)
                    success(" ")
                    variableValue(entityType.name)
                    success(" beschworen.")
                }
            }

            locationArgument("location") {
                playerExecutor { player, args ->
                    val entityType: EntityType by args
                    val amount: Int by args
                    val location: Location by args

                    repeat(amount) {
                        location.world.spawnEntity(location, entityType)
                    }

                    player.sendText {
                        appendPrefix()
                        success("Du hast ")
                        variableValue(amount)
                        success(" ")
                        variableValue(entityType.name)
                        success(" beschworen.")
                    }
                }
            }
        }
    }
}
