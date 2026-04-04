package dev.slne.surf.essentials.listener

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerJoinEvent


/**
 * As of Minecraft 1.21.6, this is no longer possible. There is a PR to Paper to reintroduce this functionality.
 *
 * @see [Paper Issue #13489](https://github.com/PaperMC/Paper/issues/13489)
 * @see [Paper PR #13507](https://github.com/PaperMC/Paper/pull/13507)
 */

object GameModeSwitcherCorrectionListener : Listener {
    @EventHandler
    fun onGameModeChange(event: PlayerGameModeChangeEvent) {
        if (event.cause != PlayerGameModeChangeEvent.Cause.GAMEMODE_SWITCHER) {
            return
        }

        if (
            !event.player.hasPermission(EssentialsPermissionRegistry.GAME_MODE_COMMAND + "." + event.newGameMode.name.lowercase())
            && !event.player.hasPermission(EssentialsPermissionRegistry.GAME_MODE_COMMAND + ".*")
        ) {
            event.player.sendText {
                appendErrorPrefix()
                error("Du hast keine Berechtigung, in den Spielmodus ")
                translatable(event.newGameMode.translationKey())
                error(" zu wechseln!")
            }
            event.isCancelled = true
            return
        }

        event.player.sendText {
            appendSuccessPrefix()
            success("Du hast den Spielmodus zu ")
            translatable(event.newGameMode.translationKey())
            success(" gewechselt.")
        }
    }

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