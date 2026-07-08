package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.util.BukkitSound
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import org.bukkit.Registry

fun testSoundCommand() = commandTree("testsound") {
    withPermission(EssentialsPermissionRegistry.TEST_SOUND_COMMAND)

    soundArgument("sound") {
        playerExecutor { player, arguments ->
            val sound: BukkitSound by arguments

            player.playSound(true) {
                type(sound)
            }

            player.sendText {
                appendSuccessPrefix()
                success("Der Sound ")
                variableValue(Registry.SOUNDS.getKey(sound)?.key.toString())
                success(" wurde erfolgreich abgespielt.")
            }
        }
        floatArgument("pitch") {
            playerExecutor { player, arguments ->
                val sound: BukkitSound by arguments
                val pitch: Float by arguments

                player.playSound(true) {
                    type(sound)
                    pitch(pitch)
                }

                player.sendText {
                    appendSuccessPrefix()
                    success("Der Sound ")
                    variableValue(Registry.SOUNDS.getKey(sound)?.key.toString())
                    success(" wurde erfolgreich abgespielt.")
                }
            }

            floatArgument("volume") {
                playerExecutor { player, arguments ->
                    val sound: BukkitSound by arguments
                    val pitch: Float by arguments
                    val volume: Float? by arguments

                    player.playSound(true) {
                        type(sound)
                        pitch(pitch)
                        volume?.let {
                            volume(it)
                        }
                    }

                    player.sendText {
                        appendSuccessPrefix()
                        success("Der Sound ")
                        variableValue(Registry.SOUNDS.getKey(sound)?.key.toString())
                        success(" wurde erfolgreich abgespielt.")
                    }
                }
            }
        }
    }
}