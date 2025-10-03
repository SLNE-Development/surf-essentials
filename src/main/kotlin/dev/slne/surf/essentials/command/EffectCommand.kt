package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.command.argument.durationArgument
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.translatable
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

fun effectCommand() = commandAPICommand("effect") {
    withPermission(EssentialsPermissionRegistry.EFFECT_COMMAND)
    literalArgument("give") {
        entitySelectorArgumentManyPlayers("players") {
            potionEffectArgument("effect") {
                durationArgument("duration") {
                    integerArgument("amplifier", optional = true) {
                        booleanArgument("hideParticles", optional = true) {
                            anyExecutor { executor, args ->
                                val players: Collection<Player> by args
                                val effect: PotionEffectType by args
                                val duration: Int by args
                                val amplifier: Int? by args
                                val hideParticles: Boolean? by args

                                players.forEach {
                                    it.addPotionEffect(
                                        PotionEffect(
                                            effect,
                                            duration,
                                            amplifier ?: 1,
                                            false,
                                            hideParticles ?: true
                                        )
                                    )
                                }

                                executor.sendText {
                                    appendPrefix()
                                    success("Du hast ")
                                    translatable(effect.translationKey()).colorIfAbsent(Colors.VARIABLE_VALUE)
                                    success(" für ")
                                    variableValue(duration.toString())
                                    success(" Sekunden an ")
                                    variableValue("${players.size} Spielern")
                                    success(" vergeben.")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    literalArgument("clear") {
        entitySelectorArgumentManyPlayers("players") {
            anyExecutor { executor, args ->
                val players: Collection<Player> by args

                players.forEach {
                    it.activePotionEffects.forEach { effect -> it.removePotionEffect(effect.type) }
                }

                executor.sendText {
                    appendPrefix()
                    success("Du hast alle Effekte von ")
                    variableValue("${players.size} Spielern")
                    success(" entfernt.")
                }
            }

            potionEffectArgument("effect") {
                anyExecutor { executor, args ->
                    val players: Collection<Player> by args
                    val effect: PotionEffectType by args

                    players.forEach {
                        it.removePotionEffect(effect)
                    }

                    executor.sendText {
                        appendPrefix()
                        success("Du hast den Effekt ")
                        translatable(effect.translationKey()).colorIfAbsent(Colors.VARIABLE_VALUE)
                        success(" von ")
                        variableValue("${players.size} Spielern")
                        success(" entfernt.")
                    }
                }
            }
        }
    }
}