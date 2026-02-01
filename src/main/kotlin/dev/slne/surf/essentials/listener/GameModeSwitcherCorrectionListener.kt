package dev.slne.surf.essentials.listener

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent


/**
 * As of Minecraft 1.21.6, this is no longer possible. There is a PR to Paper to reintroduce this functionality.
 *
 * @see [Paper Issue #13489](https://github.com/PaperMC/Paper/issues/13489)
 * @see [Paper PR #13507](https://github.com/PaperMC/Paper/pull/13507)
 */

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