package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.command.argument.durationArgument
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.ticks
import dev.slne.surf.essentials.util.translatable
import dev.slne.surf.essentials.util.userContent
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.time.Duration

fun effectCommand() = commandTree("effect") {
    withPermission(EssentialsPermissionRegistry.EFFECT_COMMAND)

    literalArgument("list") {
        playerExecutor { player, _ ->
            if (player.activePotionEffects.isEmpty()) {
                player.sendText {
                    appendPrefix()
                    error("Du hast keine aktiven Effekte.")
                }
                return@playerExecutor
            }

            player.sendText {
                appendPrefix()
                info("Du hast aktuell ")
                variableValue(player.activePotionEffects.size)
                info(" aktive Effekte: ")

                player.activePotionEffects.forEachIndexed { index, effect ->
                    translatable(effect.type.translationKey()).colorIfAbsent(Colors.VARIABLE_VALUE)

                    if (index < player.activePotionEffects.size - 1) {
                        append {
                            spacer(", ")
                        }
                    }
                }
            }
        }
    }

    literalArgument("give") {
        entitySelectorArgumentManyPlayers("players") {
            potionEffectArgument("effect") {
                durationArgument("duration") {
                    integerArgument("amplifier", optional = true) {
                        booleanArgument("hideParticles", optional = true) {
                            anyExecutor { executor, args ->
                                val players: Collection<Player> by args
                                val effect: PotionEffectType by args
                                val duration: Duration by args
                                val amplifier: Int? by args
                                val hideParticles: Boolean? by args

                                players.forEach {
                                    it.addPotionEffect(
                                        PotionEffect(
                                            effect,
                                            if (duration.isNegative) PotionEffect.INFINITE_DURATION else duration.toMillis()
                                                .ticks(),
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
                                    variableValue(duration.userContent())
                                    success(" an ")
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
        playerExecutor { player, _ ->
            if (player.activePotionEffects.isEmpty()) {
                player.sendText {
                    appendPrefix()
                    error("Du hast keine aktiven Effekte.")
                }
                return@playerExecutor
            }

            player.clearActivePotionEffects()

            player.sendText {
                appendPrefix()
                success("Du hast alle deine Effekte entfernt.")
            }
        }

        entitySelectorArgumentManyPlayers("players") {
            anyExecutor { executor, args ->
                val players: Collection<Player> by args

                players.forEach {
                    it.clearActivePotionEffects()
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