package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.command.argument.gameruleArgument
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.util.translatable
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.GameRule
import org.bukkit.World

fun gameRuleCommand() = commandTree("gamerule") {
    withPermission(EssentialsPermissionRegistry.GAMERULE_COMMAND)
    gameruleArgument("gamerule") {
        playerExecutor { player, args ->
            val gamerule: GameRule<Any> by args
            val value = player.world.getGameRuleValue(gamerule)

            player.sendText {
                appendPrefix()
                info("Die Spielregel ")
                translatable(gamerule.translationKey()).colorIfAbsent(Colors.VARIABLE_VALUE)
                info(" ist aktuell auf ")
                variableValue(value.toString())
                info(" für die Welt ")
                variableValue(player.world.name)
                info(" gesetzt.")
            }
        }

        stringArgument("value") {
            anyExecutor { executor, args ->
                val value: String by args
                val gamerule: GameRule<Any> by args

                val parsedValue = when (gamerule.type) {
                    Integer::class.java -> value.toIntOrNull()
                    Boolean::class.java -> value.toBooleanStrictOrNull()
                    else -> null
                } ?: run {
                    executor.sendText {
                        appendPrefix()
                        error("Bitte gebe einen gültigen Wert für die Spielregel an.")
                    }
                    return@anyExecutor
                }

                executor.server.worlds.forEach { it.setGameRule(gamerule, parsedValue) }

                executor.sendText {
                    appendPrefix()
                    info("Die Spielregel ")
                    translatable(gamerule.translationKey()).colorIfAbsent(Colors.VARIABLE_VALUE)
                    info(" wurde auf ")
                    variableValue(parsedValue.toString())
                    info(" für alle Welten gesetzt.")
                }
            }

            worldArgument("world") {
                anyExecutor { executor, args ->
                    val value: String by args
                    val gamerule: GameRule<Any> by args
                    val world: World by args

                    val parsedValue = when (gamerule.type) {
                        Integer::class.java -> value.toIntOrNull()
                        Boolean::class.java -> value.toBooleanStrictOrNull()
                        else -> null
                    } ?: run {
                        executor.sendText {
                            appendPrefix()
                            error("Bitte gebe einen gültigen Wert für die Spielregel an.")
                        }
                        return@anyExecutor
                    }

                    world.setGameRule(gamerule, parsedValue)

                    executor.sendText {
                        appendPrefix()
                        info("Die Spielregel ")
                        translatable(gamerule.translationKey()).colorIfAbsent(Colors.VARIABLE_VALUE)
                        info(" wurde auf ")
                        variableValue(parsedValue.toString())
                        info(" für die Welt ")
                        variableValue(world.name)
                        info(" gesetzt.")
                    }
                }
            }
        }
    }
}