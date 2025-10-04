package dev.slne.surf.essentials.util.weather

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.essentials.plugin
import org.bukkit.World

enum class WeatherType(
    val displayName: String,
    private val setWeather: (world: World, duration: Int) -> Unit
) {
    CLEAR("Klar", { world, duration ->
        world.isThundering = false
        world.setStorm(false)
        world.clearWeatherDuration = duration
    }),
    RAIN("Regen", { world, duration ->
        world.isThundering = false
        world.setStorm(true)
        world.weatherDuration = duration
    }),
    THUNDER("Gewitter", { world, duration ->
        world.setStorm(true)
        world.isThundering = true
        world.thunderDuration = duration
    });

    fun setWeather(world: World, durationInTicks: Int) {
        plugin.launch(plugin.globalRegionDispatcher) {
            setWeather.invoke(world, durationInTicks)
        }
    }
}


fun World.getWeatherType(): WeatherType =
    when {
        isThundering -> WeatherType.THUNDER
        hasStorm() -> WeatherType.RAIN
        else -> WeatherType.CLEAR
    }
