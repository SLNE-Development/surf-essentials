package dev.slne.surf.essentials.command.minecraft

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.command.argument.durationArgument
import dev.slne.surf.essentials.command.argument.weatherTypeArgument
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.util.ticks
import dev.slne.surf.essentials.util.util.userContent
import dev.slne.surf.essentials.util.weather.WeatherType
import dev.slne.surf.essentials.util.weather.getWeatherType
import org.bukkit.Bukkit
import java.time.Duration

fun weatherCommand() = commandTree("weather") {
    withPermission(EssentialsPermissionRegistry.WEATHER_COMMAND)

    literalArgument("query") {
        playerExecutor { player, _ ->
            player.sendText {
                appendInfoPrefix()
                info("In der Welt ")
                variableValue(player.world.name)
                info(" ist das Wetter aktuell ")
                variableValue(player.world.getWeatherType().displayName)
                info(".")
            }
        }
    }

    weatherTypeArgument("weather") {
        anyExecutor { sender, arguments ->
            val weather: WeatherType by arguments

            plugin.launch(plugin.globalRegionDispatcher) {
                Bukkit.getWorlds().forEach {
                    weather.setWeather(it, 6000)
                }
            }

            sender.sendText {
                appendSuccessPrefix()
                success("Das Wetter in allen Welten wurde zu ")
                variableValue(weather.displayName)
                success(" geändert.")
            }
        }

        durationArgument("duration") {
            anyExecutor { sender, arguments ->
                val weather: WeatherType by arguments
                val duration: Duration by arguments

                plugin.launch(plugin.globalRegionDispatcher) {
                    Bukkit.getWorlds().forEach {
                        weather.setWeather(it, duration.toMillis().ticks())
                    }
                }

                sender.sendText {
                    appendSuccessPrefix()
                    success("Das Wetter in allen Welten wurde zu ")
                    variableValue(weather.displayName)
                    success(" für ")
                    variableValue(duration.userContent())
                    success(" geändert.")
                }
            }
        }
    }
}