package dev.slne.surf.essentials.service

import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import org.bukkit.Location
import java.util.*

class LastLocationService {
    private val _latestLocations = mutableObject2ObjectMapOf<UUID, Location>()

    fun getLatestLocation(uuid: UUID) = _latestLocations[uuid]
    fun setLatestLocation(uuid: UUID, location: Location) {
        _latestLocations[uuid] = location
    }

    companion object {
        val INSTANCE = LastLocationService()
    }
}

val lastLocationService get() = LastLocationService.INSTANCE