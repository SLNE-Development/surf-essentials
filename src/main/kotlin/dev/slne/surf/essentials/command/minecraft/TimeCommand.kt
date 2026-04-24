package dev.slne.surf.essentials.command.minecraft

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.SurfApiPaper
import dev.slne.surf.essentials.command.argument.namedTimeArgument
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.time.NamedTime
import org.bukkit.Bukkit

fun timeCommand() = commandTree("time") {
    withPermission(EssentialsPermissionRegistry.TIME_COMMAND)
    literalArgument("query") {
        nativeExecutor { executor, _ ->
            plugin.launch(plugin.globalRegionDispatcher) {
                val time = executor.world.fullTime / 24000L % Int.MAX_VALUE

                executor.sendText {
                    appendInfoPrefix()
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

        literalArgument("day") {
            nativeExecutor { executor, _ ->
                plugin.launch(plugin.globalRegionDispatcher) {
                    val time = executor.world.fullTime / 24000L % Int.MAX_VALUE

                    executor.sendText {
                        appendInfoPrefix()
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
        }

        literalArgument("daytime") {
            nativeExecutor { executor, _ ->
                plugin.launch(plugin.globalRegionDispatcher) {
                    val time = executor.world.fullTime % 24000L

                    executor.sendText {
                        appendInfoPrefix()
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
        }

        literalArgument("gametime") {
            nativeExecutor { executor, _ ->
                plugin.launch(plugin.globalRegionDispatcher) {
                    val time = executor.world.gameTime

                    executor.sendText {
                        appendInfoPrefix()
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
    }

    literalArgument("set") {
        timeArgument("time") {
            anyExecutor { sender, args ->
                val time: Int by args

                plugin.launch(plugin.globalRegionDispatcher) {
                    Bukkit.getWorlds().forEach {
                        it.fullTime = time.toLong()
                    }
                }

                sender.sendText {
                    appendSuccessPrefix()
                    success("Die Zeit wurde auf")
                    appendSpace()
                    variableValue("$time Ticks")
                    appendSpace()
                    success("gesetzt.")
                }
            }
        }

        namedTimeArgument("namedTime") {
            anyExecutor { executor, args ->
                val namedTime: NamedTime by args

                plugin.launch(plugin.globalRegionDispatcher) {
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
                }

                executor.sendText {
                    appendSuccessPrefix()
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
            anyExecutor { executor, args ->
                val time: Int by args

                plugin.launch(plugin.globalRegionDispatcher) {
                    Bukkit.getWorlds().forEach {
                        if (time !in 100..24000) {
                            it.fullTime += time
                        } else {
                            SurfApiPaper.skipTimeSmoothly(it, time.toLong())
                        }
                    }
                }

                executor.sendText {
                    appendSuccessPrefix()
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