package dev.slne.surf.essentials.listener

import dev.slne.surf.essentials.plugin
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object SpectatorFlyListener : Listener {
    private val allowFlightPlayers = ConcurrentHashMap.newKeySet<UUID>()
    private val flyingPlayers = ConcurrentHashMap.newKeySet<UUID>()

    @EventHandler
    fun onGameModeChange(event: PlayerGameModeChangeEvent) {
        val player = event.player
        val previousGameMode = player.gameMode
        val newGameMode = event.newGameMode

        val isOldFlightMode =
            previousGameMode == GameMode.SPECTATOR || previousGameMode == GameMode.CREATIVE
        val isNewFlightMode = newGameMode == GameMode.SPECTATOR || newGameMode == GameMode.CREATIVE

        if (!isOldFlightMode && isNewFlightMode) {
            if (player.allowFlight) {
                allowFlightPlayers.add(player.uniqueId)
                if (player.isFlying) {
                    flyingPlayers.add(player.uniqueId)
                }
            }
            return
        }

        if (isOldFlightMode && !isNewFlightMode) {
            val hadAllowFlight = allowFlightPlayers.remove(player.uniqueId)
            val wasFlying = flyingPlayers.remove(player.uniqueId)

            player.scheduler.runDelayed(plugin, {
                if (player.isOnline) {
                    player.allowFlight = hadAllowFlight
                    player.isFlying = wasFlying && hadAllowFlight
                }
            }, null, 1L)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        allowFlightPlayers.remove(event.player.uniqueId)
        flyingPlayers.remove(event.player.uniqueId)
    }
}