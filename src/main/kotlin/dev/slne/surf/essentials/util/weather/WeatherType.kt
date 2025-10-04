package dev.slne.surf.essentials.util.weather

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
        setWeather(world, durationInTicks)
    }
}
