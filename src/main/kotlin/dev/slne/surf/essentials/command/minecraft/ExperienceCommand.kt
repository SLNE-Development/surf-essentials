package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

fun experienceCommand() = commandTree("experience") {
    withAliases("xp")
    withPermission(EssentialsPermissionRegistry.EXPERIENCE_COMMAND)

    literalArgument("add") {
        entitySelectorArgumentManyPlayers("players") {
            integerArgument("amount") {
                anyExecutor { executor, args ->
                    val players: Collection<Player> by args
                    val amount: Int by args

                    players.forEach {
                        it.giveExp(amount)
                    }

                    executor.sendText {
                        appendPrefix()
                        success("Du hast ")
                        variableValue(amount.toString())
                        success(" Erfahrungspunkte an ")
                        if (players.size == 1) {
                            variableValue(players.firstOrNull()?.name ?: "Unbekannt")
                        } else {
                            variableValue(players.size.toString())
                            success(" Spieler")
                        }
                        success(" vergeben.")
                    }
                }

                literalArgument("levels") {
                    anyExecutor { executor, args ->
                        val players: Collection<Player> by args
                        val amount: Int by args

                        players.forEach {
                            it.giveExpLevels(amount)
                        }

                        executor.sendText {
                            appendPrefix()
                            success("Du hast ")
                            variableValue(amount.toString())
                            success(" Erfahrungsstufen an ")
                            if (players.size == 1) {
                                variableValue(players.firstOrNull()?.name ?: "Unbekannt")
                            } else {
                                variableValue(players.size.toString())
                                success(" Spieler")
                            }
                            success(" vergeben.")
                        }
                    }
                }

                literalArgument("points") {
                    anyExecutor { executor, args ->
                        val players: Collection<Player> by args
                        val amount: Int by args

                        players.forEach {
                            it.giveExp(amount)
                        }

                        executor.sendText {
                            appendPrefix()
                            success("Du hast ")
                            variableValue(amount.toString())
                            success(" Erfahrungspunkte an ")
                            if (players.size == 1) {
                                variableValue(players.firstOrNull()?.name ?: "Unbekannt")
                            } else {
                                variableValue(players.size.toString())
                                success(" Spieler")
                            }
                            success(" vergeben.")
                        }
                    }
                }
            }
        }
    }

    literalArgument("remove") {
        entitySelectorArgumentManyPlayers("players") {
            integerArgument("amount") {
                anyExecutor { executor, args ->
                    val players: Collection<Player> by args
                    val amount: Int by args

                    players.forEach {
                        it.giveExp(-amount)
                    }

                    executor.sendText {
                        appendPrefix()
                        success("Du hast ")
                        variableValue(amount.toString())
                        success(" Erfahrungspunkte von ")
                        if (players.size == 1) {
                            variableValue(players.firstOrNull()?.name ?: "Unbekannt")
                        } else {
                            variableValue(players.size.toString())
                            success(" Spieler")
                        }
                        success(" entfernt.")
                    }
                }

                literalArgument("levels") {
                    anyExecutor { executor, args ->
                        val players: Collection<Player> by args
                        val amount: Int by args

                        players.forEach {
                            it.giveExpLevels(-amount)
                        }

                        executor.sendText {
                            appendPrefix()
                            success("Du hast ")
                            variableValue(amount.toString())
                            success(" Erfahrungsstufen von ")
                            if (players.size == 1) {
                                variableValue(players.firstOrNull()?.name ?: "Unbekannt")
                            } else {
                                variableValue(players.size.toString())
                                success(" Spieler")
                            }
                            success(" entfernt.")
                        }
                    }
                }

                literalArgument("points") {
                    anyExecutor { executor, args ->
                        val players: Collection<Player> by args
                        val amount: Int by args

                        players.forEach {
                            it.giveExp(-amount)
                        }

                        executor.sendText {
                            appendPrefix()
                            success("Du hast ")
                            variableValue(amount.toString())
                            success(" Erfahrungspunkte von ")
                            if (players.size == 1) {
                                variableValue(players.firstOrNull()?.name ?: "Unbekannt")
                            } else {
                                variableValue(players.size.toString())
                                success(" Spieler")
                            }
                            success(" entfernt.")
                        }
                    }
                }
            }
        }
    }

    literalArgument("query") {
        entitySelectorArgumentOnePlayer("player") {
            anyExecutor { executor, args ->
                val player: Player by args
                val level = player.level
                val exp = player.totalExperience

                executor.sendText {
                    appendPrefix()
                    variableValue(player.name)
                    success(" hat ")
                    variableValue(level.toString())
                    if (level == 1) {
                        success(" Erfahrungsstufe")
                    } else {
                        success(" Erfahrungsstufen")
                    }
                    success(" und ")
                    variableValue(exp.toString())
                    if (exp == 1) {
                        success(" Erfahrungspunkt.")
                    } else {
                        success(" Erfahrungspunkte.")
                    }
                }
            }
        }
    }

    literalArgument("set") {
        entitySelectorArgumentManyPlayers("players") {
            integerArgument("amount") {
                anyExecutor { executor, args ->
                    val players: Collection<Player> by args
                    val amount: Int by args

                    players.forEach {
                        it.totalExperience = amount
                    }

                    executor.sendText {
                        appendPrefix()
                        success("Du hast die Erfahrungspunkte auf ")
                        variableValue(amount)
                        success(" für ")
                        if (players.size == 1) {
                            variableValue(players.firstOrNull()?.name ?: "Unbekannt")
                        } else {
                            variableValue(players.size.toString())
                            success(" Spieler")
                        }
                        success(" gesetzt.")
                    }
                }

                literalArgument("levels") {
                    anyExecutor { executor, args ->
                        val players: Collection<Player> by args
                        val amount: Int by args

                        players.forEach {
                            it.level = amount
                        }

                        executor.sendText {
                            appendPrefix()
                            success("Du hast die Erfahrungsstufe auf ")
                            variableValue(amount)
                            success(" für ")
                            if (players.size == 1) {
                                variableValue(players.firstOrNull()?.name ?: "Unbekannt")
                            } else {
                                variableValue(players.size.toString())
                                success(" Spieler")
                            }
                            success(" gesetzt.")
                        }
                    }
                }

                literalArgument("points") {
                    anyExecutor { executor, args ->
                        val players: Collection<Player> by args
                        val amount: Int by args

                        players.forEach {
                            it.totalExperience = amount
                        }

                        executor.sendText {
                            appendPrefix()
                            success("Du hast die Erfahrungspunkte auf ")
                            variableValue(amount)
                            success(" für ")
                            if (players.size == 1) {
                                variableValue(players.firstOrNull()?.name ?: "Unbekannt")
                            } else {
                                variableValue(players.size.toString())
                                success(" Spieler")
                            }
                            success(" gesetzt.")
                        }
                    }
                }
            }
        }
    }
}