package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.command.argument.soundSourceArgument
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.sound
import org.bukkit.Location
import org.bukkit.entity.Player
import net.kyori.adventure.sound.Sound as AdventureSound
import org.bukkit.Sound as BukkitSound

fun soundCommand() = commandTree("sound") {
    withPermission(EssentialsPermissionRegistry.SOUND_COMMAND)

    literalArgument("stop") {
        playerExecutor { player, _ ->
            player.stopAllSounds()
            player.sendText {
                appendPrefix()
                success("Alle Sounds wurden gestoppt.")
            }
        }

        entitySelectorArgumentManyPlayers("targets") {
            anyExecutor { executor, args ->
                val targets: Collection<Player> by args

                for (target in targets) {
                    target.stopAllSounds()
                }

                executor.sendText {
                    appendPrefix()
                    success("Die Sounds von ")

                    if (targets.size == 1) {
                        variableValue(targets.firstOrNull()?.name ?: "Unbekannt")
                    } else {
                        variableValue("${targets.size} Spielern")
                    }

                    success(" wurden gestoppt.")
                }
            }
        }
    }

    literalArgument("play") {
        soundArgument("sound") {
            playerExecutor { player, args ->
                val sound: BukkitSound by args
                player.playSound(sound {
                    type(sound)
                }, AdventureSound.Emitter.self())
            }

            soundSourceArgument("source") {
                playerExecutor { player, args ->
                    val sound: BukkitSound by args
                    val source: AdventureSound.Source by args
                    player.playSound(sound {
                        source(source)
                        type(sound)
                    }, AdventureSound.Emitter.self())
                }
                entitySelectorArgumentManyPlayers("targets") {
                    anyExecutor { executor, args ->
                        val sound: BukkitSound by args
                        val source: AdventureSound.Source by args
                        val targets: Collection<Player> by args

                        for (target in targets) {
                            target.playSound(sound {
                                source(source)
                                type(sound)
                            }, AdventureSound.Emitter.self())
                        }

                        executor.sendText {
                            appendPrefix()
                            success("Der Sound wurde für ")

                            if (targets.size == 1) {
                                variableValue(targets.firstOrNull()?.name ?: "Unbekannt")
                            } else {
                                variableValue("${targets.size} Spieler")
                            }

                            success(" abgespielt.")
                        }
                    }

                    locationArgument("location") {
                        anyExecutor { executor, args ->
                            val sound: BukkitSound by args
                            val source: AdventureSound.Source by args
                            val targets: Collection<Player> by args
                            val location: Location by args

                            for (target in targets) {
                                target.playSound(sound {
                                    source(source)
                                    type(sound)
                                }, location.x, location.y, location.z)
                            }

                            executor.sendText {
                                appendPrefix()
                                success("Der Sound wurde für ")

                                if (targets.size == 1) {
                                    variableValue(targets.firstOrNull()?.name ?: "Unbekannt")
                                } else {
                                    variableValue("${targets.size} Spieler")
                                }

                                success(" abgespielt.")
                            }
                        }
                        floatArgument("volume") {
                            floatArgument("pitch") {
                                anyExecutor { executor, args ->
                                    val sound: BukkitSound by args
                                    val source: AdventureSound.Source by args
                                    val targets: Collection<Player> by args
                                    val location: Location by args
                                    val volume: Float by args
                                    val pitch: Float by args

                                    for (target in targets) {
                                        target.playSound(sound {
                                            source(source)
                                            type(sound)
                                            volume(volume)
                                            pitch(pitch)
                                        }, location.x, location.y, location.z)
                                    }

                                    executor.sendText {
                                        appendPrefix()
                                        success("Der Sound wurde für ")

                                        if (targets.size == 1) {
                                            variableValue(
                                                targets.firstOrNull()?.name ?: "Unbekannt"
                                            )
                                        } else {
                                            variableValue("${targets.size} Spieler")
                                        }

                                        success(" abgespielt.")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}