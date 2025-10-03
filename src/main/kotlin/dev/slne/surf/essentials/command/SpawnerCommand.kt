package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.translatable
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Location
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType

fun spawnerCommand() = commandTree("spawner") {
    withPermission(EssentialsPermissionRegistry.SPAWNER_COMMAND)
    literalArgument("info") {
        locationArgument("block") {
            playerExecutor { player, arguments ->
                val block: Location by arguments
                val spawner = block.block.state as? CreatureSpawner ?: run {
                    player.sendText {
                        appendPrefix()
                        error("Du musst einen Spawner auswählen.")
                    }
                    return@playerExecutor
                }

                player.sendText {
                    appendPrefix()
                    info("Der Spawner versucht alle ")
                    variableValue(spawner.delay)
                    info(" Ticks bis zu ")
                    variableValue(spawner.spawnCount)
                    info(" Entitäten vom Typ ")
                    translatable(
                        spawner.spawnedType?.translationKey() ?: "Unbekannt"
                    ).colorIfAbsent(Colors.VARIABLE_VALUE)
                    info(" im Radius von ")
                    variableValue("${spawner.spawnRange} Blöcken")
                    info(" zu spawnen, solange ein Spieler in ")
                    variableValue("${spawner.requiredPlayerRange} Blöcken Entfernung")
                    info(" ist.")
                }
            }
        }
    }

    literalArgument("modify") {
        locationArgument("block") {
            literalArgument("delay") {
                integerArgument("minTicks") {
                    integerArgument("maxTicks") {
                        anyExecutor { executor, args ->
                            val block: Location by args
                            val minTicks: Int by args
                            val maxTicks: Int by args

                            val spawner = block.block.state as? CreatureSpawner ?: run {
                                executor.sendText {
                                    appendPrefix()
                                    error("Du musst einen Spawner auswählen.")
                                }
                                return@anyExecutor
                            }

                            if (minTicks < 1 || maxTicks < 1 || minTicks > maxTicks) {
                                executor.sendText {
                                    appendPrefix()
                                    error("Die minimale und maximale Tick-Anzahl muss mindestens 1 sein und die minimale Anzahl darf die maximale nicht übersteigen.")
                                }
                                return@anyExecutor
                            }

                            spawner.minSpawnDelay = minTicks
                            spawner.maxSpawnDelay = maxTicks
                            spawner.update()

                            executor.sendText {
                                appendPrefix()
                                success("Der Spawner versucht nun alle ")
                                variableValue("$minTicks bis $maxTicks Ticks")
                                success(" neue Monster zu spawnen.")
                            }
                        }
                    }
                }
            }

            literalArgument("count") {
                integerArgument("amount") {
                    anyExecutor { executor, args ->
                        val block: Location by args
                        val amount: Int by args

                        val spawner = block.block.state as? CreatureSpawner ?: run {
                            executor.sendText {
                                appendPrefix()
                                error("Du musst einen Spawner auswählen.")
                            }
                            return@anyExecutor
                        }

                        if (amount < 1) {
                            executor.sendText {
                                appendPrefix()
                                error("Die Anzahl der zu spawnenden Entitäten muss mindestens 1 sein.")
                            }
                            return@anyExecutor
                        }

                        spawner.spawnCount = amount
                        spawner.update()

                        executor.sendText {
                            appendPrefix()
                            success("Der Spawner spawnt nun ")
                            variableValue("$amount Entitäten")
                            success(" pro Spawn-Versuch.")
                        }
                    }
                }
            }

            literalArgument("type") {
                entityTypeArgument("entityType") {
                    anyExecutor { executor, args ->
                        val block: Location by args
                        val entityType: EntityType by args

                        val spawner = block.block.state as? CreatureSpawner ?: run {
                            executor.sendText {
                                appendPrefix()
                                error("Du musst einen Spawner auswählen.")
                            }
                            return@anyExecutor
                        }

                        spawner.spawnedType = entityType
                        spawner.update()

                        executor.sendText {
                            appendPrefix()
                            success("Der Spawner spawnt nun ")
                            translatable(entityType.translationKey()).colorIfAbsent(Colors.VARIABLE_VALUE)
                            success(".")
                        }
                    }
                }
            }

            literalArgument("range") {
                integerArgument("spawnRange") {
                    integerArgument("requiredPlayerRange") {
                        anyExecutor { executor, args ->
                            val block: Location by args
                            val spawnRange: Int by args
                            val requiredPlayerRange: Int by args

                            val spawner = block.block.state as? CreatureSpawner ?: run {
                                executor.sendText {
                                    appendPrefix()
                                    error("Du musst einen Spawner auswählen.")
                                }
                                return@anyExecutor
                            }

                            if (spawnRange < 1 || requiredPlayerRange < 1) {
                                executor.sendText {
                                    appendPrefix()
                                    error("Die Reichweiten müssen mindestens 1 Block betragen.")
                                }
                                return@anyExecutor
                            }

                            spawner.spawnRange = spawnRange
                            spawner.requiredPlayerRange = requiredPlayerRange
                            spawner.update()

                            executor.sendText {
                                appendPrefix()
                                success("Der Spawner spawnt nun Entitäten in einem Radius von ")
                                variableValue("$spawnRange Blöcken")
                                success(" und benötigt einen Spieler in einem Radius von ")
                                variableValue("$requiredPlayerRange Blöcken")
                                success(".")
                            }
                        }
                    }
                }
            }
        }
    }
}