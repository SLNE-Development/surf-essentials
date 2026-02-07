package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.util.translatable
import dev.slne.surf.surfapi.bukkit.api.command.args.adventureCompoundBinaryTagArgument
import dev.slne.surf.surfapi.bukkit.api.nms.NmsUseWithCaution
import dev.slne.surf.surfapi.bukkit.api.nms.bridges.entityBridge
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.nbt.CompoundBinaryTag
import org.bukkit.Location
import org.bukkit.entity.EntityType

@Suppress("UnstableApiUsage")
@NmsUseWithCaution
fun summonCommand() = commandTree("summon") {
    withPermission(EssentialsPermissionRegistry.SUMMON_COMMAND)
    entityTypeArgument("entityType") {
        playerExecutor { player, args ->
            val entityType: EntityType by args

            player.location.world.spawnEntity(player.location, entityType)
            player.sendText {
                appendSuccessPrefix()
                success("Du hast einen ")
                translatable(entityType.translationKey()).color(Colors.VARIABLE_VALUE)
                success(" beschworen.")
            }
        }

        locationArgument("location") {
            playerExecutor { player, args ->
                val entityType: EntityType by args
                val location: Location by args

                location.world.spawnEntity(location, entityType)

                player.sendText {
                    appendSuccessPrefix()
                    success("Du hast ")
                    translatable(entityType.translationKey()).color(Colors.VARIABLE_VALUE)
                    success(" beschworen.")
                }
            }

            adventureCompoundBinaryTagArgument("nbt") {
                playerExecutor { player, args ->
                    val entityType: EntityType by args
                    val location: Location by args
                    val nbt: CompoundBinaryTag by args

                    entityBridge.createEntityByNbt(player.world, entityType, location, nbt)

                    player.sendText {
                        appendSuccessPrefix()
                        success("Du hast ")
                        translatable(entityType.translationKey()).color(Colors.VARIABLE_VALUE)
                        success(" beschworen.")
                    }
                }

                integerArgument("amount") {
                    playerExecutor { player, args ->
                        val entityType: EntityType by args
                        val location: Location by args
                        val nbt: CompoundBinaryTag by args
                        val amount: Int by args

                        repeat(amount) {
                            entityBridge.createEntityByNbt(player.world, entityType, location, nbt)
                        }

                        player.sendText {
                            appendSuccessPrefix()
                            success("Du hast ")
                            success("$amount ")
                            translatable(entityType.translationKey()).color(Colors.VARIABLE_VALUE)
                            success(" beschworen.")
                        }
                    }
                }
            }
        }
    }
}
