package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.translatable
import dev.slne.surf.surfapi.core.api.messages.Colors
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
                translatable(entityType.translationKey()).color(Colors.VARIABLE_VALUE)
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
                    translatable(entityType.translationKey()).color(Colors.VARIABLE_VALUE)
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
                        translatable(entityType.translationKey()).color(Colors.VARIABLE_VALUE)
                        success(" beschworen.")
                    }
                }
            }
        }
    }//TODO: add NBT Support
}
