package dev.slne.surf.essentials.command.minecraft

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.command.argument.namedTimeArgument
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.permission.NamedTime
import dev.slne.surf.surfapi.bukkit.api.surfBukkitApi
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit

fun timeCommand() = commandTree("time") {
    withPermission(EssentialsPermissionRegistry.TIME_COMMAND)
    literalArgument("query") {
        nativeExecutor { executor, _ ->
            val time = executor.world.fullTime / 24000L % Int.MAX_VALUE

            executor.sendText {
                appendPrefix()
                info("Die")
                appendSpace()
                variableValue("Zeit")
                appendSpace()
                info("in der Welt")
                appendSpace()
                variableValue(executor.world.name)
                appendSpace()
                info("beträgt")
                appendSpace()
                variableValue("$time Tage!")
            }
        }

        literalArgument("day") {
            nativeExecutor { executor, _ ->
                val time = executor.world.fullTime / 24000L % Int.MAX_VALUE

                executor.sendText {
                    appendPrefix()
                    info("Die")
                    appendSpace()
                    variableValue("Zeit")
                    appendSpace()
                    info("in der Welt")
                    appendSpace()
                    variableValue(executor.world.name)
                    appendSpace()
                    info("beträgt")
                    appendSpace()
                    variableValue("$time Tage!")
                }
            }
        }

        literalArgument("daytime") {
            nativeExecutor { executor, _ ->
                val time = executor.world.fullTime % 24000L

                executor.sendText {
                    appendPrefix()
                    info("Die")
                    appendSpace()
                    variableValue("Tageszeit")
                    appendSpace()
                    info("in der Welt")
                    appendSpace()
                    variableValue(executor.world.name)
                    appendSpace()
                    info("beträgt")
                    appendSpace()
                    variableValue("$time Ticks!")
                }
            }
        }

        literalArgument("gametime") {
            nativeExecutor { executor, _ ->
                val time = executor.world.gameTime

                executor.sendText {
                    appendPrefix()
                    info("Die")
                    appendSpace()
                    variableValue("Spielzeit")
                    appendSpace()
                    info("in der Welt")
                    appendSpace()
                    variableValue(executor.world.name)
                    appendSpace()
                    info("beträgt")
                    appendSpace()
                    variableValue("$time Ticks!")
                }
            }
        }
    }

    literalArgument("set") {
        timeArgument("time") {
            nativeExecutor { executor, args ->
                val time: Int by args

                Bukkit.getWorlds().forEach {
                    it.fullTime = time.toLong()
                }

                executor.sendText {
                    appendPrefix()
                    success("Die Zeit wurde auf")
                    appendSpace()
                    variableValue("$time Ticks")
                    appendSpace()
                    success("gesetzt.")
                }
            }
        }

        namedTimeArgument("namedTime") {
            nativeExecutor { executor, args ->
                val namedTime: NamedTime by args

                Bukkit.getWorlds().forEach { world ->
                    val current = world.fullTime % 24000
                    val target = namedTime.ticks % 24000

                    val diff = if (target >= current) {
                        target - current
                    } else {
                        (24000 - current) + target
                    }

                    world.fullTime += diff
                }


                executor.sendText {
                    appendPrefix()
                    success("Die Zeit wurde auf")
                    appendSpace()
                    variableValue(namedTime.timeName)
                    appendSpace()
                    success("gesetzt.")
                }
            }
        }
    }

    literalArgument("add") {
        timeArgument("time") {
            nativeExecutor { executor, args ->
                val time: Int by args

                Bukkit.getWorlds().forEach {
                    if (time !in 100..24000) {
                        it.fullTime += time
                    } else {
                        plugin.launch {
                            surfBukkitApi.skipTimeSmoothly(it, time.toLong())
                        }
                    }
                }

                executor.sendText {
                    appendPrefix()
                    success("Die Zeit wurde um")
                    appendSpace()
                    variableValue("$time Ticks")
                    appendSpace()
                    success("erweitert.")
                }
            }
        }
    }
}