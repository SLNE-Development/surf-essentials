package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.essentials.command.argument.durationArgument
import dev.slne.surf.essentials.command.argument.weatherTypeArgument
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.util.ticks
import dev.slne.surf.essentials.util.util.userContent
import dev.slne.surf.essentials.util.weather.WeatherType
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import java.time.Duration

fun weatherCommand() = commandTree("weather") {
    withPermission(EssentialsPermissionRegistry.WEATHER_COMMAND)
    weatherTypeArgument("weather") {
        playerExecutor { player, args ->
            val weather: WeatherType by args

            weather.setWeather(player.world, 6000)
            player.sendText {
                appendPrefix()
                success("Das Wetter wurde zu ")
                variableValue(weather.name)
                success(" geändert.")
            }
        }

        durationArgument("duration") {
            playerExecutor { player, args ->
                val weather: WeatherType by args
                val duration: Duration by args

                weather.setWeather(player.world, duration.toMillis().ticks())
                player.sendText {
                    appendPrefix()
                    success("Das Wetter wurde zu ")
                    variableValue(weather.name)
                    success(" für ")
                    variableValue(duration.userContent())
                    success(" geändert.")
                }
            }
        }
    }
}