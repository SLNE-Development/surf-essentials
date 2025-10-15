package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.essentials.command.argument.durationArgument
import dev.slne.surf.essentials.command.argument.greedyRestartReasonArgument
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.util.userContent
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.sound
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.Sound
import java.time.Duration
import java.util.concurrent.TimeUnit

private var restartTask: ScheduledTask? = null

fun restartCommand() = commandTree("restart") {
    withPermission(EssentialsPermissionRegistry.RESTART_COMMAND)

    literalArgument("stop") {
        anyExecutor { executor, _ ->
            if (restartTask == null) {
                executor.sendText {
                    appendPrefix()
                    error("Es läuft kein geplanter Neustart.")
                }
            }

            restartTask?.cancel()
            restartTask = null

            executor.sendText {
                appendPrefix()
                success("Der geplante Neustart wurde abgebrochen.")
            }

            Bukkit.broadcast(buildText {
                appendPrefix()
                error("Der geplante Neustart wurde abgebrochen.")
            }, EssentialsPermissionRegistry.RESTART_NOTIFY)
        }
    }

    anyExecutor { executor, _ ->
        executor.sendText {
            appendPrefix()
            success("Du startest den Server neu...")
        }

        Bukkit.broadcast(buildText {
            appendPrefix()
            variableValue(executor.name)
            success(" startet den Server neu...")
        }, EssentialsPermissionRegistry.RESTART_NOTIFY)

        Bukkit.shutdown()
    }

    durationArgument("delay") {
        anyExecutor { executor, args ->
            val delay: Duration by args
            val seconds = delay.seconds

            if (seconds < 1) {
                executor.sendText {
                    appendPrefix()
                    error("Die Verzögerung muss mindestens 1 Sekunde betragen.")
                }
                return@anyExecutor
            }
            var remaining = Duration.ofSeconds(seconds)

            restartTask?.cancel()
            restartTask = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
                if (remaining.seconds <= 0) {
                    Bukkit.broadcast(buildText {
                        appendPrefix()
                        success("Der Server wird ")
                        variableValue("jetzt")
                        success(" neu gestartet...")
                    })

                    Bukkit.shutdown()
                    return@runAtFixedRate
                }

                if (shouldNotify(remaining, delay)) {
                    forEachPlayer {
                        it.sendText {
                            appendPrefix()
                            success("Der Server wird in ")
                            variableValue(remaining.userContent())
                            success(" neu gestartet...")
                        }

                        it.playSound(sound {
                            type(Sound.BLOCK_NOTE_BLOCK_PLING)
                        }, net.kyori.adventure.sound.Sound.Emitter.self())
                    }
                }

                forEachPlayer {
                    it.sendActionBar(buildText {
                        success("Neustart in ")
                        variableValue(remaining.userContent())
                    })
                }

                remaining = remaining.minusSeconds(1)
            }, 0L, 1L, TimeUnit.SECONDS)
        }

        greedyRestartReasonArgument("reason") {
            anyExecutor { executor, args ->
                val delay: Duration by args
                val reason: String by args
                val seconds = delay.seconds

                if (seconds < 1) {
                    executor.sendText {
                        appendPrefix()
                        error("Die Verzögerung muss mindestens 1 Sekunde betragen.")
                    }
                    return@anyExecutor
                }

                var remaining = Duration.ofSeconds(seconds)
                restartTask?.cancel()
                restartTask = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
                    if (remaining.seconds <= 0) {
                        Bukkit.broadcast(buildText {
                            appendPrefix()
                            success("Der Server wird ")
                            variableValue("jetzt")
                            success(" neu gestartet...")
                        })
                        Bukkit.shutdown()
                        return@runAtFixedRate
                    }

                    if (shouldNotify(remaining, delay)) {
                        forEachPlayer {
                            it.sendText {
                                appendPrefix()
                                success("Der Server wird in ")
                                variableValue(remaining.userContent())
                                success(" neu gestartet...")
                                appendNewPrefixedLine {
                                    spacer("Grund: ")
                                    info(reason.toSmallCaps())
                                }
                            }

                            it.playSound(sound {
                                type(Sound.BLOCK_NOTE_BLOCK_PLING)
                            }, net.kyori.adventure.sound.Sound.Emitter.self())
                        }
                    }

                    forEachPlayer {
                        it.sendActionBar(buildText {
                            success("Neustart in ")
                            variableValue(remaining.userContent())
                        })
                    }

                    remaining = remaining.minusSeconds(1)
                }, 0L, 1L, TimeUnit.SECONDS)
            }
        }
    }
}

private fun shouldNotify(remaining: Duration, total: Duration): Boolean {
    if (remaining.isZero || remaining.isNegative) {
        return false
    }

    if (remaining.seconds == total.seconds) {
        return true
    }

    val remainingSeconds = remaining.seconds
    val totalSeconds = total.seconds

    val thresholds = mutableSetOf<Long>()

    var current = totalSeconds
    while (current > 600) {
        current /= 2
        thresholds.add(current)
    }

    val finerSteps = listOf(300L, 180L, 120L, 60L, 30L, 15L, 10L, 5L, 3L, 2L, 1L)
    thresholds.addAll(finerSteps.filter { it < totalSeconds })

    return remainingSeconds in thresholds
}