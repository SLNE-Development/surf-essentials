package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.slne.surf.essentials.command.argument.durationArgument
import dev.slne.surf.essentials.command.argument.greedyRestartReasonArgument
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.util.userContent
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.sound
import org.bukkit.Bukkit
import org.bukkit.Sound
import java.time.Duration
import java.util.concurrent.TimeUnit

fun restartCommand() = commandTree("restart") {
    withPermission(EssentialsPermissionRegistry.RESTART_COMMAND)
    anyExecutor { executor, _ ->
        executor.sendText {
            appendPrefix()
            success("Der Server wird neu gestartet...")
        }

        Bukkit.broadcast(buildText {
            appendPrefix()
            success("Der Server wird neu gestartet...")
        }, EssentialsPermissionRegistry.RESTART_NOTIFY)

        Bukkit.restart()
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

            executor.sendText {
                appendPrefix()
                success("Der Server wird in ")
                variableValue(delay.userContent())
                success(" neu gestartet...")
            }

            var remaining = Duration.ofSeconds(seconds)

            Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
                if (remaining.seconds <= 0) {
                    Bukkit.broadcast(buildText {
                        appendPrefix()
                        success("Der Server wird ")
                        variableValue("jetzt")
                        success(" neu gestartet...")
                    })
                    return@runAtFixedRate
                }

                val shouldNotify = when {
                    remaining.seconds > 30 -> remaining.seconds.toInt() % 30 == 0
                    remaining.seconds > 10 -> remaining.seconds.toInt() % 10 == 0
                    remaining.seconds > 5 -> remaining.seconds.toInt() % 5 == 0
                    else -> true
                }

                if (shouldNotify) {
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

                executor.sendText {
                    appendPrefix()
                    success("Der Server wird in ")
                    variableValue(delay.userContent())
                    success(" neu gestartet...")
                    appendNewline {
                        success(reason)
                    }
                }

                var remaining = Duration.ofSeconds(seconds)

                Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
                    if (remaining.seconds <= 0) {
                        Bukkit.broadcast(buildText {
                            appendPrefix()
                            success("Der Server wird ")
                            variableValue("jetzt")
                            success(" neu gestartet...")
                        })
                        return@runAtFixedRate
                    }

                    val shouldNotify = when {
                        remaining.seconds > 30 -> remaining.seconds.toInt() % 30 == 0
                        remaining.seconds > 10 -> remaining.seconds.toInt() % 10 == 0
                        remaining.seconds > 5 -> remaining.seconds.toInt() % 5 == 0
                        else -> true
                    }

                    if (shouldNotify) {
                        forEachPlayer {
                            it.sendText {
                                appendPrefix()
                                success("Der Server wird in ")
                                variableValue(remaining.userContent())
                                success(" neu gestartet...")
                                appendNewline {
                                    success(reason)
                                }
                            }

                            it.playSound(sound {
                                type(Sound.BLOCK_NOTE_BLOCK_PLING)
                            }, net.kyori.adventure.sound.Sound.Emitter.self())
                        }
                    }

                    remaining = remaining.minusSeconds(1)
                }, 0L, 1L, TimeUnit.SECONDS)
            }
        }
    }
}