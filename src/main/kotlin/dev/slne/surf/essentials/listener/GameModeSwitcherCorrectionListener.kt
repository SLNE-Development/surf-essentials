package dev.slne.surf.essentials.listener

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent

object GameModeSwitcherCorrectionListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (event.player.hasPermission(EssentialsPermissionRegistry.GAME_MODE_SWITCHER)) {
            fixGameModeSwitcher(event.player)
        }
    }

    @EventHandler
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        if (event.player.hasPermission(EssentialsPermissionRegistry.GAME_MODE_SWITCHER)) {
            fixGameModeSwitcher(event.player)
        }
    }

    @EventHandler
    fun afterRespawn(event: PlayerPostRespawnEvent) {
        if (event.player.hasPermission(EssentialsPermissionRegistry.GAME_MODE_SWITCHER)) {
            fixGameModeSwitcher(event.player)
        }
    }

    private fun fixGameModeSwitcher(player: Player) {
        player.sendOpLevel(2.toByte())
    }
}